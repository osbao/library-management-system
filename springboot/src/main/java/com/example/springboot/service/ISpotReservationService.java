package com.example.springboot.service;

import com.example.springboot.entity.SpotReservation;

import java.util.List;
import java.util.Map;

public interface ISpotReservationService {
    /** 车主预约车位 */
    SpotReservation reserveSpot(String driverNo, String spotNo);
    /** 车主取消预约 */
    void cancelReservation(Long reservationId, String driverNo);
    /** 管理员取消预约 */
    void adminCancelReservation(Long reservationId);
    /** 查询车主的预约列表 */
    List<SpotReservation> getDriverReservations(String driverNo, String status);
    /** 管理员确认入场 */
    void confirmArrival(Long reservationId);
    /** 管理员查询待入场列表 */
    List<SpotReservation> getWaitingArrivalList();
    /** 管理员查询预约列表（带分页和筛选） */
    Map<String, Object> getAdminReservationList(String driverName, String driverNo, String spotNo,
            String status, Integer pageNum, Integer pageSize);
}