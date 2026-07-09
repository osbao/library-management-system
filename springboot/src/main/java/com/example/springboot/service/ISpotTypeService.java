package com.example.springboot.service;

import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.SpotType;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ISpotTypeService {
    List<SpotType> list();

    PageInfo<SpotType> page(CategoryPageRequest pageRequest);

    void save(SpotType obj);

    SpotType getById(Integer id);

    void update(SpotType obj);

    void deleteById(Integer id);
}