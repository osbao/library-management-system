package com.example.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.BorrowPageRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Return_;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.mapper.po.BorrowReturCountPO;
import com.example.springboot.service.IBorrowService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Slf4j
@Service
public class BorrowService implements IBorrowService {
    @Autowired
    BorrowMapper borrowMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    BookMapper bookMapper;
    @Autowired
    com.example.springboot.mapper.BookItemMapper bookItemMapper;
    @Autowired
    com.example.springboot.mapper.ReservationMapper reservationMapper;


    @Override
    public List<Borrow> list(){
        return borrowMapper.list();
    }

    @Override
    public PageInfo<Borrow> page(BorrowPageRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<Borrow> borrows = borrowMapper.listByCondition(baseRequest);
        for(Borrow borrow : borrows){
            LocalDate returnDate = borrow.getReturnDate(); //归还的日期
            LocalDate now = LocalDate.now(); // 当前日期
            if(now.plusDays(1).isEqual(returnDate)){ // 当前日期比归还的日期小一天
                borrow.setNote("即将到期");
            }else if(now.isEqual(returnDate)){ // 当前日期比归还的日期相等
                borrow.setNote("今日到期");
            }else if(returnDate.isAfter(now)){ // 当前日期比归还的日期大
                borrow.setNote("正常");
            }else{  // 超出归还日期大于一天以上
                borrow.setNote("已过期");
            }
        }
        return new PageInfo<>(borrows);
    }

    @Override
    public PageInfo<Return_> pageReturn_(BorrowPageRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        return new PageInfo<>(borrowMapper.listReturByCondition(baseRequest));
    }

    @Transactional
    @Override
    public void save(Borrow obj) {
        // 1.校验用户积分是否足够
        String userNo  = obj.getUserNo();
        User user = userMapper.getByUsername(userNo);
        if(Objects.isNull(user)){
            throw new ServiceException("用户不存在");
        }
        // 2.校验图书数量是否足够
        Book book = bookMapper.getByNo(obj.getBookNo());
        if(Objects.isNull(book)){
            throw new ServiceException("图书不存在");
        }
        // 3.检验图书数量（支持借阅多本）
        Integer borrowNums = obj.getNums() != null ? obj.getNums() : 1; // 默认借1本
        if(book.getNums() < borrowNums){
            throw new ServiceException("图书数量不足，库存仅剩" + book.getNums() + "本");
        }
        // 4.检验用户余额（计算总积分：单本积分*天数*数量）
        Integer totalScore = book.getScore() * obj.getDays() * borrowNums; // totalScore = 借出积分*天数*数量
        Integer account = user.getAccount();
        if(totalScore > account){
            throw  new ServiceException("用户积分不够，需要" + totalScore + "积分，当前只有" + account + "积分");
        }
        
        // 5.更新余额（一次性扣除所有图书的积分）
        user.setAccount(user.getAccount()-totalScore);
        userMapper.updateById(user);
        
        // 6.更新图书数量（一次性扣除所有图书数量）
        book.setNums(book.getNums()-borrowNums);
        bookMapper.updateById(book);
        
        // 7.为每本书创建独立的借书记录（每本一条记录，nums=1）
        LocalDate returnDate = LocalDate.now().plus(obj.getDays(), ChronoUnit.DAYS);
        for (int i = 0; i < borrowNums; i++) {
            Borrow borrowRecord = new Borrow();
            borrowRecord.setBookName(obj.getBookName());
            borrowRecord.setBookNo(obj.getBookNo());
            borrowRecord.setUserNo(obj.getUserNo());
            borrowRecord.setUserName(obj.getUserName());
            borrowRecord.setUserPhone(obj.getUserPhone());
            borrowRecord.setDays(obj.getDays());
            borrowRecord.setReturnDate(returnDate);
            borrowRecord.setScore(book.getScore() * obj.getDays()); // 单本积分
            borrowRecord.setNums(1); // 每本书记录数量为1
            borrowRecord.setUpdatetime(LocalDate.now());
            borrowMapper.save(borrowRecord);
        }
    }

