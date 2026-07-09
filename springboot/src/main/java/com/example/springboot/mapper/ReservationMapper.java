package com.example.springboot.mapper;

import com.example.springboot.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约记录Mapper接口
 */
@Mapper
public interface ReservationMapper {
    
    /**
     * 插入预约记录
     */
    int insert(Reservation reservation);
    
    /**
     * 根据ID查询预约记录
     */
    Reservation getById(@Param("id") Long id);
    
    /**
     * 根据用户编号查询预约列表
     */
    List<Reservation> listByUserNo(@Param("userNo") String userNo, @Param("status") String status);
    
    /**
     * 查询待取书列表（管理员用）
     */
    List<Reservation> listWaitingPickup();
    
    /**
     * 查询超时的预约记录
     */
    List<Reservation> listExpiredReservations();
    
    /**
     * 更新预约状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 确认取书
     */
    int confirmPickup(@Param("id") Long id, @Param("pickupTime") java.util.Date pickupTime);
    
    /**
     * 取消预约
     */
    int cancelReservation(@Param("id") Long id, @Param("cancelTime") java.util.Date cancelTime);
    
    /**
     * 检查用户是否已预约某本图书
     */
    Reservation getByUserNoAndBookItemId(@Param("userNo") String userNo, @Param("bookItemId") Long bookItemId);
    
    /**
     * 统计用户的预约数量
     */
    int countByUserNoAndStatus(@Param("userNo") String userNo, @Param("status") String status);
    
    /**
     * 根据图书ISBN和月份统计预约数量
     */
    List<com.example.springboot.mapper.po.BorrowReturCountPO> countReservationsByBookNoAndMonth(@Param("bookNo") String bookNo);
    
    /**
     * 根据时间范围统计预约数量
     */
    List<com.example.springboot.mapper.po.BorrowReturCountPO> getCountByTimeRange(@Param("timeRange") String timeRange);
    
    /**
     * 管理员查询预约列表（带分页和筛选）
     */
    List<Reservation> listForAdmin(@Param("userName") String userName, @Param("userNo") String userNo, 
                                    @Param("bookName") String bookName, @Param("status") String status,
                                    @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 管理员查询预约总数
     */
    int countForAdmin(@Param("userName") String userName, @Param("userNo") String userNo,
                      @Param("bookName") String bookName, @Param("status") String status);
}
