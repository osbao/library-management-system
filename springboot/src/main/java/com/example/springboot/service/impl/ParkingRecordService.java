package com.example.springboot.service.impl;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ExitRecord;
import com.example.springboot.entity.ParkingRecord;
import com.example.springboot.entity.ParkingSpot;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ExitRecordMapper;
import com.example.springboot.mapper.ParkingRecordMapper;
import com.example.springboot.mapper.ParkingSpotMapper;
import com.example.springboot.service.IParkingRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ParkingRecordService implements IParkingRecordService {
    @Autowired
    ParkingRecordMapper parkingRecordMapper;
    @Autowired
    ParkingSpotMapper parkingSpotMapper;
    @Autowired
    ExitRecordMapper exitRecordMapper;

    @Override
    public PageInfo<ParkingRecord> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        return new PageInfo<>(parkingRecordMapper.listByCondition(baseRequest));
    }

    @Override
    public List<ParkingRecord> list() {
        return parkingRecordMapper.list();
    }

    @Override
    public ParkingRecord getById(Integer id) {
        return parkingRecordMapper.getById(id);
    }

    @Override
    public void save(ParkingRecord obj) {
        try {
            // 检查是否已有该车牌在场记录
            ParkingRecord exist = parkingRecordMapper.getActiveByPlate(obj.getPlate());
            if (exist != null) {
                throw new ServiceException("该车辆已在停车场内，请先离场");
            }
            obj.setStatus("在场");
            obj.setCreatetime(new Date());
            parkingRecordMapper.save(obj);
            // 更新车位状态为已被占用
            ParkingSpot spot = parkingSpotMapper.getBySpotNo(obj.getSpotNo());
            if (spot != null) {
                spot.setStatus("occupied");
                parkingSpotMapper.updateById(spot);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("车辆入场数据插入错误", e);
        }
    }

    @Override
    public void update(ParkingRecord obj) {
        try {
            parkingRecordMapper.updateById(obj);
        } catch (Exception e) {
            throw new ServiceException("停车记录数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        parkingRecordMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void processExit(Integer recordId, Double actualFee) {
        try {
            ParkingRecord record = parkingRecordMapper.getById(recordId);
            if (record == null) {
                throw new ServiceException("停车记录不存在");
            }
            if (!"在场".equals(record.getStatus())) {
                throw new ServiceException("该记录已离场");
            }
            LocalDateTime exitTime = LocalDateTime.now();
            Duration duration = Duration.between(record.getEntryTime(), exitTime);
            long minutes = duration.toMinutes();
            double feeRate = record.getFeeRate() != null ? record.getFeeRate() : 0;
            double estimatedFee = minutes * feeRate / 60.0;

            parkingRecordMapper.processExit(recordId, actualFee, exitTime, "已离场");

            // 写入离场记录
            ExitRecord exitRecord = new ExitRecord();
            exitRecord.setPlate(record.getPlate());
            exitRecord.setSpotNo(record.getSpotNo());
            exitRecord.setDriverNo(record.getDriverNo());
            exitRecord.setEntryTime(record.getEntryTime());
            exitRecord.setExitTime(exitTime);
            exitRecord.setEstimatedFee(estimatedFee);
            exitRecord.setActualFee(actualFee);
            exitRecord.setStatus("已离场");
            exitRecord.setCreatetime(new Date());
            exitRecordMapper.save(exitRecord);

            // 释放车位
            ParkingSpot spot = parkingSpotMapper.getBySpotNo(record.getSpotNo());
            if (spot != null) {
                spot.setStatus("free");
                parkingSpotMapper.updateById(spot);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("车辆离场处理错误", e);
        }
    }
}