    @Transactional
    @Override
    public void saveRetur(Return_ obj){
        // todo 判断过期状态

        obj.setStatus("已归还");
        borrowMapper.updateStatus("已归还",obj.getId()); // 前端传来的借书id

        // 获取借书记录，填充还书记录的缺失字段
        Borrow borrow = borrowMapper.getById(obj.getId());
        if (borrow != null) {
            obj.setBarcode(borrow.getBarcode());
            obj.setBookItemId(borrow.getBookItemId());
        }
        
        obj.setRealDate(LocalDate.now());
        borrowMapper.saveRetur(obj);
        
        // 根据借阅数量恢复库存
        if (borrow != null && borrow.getNums() != null) {
            // 根据实际借阅数量恢复库存（现在每条记录nums=1）
            bookMapper.updateNumByNoWithCount(obj.getBookNo(), borrow.getNums());
        } else {
            // 兼容旧数据，如果没有nums字段，默认恢复1本
            bookMapper.updateNumByNo(obj.getBookNo());
        }
        
        // 更新book_item表状态为"在架"
        if (borrow != null && borrow.getBookItemId() != null) {
            bookItemMapper.updateStatus(borrow.getBookItemId(), "在架");
            log.info("还书成功，更新book_item状态为在架，bookItemId: {}", borrow.getBookItemId());
        }
        
        // 还书积分处理：判断是否逾期
        if (borrow != null && borrow.getUserNo() != null) {
            LocalDate returnDate = borrow.getReturnDate(); // 应还日期
            LocalDate realReturnDate = LocalDate.now(); // 实际归还日期
            
            if (realReturnDate.isAfter(returnDate)) {
                // 逾期还书，计算逾期天数并扣分
                long overdueDays = ChronoUnit.DAYS.between(returnDate, realReturnDate);
                int deductScore = (int) overdueDays * 5; // 每逾期1天扣5分
                userMapper.updateScore(borrow.getUserNo(), -deductScore);
                log.info("用户{}逾期还书，逾期{}天，扣除{}积分", borrow.getUserNo(), overdueDays, deductScore);
            } else {
                // 按时还书，奖励1积分
                userMapper.updateScore(borrow.getUserNo(), 1);
                log.info("用户{}按时还书，奖励1积分", borrow.getUserNo());
            }
        }
    }

    @Override
    public Borrow getById(Integer id) {
        return borrowMapper.getById(id);
    }

    @Override
    public void update(Borrow obj) {
        obj.setUpdatetime(LocalDate.now());
        borrowMapper.updateById(obj);
    }

    @Override
    public void deleteById(Integer id) {
        borrowMapper.deleteById(id);
    }

    @Override
    public void deleteReturById(Integer id) {
        borrowMapper.deleteReturById(id);
    }

    @Override
    public Map<String, Object> getCountByTimeRange(String timeRange) {
        log.info("获取借还统计数据，timeRange: {}", timeRange);
        Map<String, Object> map = new HashMap<>();
        Date today = new Date();
        List<DateTime> dateRange;
        switch (timeRange) {
            // offsetDay 计算时间的一个工具方法
            // rangeToList 返回从开始时间到借书时间的一个时间范围
            case  "week":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-6),today, DateField.DAY_OF_WEEK);
                break;
            case "month":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-29),today, DateField.DAY_OF_MONTH);
                break;
            case "month2":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-59),today, DateField.DAY_OF_MONTH);
                break;
            case "month3":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-89),today, DateField.DAY_OF_MONTH);
                break;
            default:
                dateRange = new ArrayList<>();
        }
        log.info("日期范围大小: {}", dateRange != null ? dateRange.size() : 0);
        // 处理方法 把datetime类型的list转换成一个String的list
        // 1.
        List<String> dateStrRange = datetimeToDateStr(dateRange);
        log.info("日期字符串范围: {}", dateStrRange);
        map.put("date", dateStrRange);
        // 2.and3. getcountbytimrange 不会统计数据库已有的日期
        // timeRange = week or month
        List<BorrowReturCountPO> borrowCount = borrowMapper.getCountByTimeRange(timeRange,1);
        log.info("借书统计数据条数: {}", borrowCount != null ? borrowCount.size() : 0);
        if (borrowCount != null && !borrowCount.isEmpty()) {
            for (BorrowReturCountPO po : borrowCount) {
                log.info("借书统计 - 日期: {}, 数量: {}", po.getDate(), po.getCount());
            }
        }
        List<BorrowReturCountPO> returnCount = borrowMapper.getCountByTimeRange(timeRange,2);
        log.info("还书统计数据条数: {}", returnCount != null ? returnCount.size() : 0);
        if (returnCount != null && !returnCount.isEmpty()) {
            for (BorrowReturCountPO po : returnCount) {
                log.info("还书统计 - 日期: {}, 数量: {}", po.getDate(), po.getCount());
            }
        }
        List<Integer> borrowList = countList(borrowCount, dateStrRange);
        List<Integer> returnList = countList(returnCount, dateStrRange);
        log.info("最终借书数据列表: {}", borrowList);
        log.info("最终还书数据列表: {}", returnList);
        map.put("borrow", borrowList);
        map.put("retur", returnList);
        return map;
    }
    
    @Override
    public Map<String, Object> getReservationCountByTimeRange(String timeRange) {
        Map<String, Object> map = new HashMap<>();
        Date today = new Date();
        List<DateTime> dateRange;
        switch (timeRange) {
            case  "week":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-6),today, DateField.DAY_OF_WEEK);
                break;
            case "month":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-29),today, DateField.DAY_OF_MONTH);
                break;
            case "month2":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-59),today, DateField.DAY_OF_MONTH);
                break;
            case "month3":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today,-89),today, DateField.DAY_OF_MONTH);
                break;
            default:
                dateRange = new ArrayList<>();
        }
        // 处理方法 把datetime类型的list转换成一个String的list
        List<String> dateStrRange = datetimeToDateStr(dateRange);
        map.put("date", dateStrRange);
        
        // 获取预约统计数据
        List<BorrowReturCountPO> reservationCount = reservationMapper.getCountByTimeRange(timeRange);
        map.put("reservation", countList(reservationCount, dateStrRange));
        return map;
    }
