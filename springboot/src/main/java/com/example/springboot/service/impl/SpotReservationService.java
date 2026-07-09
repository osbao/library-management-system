package com.example.springboot.service.impl;

import com.example.springboot.entity.SpotReservation;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.SpotReservationMapper;
import com.example.springboot.service.ISpotReservationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SpotReservationService implements ISpotReservationService {
    @Autowired
    SpotReservationMapper spotReservationMapper;

    @Override
    public void save(SpotReservation obj) {
        try {
            obj.setStatus("active");
            obj.setCreatetime(new Date());
            spotReservationMapper.save(obj);
        } catch (Exception e) {
            throw new ServiceException("预约数据插入错误", e);
        }
    }

    @Override
    public SpotReservation getById(Long id) {
        return spotReservationMapper.getById(id);
    }

    @Override
    public void update(SpotReservation obj) {
        try {
            spotReservationMapper.updateById(obj);
        } catch (Exception e) {
            throw new ServiceException("预约数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        spotReservationMapper.deleteById(id);
    }

    @Override
    public PageInfo<SpotReservation> page(Integer pageNum, Integer pageSize, String driverName, String driverNo,
            String spotNo, String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<SpotReservation> reservations = spotReservationMapper.listByCondition(driverName, driverNo, spotNo, status);
        return new PageInfo<>(reservations);
    }

    @Override
    public SpotReservation applyReservation(SpotReservation reservation) {
        // 检查是否有未取消的预约
        List<SpotReservation> existList = spotReservationMapper.listByDriverNoAndStatus(
                reservation.getDriverNo(), "active");
        if (existList != null && !existList.isEmpty()) {
            throw new ServiceException("你已有一个活跃的预约，请先取消");
        }
        reservation.setStatus("active");
        reservation.setCreatetime(new Date());
        spotReservationMapper.save(reservation);
        return reservation;
    }

    @Override
    public SpotReservation cancelReservation(Long id, String driverNo) {
        SpotReservation reservation = spotReservationMapper.getById(id);
        if (reservation == null) {
            throw new ServiceException("预约不存在");
        }
        if (!reservation.getDriverNo().equals(driverNo)) {
            throw new ServiceException("只能取消自己的预约");
        }
        if (!"active".equals(reservation.getStatus())) {
            throw new ServiceException("预约已完成或已取消");
        }
        reservation.setStatus("cancelled");
        spotReservationMapper.updateById(reservation);
        return reservation;
    }
}