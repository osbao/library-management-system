package com.example.springboot.service.impl;

import com.example.springboot.controller.request.AdminPageRequest;
import com.example.springboot.entity.GarageAdmin;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.GarageAdminMapper;
import com.example.springboot.service.IGarageAdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GarageAdminService implements IGarageAdminService {
    @Autowired
    GarageAdminMapper garageAdminMapper;

    @Override
    public List<GarageAdmin> list() {
        return garageAdminMapper.list();
    }

    @Override
    public PageInfo<GarageAdmin> page(AdminPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return new PageInfo<>(garageAdminMapper.listByCondition(pageRequest));
    }

    @Override
    public void save(GarageAdmin obj) {
        try {
            GarageAdmin exist = garageAdminMapper.getByUsername(obj.getUsername());
            if (exist != null) {
                throw new ServiceException("管理员账号已存在");
            }
            obj.setCreatetime(new Date());
            garageAdminMapper.save(obj);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("管理员数据插入错误", e);
        }
    }

    @Override
    public GarageAdmin getById(Integer id) {
        return garageAdminMapper.getById(id);
    }

    @Override
    public void update(GarageAdmin obj) {
        try {
            obj.setUpdatetime(new Date());
            garageAdminMapper.updateById(obj);
        } catch (Exception e) {
            throw new ServiceException("管理员数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        garageAdminMapper.deleteById(id);
    }

    @Override
    public GarageAdmin login(GarageAdmin admin) {
        GarageAdmin dbAdmin = garageAdminMapper.getByUsername(admin.getUsername());
        if (dbAdmin == null) {
            throw new ServiceException("账号不存在");
        }
        if (!admin.getPassword().equals(dbAdmin.getPassword())) {
            throw new ServiceException("密码错误");
        }
        return dbAdmin;
    }
}