// 对数据库统计的时间进行处理
    private List<Integer> countList(List<BorrowReturCountPO> countPOList, List<String> dateStrRange) {
        List<Integer> list = CollUtil.newArrayList();
        // 即使countPOList为空，也要返回与dateStrRange长度一致的0值列表
        if (CollUtil.isEmpty(dateStrRange)) {
            return list;
        }
        for (String date : dateStrRange) {
            //.map(BorrowReturCountPO::getCount)取出对象里的count值
            //orELse 对没匹配的数据返回0
            // "2022-11-02" 没有的话就返回0
            Integer count = 0;
            if (CollUtil.isNotEmpty(countPOList)) {
                count = countPOList.stream().filter(countPO -> date.equals(countPO.getDate()))
                        .map(BorrowReturCountPO::getCount).findFirst().orElse(0);
            }
            list.add(count);
        }
        return list;
//        dataRange:[
//                "2022-11-29"
//                "2022-11-29",
//                "2022-11-29",
//                "2022-11-29",
//                "2022-11-29"
//                ],
//        borrow:[
//                0,
//                0,
//                2
//                ],
//          retur:[
//                0,
//                2,
//                1
//                ]
    }
// 把datetime的list数据转为一个String的list
    private List<String> datetimeToDateStr(List<DateTime> dateTimeList) {
        List<String> list = CollUtil.newArrayList();
        if (CollUtil.isEmpty(dateTimeList)){
            return list;
        }
        for (DateTime dateTime : dateTimeList) {
            String date = DateUtil.formatDate(dateTime);
            list.add(date);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getBookBorrowStats(String bookNo) {
        log.info("获取图书借阅统计，bookNo: {}", bookNo);
        // 获取数据库中的统计数据
        List<BorrowReturCountPO> stats = borrowMapper.getBookBorrowStats(bookNo);
        log.info("查询到的借阅统计数据条数: {}", stats != null ? stats.size() : 0);
        if (stats != null && !stats.isEmpty()) {
            for (BorrowReturCountPO stat : stats) {
                log.info("借阅统计 - 月份: {}, 数量: {}", stat.getDate(), stat.getCount());
            }
        }
        
        // 生成最近12个月的月份列表
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -11); // 从11个月前开始
        
        for (int i = 0; i < 12; i++) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // Calendar月份从0开始
            String monthStr = String.format("%d-%02d", year, month);
            months.add(monthStr);
            cal.add(Calendar.MONTH, 1);
        }
        
        // 将统计数据转换为Map，方便查找
        Map<String, Integer> statMap = new HashMap<>();
        for (BorrowReturCountPO stat : stats) {
            statMap.put(stat.getDate(), stat.getCount());
        }
        
        // 构建返回结果，确保每个月份都有数据（没有则为0）
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", month);
            item.put("count", statMap.getOrDefault(month, 0));
            result.add(item);
        }
        
        log.info("返回的借阅统计结果条数: {}", result.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getBookReservationStats(String bookNo) {
        log.info("获取图书预约统计，bookNo: {}", bookNo);
        // 从 reservation 表获取预约统计数据
        List<BorrowReturCountPO> stats = reservationMapper.countReservationsByBookNoAndMonth(bookNo);
        log.info("查询到的预约统计数据条数: {}", stats != null ? stats.size() : 0);
        if (stats != null && !stats.isEmpty()) {
            for (BorrowReturCountPO stat : stats) {
                log.info("预约统计 - 月份: {}, 数量: {}", stat.getDate(), stat.getCount());
            }
        }
        
        // 生成最近12个月的月份列表
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -11); // 从11个月前开始
        
        for (int i = 0; i < 12; i++) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // Calendar月份从0开始
            String monthStr = String.format("%d-%02d", year, month);
            months.add(monthStr);
            cal.add(Calendar.MONTH, 1);
        }
        
        // 将统计数据转换为Map，方便查找
        Map<String, Integer> statMap = new HashMap<>();
        for (BorrowReturCountPO stat : stats) {
            statMap.put(stat.getDate(), stat.getCount());
        }
        
        // 构建返回结果，确保每个月份都有数据（没有则为0）
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", month);
            item.put("count", statMap.getOrDefault(month, 0));
            result.add(item);
        }
        
        log.info("返回的预约统计结果条数: {}", result.size());
        return result;
    }


}
