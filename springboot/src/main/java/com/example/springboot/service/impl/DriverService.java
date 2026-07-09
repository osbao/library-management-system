package com.example.springboot.service.impl;

import com.example.springboot.controller.request.DriverPageRequest;
import com.example.springboot.entity.Driver;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.DriverMapper;
import com.example.springboot.service.IDriverService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DriverService implements IDriverService {
    @Autowired
    DriverMapper driverMapper;

    @Override
    public List<Driver> list() {
        return driverMapper.list();
    }

    @Override
    public PageInfo<Driver> page(DriverPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return new PageInfo<>(driverMapper.listByCondition(pageRequest));
    }

    @Override
    public void save(Driver driver) {
        try {
            Driver exist = driverMapper.getByDriverNo(driver.getDriverNo());
            if (exist != null) {
                throw new ServiceException("车主编号已存在");
            }
            driver.setScore(0);
            driver.setCreatetime(new Date());
            driverMapper.save(driver);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("车主数据插入错误", e);
        }
    }

    @Override
    public Driver getById(Integer id) {
        return driverMapper.getById(id);
    }

    @Override
    public void update(Driver driver) {
        try {
            driver.setUpdatetime(new Date());
            driverMapper.updateById(driver);
        } catch (Exception e) {
            throw new ServiceException("车主数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        driverMapper.deleteById(id);
    }

    @Override
    public Driver login(Driver driver) {
        Driver dbDriver = driverMapper.getByDriverNo(driver.getDriverNo());
        if (dbDriver == null) {
            throw new ServiceException("账号不存在");
        }
        if (!driver.getPassword().equals(dbDriver.getPassword())) {
            throw new ServiceException("密码错误");
        }
        return dbDriver;
    }

    @Override
    public void register(Driver driver) {
        try {
            Driver exist = driverMapper.getByDriverNo(driver.getDriverNo());
            if (exist != null) {
                throw new ServiceException("车主编号已存在");
            }
            driver.setRole("DRIVER");
            driver.setScore(0);
            driver.setStatus(true);
            driver.setCreatetime(new Date());
            driverMapper.save(driver);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("车主注册错误", e);
        }
    }

    @Override
    public void updateScore(String driverNo, Integer score) {
        try {
            driverMapper.updateScore(driverNo, score);
        } catch (Exception e) {
            throw new ServiceException("积分更新错误", e);
        }
    }
}