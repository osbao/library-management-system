package com.example.springboot.service;

import com.example.springboot.entity.Reservation;

import java.util.List;

/**
 * 预约服务接口
 */
public interface IReservationService {
    
    /**
     * 用户预约图书
     * @param userNo 用户编号
     * @param bookNo 图书ISBN
     * @return 预约记录
     */
    Reservation reserveBook(String userNo, String bookNo);
    
    /**
     * 用户取消预约
     * @param reservationId 预约记录ID
     * @param userNo 用户编号（用于权限验证）
     */
    void cancelReservation(Long reservationId, String userNo);
    
    /**
     * 管理员取消预约
     * @param reservationId 预约记录ID
     */
    void adminCancelReservation(Long reservationId);
    
    /**
     * 管理员确认取书
     * @param reservationId 预约记录ID
     * @param days 借阅天数
     */
    void confirmPickup(Long reservationId, Integer days);
    
    /**
     * 查询用户的预约列表
     * @param userNo 用户编号
     * @param status 状态（可选）
     * @return 预约列表
     */
    List<Reservation> getUserReservations(String userNo, String status);
    
    /**
     * 查询管理员端的待取书列表
     * @return 待取书列表
     */
    List<Reservation> getWaitingPickupList();
    
    /**
     * 定时任务：处理超时的预约
     */
    void handleExpiredReservations();
    
    /**
     * 管理员查询预约列表（带分页和筛选）
     * @param userName 用户姓名（模糊查询）
     * @param userNo 用户卡号（模糊查询）
     * @param bookName 图书名称（模糊查询）
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 预约列表
     */
    java.util.Map<String, Object> getAdminReservationList(String userName, String userNo, 
                                                           String bookName, String status,
                                                           Integer pageNum, Integer pageSize);
}
