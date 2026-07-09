package com.example.springboot.service.impl;

import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.ParkingSpot;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ParkingSpotMapper;
import com.example.springboot.service.IParkingSpotService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ParkingSpotService implements IParkingSpotService {
    @Autowired
    ParkingSpotMapper parkingSpotMapper;

    @Override
    public List<ParkingSpot> list() {
        return parkingSpotMapper.list();
    }

    @Override
    public PageInfo<ParkingSpot> page(CategoryPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return new PageInfo<>(parkingSpotMapper.listByCondition(pageRequest));
    }

    @Override
    public void save(ParkingSpot obj) {
        try {
            obj.setCreatetime(new Date());
            parkingSpotMapper.save(obj);
        } catch (Exception e) {
            throw new ServiceException("车位数据插入错误", e);
        }
    }

    @Override
    public ParkingSpot getById(Integer id) {
        return parkingSpotMapper.getById(id);
    }

    @Override
    public void update(ParkingSpot obj) {
        try {
            obj.setUpdatetime(new Date());
            parkingSpotMapper.updateById(obj);
        } catch (Exception e) {
            throw new ServiceException("车位数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        parkingSpotMapper.deleteById(id);
    }

    @Override
    public PageInfo<ParkingSpot> pageByType(String type, String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ParkingSpot> spots = parkingSpotMapper.listByType(type, keyword);
        return new PageInfo<>(spots);
    }
}