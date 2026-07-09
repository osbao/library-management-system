package com.example.springboot.service.impl;

import com.example.springboot.entity.Book;
import com.example.springboot.entity.BookItem;
import com.example.springboot.entity.Reservation;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookItemMapper;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.ReservationMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预约服务实现类
 */
@Slf4j
@Service
public class ReservationServiceImpl implements IReservationService {
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private BorrowMapper borrowMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BookItemMapper bookItemMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    // 预约有效期：3天（72小时）
    private static final int RESERVATION_EXPIRE_DAYS = 3;
    // 每人最多同时预约数量
    private static final int MAX_RESERVATION_PER_USER = 3;
    // 借阅+预约总数上限
    private static final int MAX_BORROW_PLUS_RESERVATION = 5;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation reserveBook(String userNo, String bookNo) {
        log.info("用户{}开始预约图书{}", userNo, bookNo);
        
        // 1. 验证用户是否存在
        User user = userMapper.getByUsername(userNo);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 2. 验证用户是否有逾期未还的图书
        List<com.example.springboot.entity.Borrow> overdueBorrows = borrowMapper.listByUserNoAndStatus(userNo, "已逾期");
        if (!overdueBorrows.isEmpty()) {
            throw new ServiceException("您有逾期未还的图书，无法预约新书");
        }
        
        // 3. 验证用户当前预约数量是否已达上限
        int currentReservations = reservationMapper.countByUserNoAndStatus(userNo, "待取书");
        if (currentReservations >= MAX_RESERVATION_PER_USER) {
            throw new ServiceException("您已达到最大预约数量（" + MAX_RESERVATION_PER_USER + "本）");
        }
        
        // 4. 验证用户当前借阅+预约总数是否已达上限
        List<com.example.springboot.entity.Borrow> borrowedBooks = borrowMapper.listByUserNoAndStatus(userNo, "已借出");
        int totalActive = currentReservations + borrowedBooks.size();
        if (totalActive >= MAX_BORROW_PLUS_RESERVATION) {
            throw new ServiceException("您已达到最大借阅+预约数量（" + MAX_BORROW_PLUS_RESERVATION + "本）");
        }
        
        // 5. 验证图书是否存在
        Book book = bookMapper.getByNo(bookNo);
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        
        // 6. 查找一本"在架"状态的副本
        List<BookItem> availableItems = bookItemMapper.listByIsbn(bookNo);
        BookItem selectedItem = null;
        for (BookItem item : availableItems) {
            if ("在架".equals(item.getStatus())) {
                selectedItem = item;
                break;
            }
        }
        
        if (selectedItem == null) {
            throw new ServiceException("没有可用的图书副本");
        }
        
        // 7. 检查用户是否已经预约了这本书的某个副本
        Reservation existingReservation = reservationMapper.getByUserNoAndBookItemId(userNo, selectedItem.getId());
        if (existingReservation != null) {
            throw new ServiceException("您已经预约了这本书");
        }
        
        // 8. 更新图书副本状态为"已预约"
        bookItemMapper.updateStatus(selectedItem.getId(), "已预约");
        
        // 9. 创建预约记录
        Reservation reservation = new Reservation();
        reservation.setUserNo(userNo);
        reservation.setBookItemId(selectedItem.getId());
        reservation.setBookNo(bookNo);
        reservation.setBookName(book.getName());
        reservation.setBarcode(selectedItem.getBarcode());
        reservation.setStatus("待取书");
        reservation.setReserveTime(new Date());
        
        // 计算过期时间（3天后）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reservation.getReserveTime());
        calendar.add(Calendar.DAY_OF_MONTH, RESERVATION_EXPIRE_DAYS);
        reservation.setExpireTime(calendar.getTime());
        
        reservationMapper.insert(reservation);
        
        log.info("用户{}成功预约图书{}，预约ID：{}", userNo, bookNo, reservation.getId());
        
        return reservation;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long reservationId, String userNo) {
        log.info("用户{}取消预约，预约ID：{}", userNo, reservationId);
        
        // 1. 查询预约记录
        Reservation reservation = reservationMapper.getById(reservationId);
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }
        
        // 2. 验证状态：只有"待取书"状态可以取消
        if (!"待取书".equals(reservation.getStatus())) {
            throw new ServiceException("当前状态不允许取消预约");
        }
        
        // 3. 验证权限：只能取消自己的预约
        if (!userNo.equals(reservation.getUserNo())) {
            throw new ServiceException("无权取消他人的预约");
        }
        
        // 4. 释放图书副本：更新book_item表状态为"在架"
        bookItemMapper.updateStatus(reservation.getBookItemId(), "在架");
        
        // 5. 更新预约记录状态为"已取消"
        reservationMapper.cancelReservation(reservationId, new Date());
        
