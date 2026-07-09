package com.example.springboot.config;

import com.example.springboot.service.IReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 预约定时任务
 */
@Slf4j
@Component
public class ReservationScheduler {
    
    @Autowired
    private IReservationService reservationService;
    
    /**
     * 每小时执行一次，处理超时的预约
     * cron表达式：0 0 * * * ? 表示每小时的第0分钟执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void handleExpiredReservations() {
        log.info("========== 开始执行预约超时检查定时任务 ==========");
        try {
            reservationService.handleExpiredReservations();
        } catch (Exception e) {
            log.error("预约超时检查定时任务执行失败", e);
        }
        log.info("========== 预约超时检查定时任务执行完成 ==========");
    }
}
