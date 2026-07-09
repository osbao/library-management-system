package com.example.springboot.service.impl;

import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.SpotType;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.SpotTypeMapper;
import com.example.springboot.service.ISpotTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SpotTypeService implements ISpotTypeService {
    @Autowired
    SpotTypeMapper spotTypeMapper;

    @Override
    public List<SpotType> list() {
        return spotTypeMapper.list();
    }

    @Override
    public PageInfo<SpotType> page(CategoryPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return new PageInfo<>(spotTypeMapper.listByCondition(pageRequest));
    }

    @Override
    public void save(SpotType obj) {
        try {
            obj.setCreatetime(new Date());
            spotTypeMapper.save(obj);
        } catch (Exception e) {
            throw new ServiceException("车位类型数据插入错误", e);
        }
    }

    @Override
    public SpotType getById(Integer id) {
        return spotTypeMapper.getById(id);
    }

    @Override
    public void update(SpotType obj) {
        try {
            obj.setUpdatetime(new Date());
            spotTypeMapper.updateById(obj);
        } catch (Exception e) {
            throw new ServiceException("车位类型数据更新错误", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        spotTypeMapper.deleteById(id);
    }
}