        log.info("用户{}成功取消预约，预约ID：{}", userNo, reservationId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminCancelReservation(Long reservationId) {
        log.info("管理员取消预约，预约ID：{}", reservationId);
        
        // 1. 查询预约记录
        Reservation reservation = reservationMapper.getById(reservationId);
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }
        
        // 2. 验证状态：只有"待取书"状态可以取消
        if (!"待取书".equals(reservation.getStatus())) {
            throw new ServiceException("当前状态不允许取消预约");
        }
        
        // 3. 释放图书副本：更新book_item表状态为"在架"
        bookItemMapper.updateStatus(reservation.getBookItemId(), "在架");
        
        // 4. 更新预约记录状态为"已取消"
        reservationMapper.cancelReservation(reservationId, new Date());
        
        log.info("管理员成功取消预约，预约ID：{}", reservationId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPickup(Long reservationId, Integer days) {
        log.info("管理员确认取书，预约ID：{}，借阅天数：{}", reservationId, days);
        
        // 1. 查询预约记录
        Reservation reservation = reservationMapper.getById(reservationId);
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }
        
        // 2. 验证状态：只有"待取书"状态可以确认取书
        if (!"待取书".equals(reservation.getStatus())) {
            throw new ServiceException("当前状态不允许确认取书");
        }
        
        // 3. 更新图书副本状态：已预约 -> 已借出
        bookItemMapper.updateStatus(reservation.getBookItemId(), "已借出");
        
        // 4. 更新预约记录状态为"已取书"
        reservationMapper.confirmPickup(reservationId, new Date());
        
        // 5. 创建借阅记录（borrow表）
        com.example.springboot.entity.Borrow borrow = new com.example.springboot.entity.Borrow();
        borrow.setBookName(reservation.getBookName());
        borrow.setBookNo(reservation.getBookNo());
        borrow.setBarcode(reservation.getBarcode());
        borrow.setBookItemId(reservation.getBookItemId());
        borrow.setUserNo(reservation.getUserNo());
        
        // 获取用户信息
        User user = userMapper.getByUsername(reservation.getUserNo());
        if (user != null) {
            borrow.setUserName(user.getName());
            borrow.setUserPhone(user.getPhone());
        }
        
        // 使用传入的借阅天数，默认30天
        int borrowDays = (days != null && days > 0) ? days : 30;
        borrow.setDays(borrowDays);
        borrow.setStatus("已借出");
        borrow.setCreatetime(LocalDate.now());
        borrow.setUpdatetime(LocalDate.now());
        borrow.setActualBorrowTime(new Date());
        
        // 计算应还日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, borrowDays);
        borrow.setReturnDate(calendar.getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        
        borrowMapper.save(borrow);
        
        log.info("管理员成功确认取书，预约ID：{}，借阅记录ID：{}，借阅天数：{}", reservationId, borrow.getId(), borrowDays);
    }
    
    @Override
    public List<Reservation> getUserReservations(String userNo, String status) {
        // 从 reservation 表查询用户的预约记录
        return reservationMapper.listByUserNo(userNo, status);
    }
    
    @Override
    public List<Reservation> getWaitingPickupList() {
        // 从 reservation 表查询所有待取书的记录
        return reservationMapper.listWaitingPickup();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleExpiredReservations() {
        log.info("开始处理超时预约...");
        
        // 1. 从 reservation 表查询超时的预约记录
        List<Reservation> expiredReservations = reservationMapper.listExpiredReservations();
        
        if (expiredReservations.isEmpty()) {
            log.info("没有超时预约记录");
            return;
        }
        
        log.info("发现{}条超时预约记录", expiredReservations.size());
        
        int expiredCount = 0;
        
        // 2. 逐条处理超时预约
        for (Reservation reservation : expiredReservations) {
            try {
                // 释放图书副本：更新book_item表状态为"在架"
                bookItemMapper.updateStatus(reservation.getBookItemId(), "在架");
                
                // 更新预约记录状态为"已超时"
                reservationMapper.updateStatus(reservation.getId(), "已超时");
                
                // 预约违约扣分：扣除5积分
                userMapper.updateScore(reservation.getUserNo(), -5);
                log.info("用户{}预约违约（超时未取书），扣除5积分", reservation.getUserNo());
                
                expiredCount++;
                log.info("处理超时预约成功，预约ID：{}，用户：{}", reservation.getId(), reservation.getUserNo());
            } catch (Exception e) {
                log.error("处理超时预约失败，预约ID：{}", reservation.getId(), e);
                // 继续处理下一条，不中断整个流程
            }
        }
        
        log.info("超时预约处理完成，共处理{}条", expiredCount);
    }
    
    @Override
    public java.util.Map<String, Object> getAdminReservationList(String userName, String userNo,
                                                                  String bookName, String status,
                                                                  Integer pageNum, Integer pageSize) {
        // 计算分页参数
        int offset = (pageNum - 1) * pageSize;
        
        // 查询列表
        List<Reservation> list = reservationMapper.listForAdmin(userName, userNo, bookName, status, offset, pageSize);
        
        // 查询总数
        int total = reservationMapper.countForAdmin(userName, userNo, bookName, status);
        
        // 封装结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return result;
    }
}
