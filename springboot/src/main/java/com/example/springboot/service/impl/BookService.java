package com.example.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Category;
import com.example.springboot.entity.Return_;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.CategoryMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IBookService;
import com.example.springboot.service.ICategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService implements IBookService {
    @Autowired
    BookMapper bookMapper;

    @Autowired
    BorrowMapper borrowMapper;

    @Autowired
    UserMapper userMapper;


    @Override
    public List<Book> list(){
        return bookMapper.list();
    }

    @Override
    public PageInfo<Book> page(BookPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(),pageRequest.getPageSize());
        return new PageInfo<>(bookMapper.listByCondition(pageRequest));
    }
    @Override
    public void save(Book obj) {
        try {
            obj.setCategory(category(obj.getCategories()));
            obj.setUpdatetime(new Date());
            bookMapper.save(obj);
        } catch (Exception e) {
            throw new ServiceException("数据插入错误",e);
        }

    }

    @Override
    public Book getById(Integer id) {
        return bookMapper.getById(id);
    }

    @Override
    public void update(Book obj) {
        try {
            obj.setCategory(category(obj.getCategories()));
            obj.setUpdatetime(new Date());
            bookMapper.updateById(obj);
        } catch (Exception e) {
            throw  new ServiceException("数据更新错误",e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        bookMapper.deleteById(id);
    }

    private String category(List<String> categories){
         StringBuilder  sb= new StringBuilder();
         if(CollUtil.isNotEmpty(categories)){
             categories.forEach(v -> sb.append(v).append(" > "));
             return sb.substring(0,sb.lastIndexOf(" > "));
         }
        return sb.toString();
    }

    /**
     * 基于用户借阅历史的图书推荐算法
     * 改进版：结合借书记录和还书记录，添加时间权重，随机打乱结果
     * @param userId 用户ID
     * @return 推荐图书列表
     */
    @Override
    @org.springframework.cache.annotation.Cacheable(value = "recommend", key = "#userId")
    public List<Book> recommendBooks(Integer userId) {
        try {
            // 1. 根据用户ID获取用户信息
            User user = userMapper.getById(userId);
            if (user == null) {
                log.warn("用户{}不存在，返回热门图书", userId);
                return getDynamicPopularBooks();
            }
            
            // 2. 获取用户完整借阅历史（借书记录 + 还书记录）
            com.example.springboot.controller.request.BorrowPageRequest borrowRequest = 
                new com.example.springboot.controller.request.BorrowPageRequest();
            borrowRequest.setUserName(user.getUsername());
            List<Borrow> userBorrows = borrowMapper.listByCondition(borrowRequest);
            List<Return_> userReturns = borrowMapper.listReturByCondition(borrowRequest);
            
            // 合并借阅和归还记录
            List<BookHistoryItem> allHistory = new ArrayList<>();
            
            // 添加借书记录
            if (userBorrows != null) {
                for (Borrow borrow : userBorrows) {
                    LocalDateTime borrowTime = null;
                    if (borrow.getCreatetime() != null) {
                        borrowTime = borrow.getCreatetime().atStartOfDay();
                    }
                    allHistory.add(new BookHistoryItem(borrow.getBookNo(), borrowTime));
                }
            }
            
            // 添加还书记录
            if (userReturns != null) {
                for (Return_ retur : userReturns) {
                    LocalDateTime returnTime = null;
                    if (retur.getRealDate() != null) {
                        returnTime = retur.getRealDate().atStartOfDay();
                    }
                    allHistory.add(new BookHistoryItem(retur.getBookNo(), returnTime));
                }
            }

            if (allHistory.isEmpty()) {
                // 冷启动问题：新用户，返回动态热门图书
                log.info("用户{}（{}）无借阅历史，返回动态热门图书", userId, user.getUsername());
                return getDynamicPopularBooks();
            }

            // 3. 提取用户喜欢的图书分类（带时间权重）
            Map<String, Double> weightedCategories = new HashMap<>();
            Set<String> borrowedBookNos = new HashSet<>();
            
            for (BookHistoryItem history : allHistory) {
                Book book = bookMapper.getByBookNo(history.bookNo);
                if (book != null && book.getCategory() != null) {
                    borrowedBookNos.add(history.bookNo);
                    
                    // 计算时间权重：越近的借阅权重越高
                    double timeWeight = calculateTimeWeight(history.borrowTime);
                    
                    // 将分类路径拆分，如 "计算机 > 编程 > Java"
                    String[] cats = book.getCategory().split(" > ");
                    for (String cat : cats) {
                        weightedCategories.merge(cat, timeWeight, Double::sum);
                    }
                }
            }

            if (weightedCategories.isEmpty()) {
                log.info("用户{}（{}）的借阅图书无分类信息，返回动态热门图书", userId, user.getUsername());
                return getDynamicPopularBooks();
            }

            // 4. 基于分类权重推荐相似图书（纯个性化，不考虑评分）
            List<Book> allBooks = bookMapper.list();
            
            // 为每本未借阅的图书计算分类匹配度
            List<BookWithScore> candidateBooks = new ArrayList<>();
            for (Book book : allBooks) {
                // 排除已借阅和无库存的图书
                if (borrowedBookNos.contains(book.getBookNo())) {
                    continue;
                }
                if (book.getNums() == null || book.getNums() <= 0) {
                    continue;
                }
                
                // 计算图书与用户喜好的分类匹配度
                double matchScore = 0;
                if (book.getCategory() != null) {
                    String[] bookCats = book.getCategory().split(" > ");
                    for (String cat : bookCats) {
                        // 累加该分类的权重
                        matchScore += weightedCategories.getOrDefault(cat, 0.0);
                    }
                }
                
                // 只保留有匹配度的图书
                if (matchScore > 0) {
                    candidateBooks.add(new BookWithScore(book, matchScore));
                }
            }
            
            // 按匹配度降序排序
            candidateBooks.sort((b1, b2) -> Double.compare(b2.score, b1.score));
            
            // 取前20本候选
            List<Book> recommendations = candidateBooks.stream()
                .limit(20)
                .map(bws -> bws.book)
                .collect(Collectors.toList());
            
            // 5. 随机打乱前10本推荐结果
            if (recommendations.size() > 10) {
                List<Book> top10 = recommendations.subList(0, 10);
                Collections.shuffle(top10); // 随机打乱
                recommendations = new ArrayList<>(top10);
            }

            log.info("为用户{}推荐了{}本图书（基于{}条历史记录）", userId, recommendations.size(), allHistory.size());
            return recommendations;

        } catch (Exception e) {
            log.error("为用户{}推荐图书失败", userId, e);
            // 出错时返回动态热门图书
            return getDynamicPopularBooks();
        }
    }
    
    /**
     * 计算时间权重：越近的借阅权重越高
     * @param borrowTime 借阅时间
     * @return 时间权重（0-1之间）
     */
    private double calculateTimeWeight(LocalDateTime borrowTime) {
        if (borrowTime == null) {
            return 0.5; // 默认权重
        }
        
        long daysDiff = java.time.Duration.between(borrowTime, LocalDateTime.now()).toDays();
        
        // 使用指数衰减：最近7天权重1.0，30天后降到0.5，90天后降到0.2
        if (daysDiff <= 7) {
            return 1.0;
        } else if (daysDiff <= 30) {
            return 0.8;
        } else if (daysDiff <= 90) {
            return 0.5;
        } else {
            return 0.3;
        }
    }
    
    /**
     * 图书历史记录项
     */
    private static class BookHistoryItem {
        String bookNo;
        LocalDateTime borrowTime;
        
        BookHistoryItem(String bookNo, LocalDateTime borrowTime) {
            this.bookNo = bookNo;
            this.borrowTime = borrowTime;
        }
    }
    
    /**
     * 带得分的图书记录（用于排序）
     */
    private static class BookWithScore {
        Book book;
        double score;
        
        BookWithScore(Book book, double score) {
            this.book = book;
            this.score = score;
        }
    }

    /**
     * 获取动态热门图书（基于最近30天借阅次数）
     * @return 热门图书列表
     */
    private List<Book> getDynamicPopularBooks() {
        try {
            // 查询最近30天的借阅Top10
            java.time.LocalDate end = java.time.LocalDate.now();
            java.time.LocalDate begin = end.minusDays(30);
            
            List<com.example.springboot.pojo.dto.BooksSalesDTO> top10 = 
                borrowMapper.getSalesTop10(begin.atStartOfDay(), end.atStartOfDay());
            
            if (top10 == null || top10.isEmpty()) {
                // 如果没有借阅数据，返回所有有库存的图书（随机）
                log.info("最近30天无借阅记录，返回随机图书");
                return getRandomBooks();
            }
            
            // 获取所有图书，然后根据Top10的名称匹配
            List<Book> allBooks = bookMapper.list();
            Map<String, Book> bookMap = new HashMap<>();
            for (Book book : allBooks) {
                bookMap.put(book.getName(), book);
            }
            
            // 根据Top10的bookName获取完整图书信息
            List<Book> popularBooks = new ArrayList<>();
            for (com.example.springboot.pojo.dto.BooksSalesDTO dto : top10) {
                Book book = bookMap.get(dto.getName());
                if (book != null && book.getNums() != null && book.getNums() > 0) {
                    popularBooks.add(book);
                }
            }
            
            // 如果数量不足10本，补充一些随机图书
            if (popularBooks.size() < 10) {
                Set<String> existingNos = popularBooks.stream()
                    .map(Book::getBookNo)
                    .collect(Collectors.toSet());
                
                // 随机选择其他有库存的图书
                List<Book> otherBooks = allBooks.stream()
                    .filter(book -> !existingNos.contains(book.getBookNo()))
                    .filter(book -> book.getNums() != null && book.getNums() > 0)
                    .collect(Collectors.toList());
                
                Collections.shuffle(otherBooks);
                for (Book book : otherBooks) {
                    if (popularBooks.size() >= 10) break;
                    popularBooks.add(book);
                }
            }
            
            // 随机打乱顺序
            Collections.shuffle(popularBooks);
            
            log.info("返回动态热门图书{}本", popularBooks.size());
            return popularBooks;
            
        } catch (Exception e) {
            log.error("获取动态热门图书失败，返回随机图书", e);
            return getRandomBooks();
        }
    }
    
    /**
     * 获取随机图书（用于新用户或无数据时）
     * @return 随机图书列表
     */
    private List<Book> getRandomBooks() {
        List<Book> allBooks = bookMapper.list();
        List<Book> availableBooks = allBooks.stream()
            .filter(book -> book.getNums() != null && book.getNums() > 0)
            .collect(Collectors.toList());
        
        // 随机打乱
        Collections.shuffle(availableBooks);
        
        // 返回前10本
        return availableBooks.stream()
            .limit(10)
            .collect(Collectors.toList());
    }

    /**
     * 按分类分页获取图书列表
     * @param category 分类名称（可选，为空则查询全部）
     * @param keyword 搜索关键字（ISBN或书名，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页图书列表
     */
    @Override
    public PageInfo<Book> pageByCategory(String category, String keyword, Integer pageNum, Integer pageSize) {
        // 使用PageHelper进行分页，必须在查询之前调用
        PageHelper.startPage(pageNum, pageSize);
        
        // 直接调用Mapper方法，在数据库层面进行筛选和分页
        List<Book> books = bookMapper.listByCategory(category, keyword);
        
        return new PageInfo<>(books);
    }

}
