package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.GarageAdmin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GarageAdminMapper {

    List<GarageAdmin> list();

    List<GarageAdmin> listByCondition(BaseRequest baseRequest);

    void save(GarageAdmin obj);

    GarageAdmin getById(Integer id);

    void updateById(GarageAdmin obj);

    void deleteById(Integer id);

    GarageAdmin getByUsername(String username);
}