package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.SpotReservation;
import com.example.springboot.service.ISpotReservationService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车位预约控制器
 */
@Slf4j
@RestController
@RequestMapping("/reservation")
@CrossOrigin
public class SpotReservationController {

    @Autowired
    private ISpotReservationService reservationService;

    /**
     * 车主预约车位
     * @param params 预约参数：spotNo
     * @return 预约结果
     */
    @PostMapping("/reserve")
    public Result reserveSpot(@RequestBody java.util.Map<String, Object> params) {
        try {
            String spotNo = (String) params.get("spotNo");
            String driverNo = TokenUtils.getCurrentDriver().getUsername();
            SpotReservation reservation = reservationService.reserveSpot(driverNo, spotNo);
            return Result.success(reservation);
        } catch (Exception e) {
            log.error("预约失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 车主取消预约
     * @param reservationId 预约记录ID
     * @return 取消结果
     */
    @PostMapping("/cancel/{reservationId}")
    public Result cancelReservation(@PathVariable Long reservationId) {
        try {
            String driverNo = TokenUtils.getCurrentDriver().getUsername();
            reservationService.cancelReservation(reservationId, driverNo);
            return Result.success();
        } catch (Exception e) {
            log.error("取消预约失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员取消预约
     * @param reservationId 预约记录ID
     * @return 取消结果
     */
    @PostMapping("/admin/cancel/{reservationId}")
    public Result adminCancelReservation(@PathVariable Long reservationId) {
        try {
            reservationService.adminCancelReservation(reservationId);
            return Result.success();
        } catch (Exception e) {
            log.error("管理员取消预约失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询车主的预约列表
     * @param status 状态（可选）
     * @return 预约列表
     */
    @GetMapping("/my")
    public Result getMyReservations(@RequestParam(required = false) String status) {
        try {
            String driverNo = TokenUtils.getCurrentDriver().getUsername();
            List<SpotReservation> reservations = reservationService.getDriverReservations(driverNo, status);
            return Result.success(reservations);
        } catch (Exception e) {
            log.error("查询预约列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员确认入场
     * @param reservationId 预约记录ID
     * @return 确认结果
     */
    @PostMapping("/confirm/{reservationId}")
    public Result confirmArrival(@PathVariable Long reservationId) {
        try {
            reservationService.confirmArrival(reservationId);
            return Result.success();
        } catch (Exception e) {
            log.error("确认入场失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员查询待入场列表
     * @return 待入场列表
     */
    @GetMapping("/waiting-arrival")
    public Result getWaitingArrivalList() {
        try {
            List<SpotReservation> list = reservationService.getWaitingArrivalList();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询待入场列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员查询预约列表（带分页和筛选）
     * @param driverName 车主姓名
     * @param driverNo 车主编号
     * @param spotNo 车位编号
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 预约列表
     */
    @GetMapping("/admin/list")
    public Result getAdminReservationList(
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String driverNo,
            @RequestParam(required = false) String spotNo,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            java.util.Map<String, Object> result = reservationService.getAdminReservationList(
                driverName, driverNo, spotNo, status, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询预约列表失败", e);
            return Result.error(e.getMessage());
        }
    }
}