package com.example.springboot.mapper;

import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.SpotType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpotTypeMapper {

    List<SpotType> list();

    List<SpotType> listByCondition(CategoryPageRequest pageRequest);

    void save(SpotType obj);

    SpotType getById(Integer id);

    void updateById(SpotType obj);

    void deleteById(Integer id);
}