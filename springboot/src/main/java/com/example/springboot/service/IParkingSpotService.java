package com.example.springboot.service;

import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.ParkingSpot;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IParkingSpotService {
    List<ParkingSpot> list();

    PageInfo<ParkingSpot> page(CategoryPageRequest pageRequest);

    void save(ParkingSpot obj);

    ParkingSpot getById(Integer id);

    void update(ParkingSpot obj);

    void deleteById(Integer id);

    /**
     * 按车位类型分页获取车位列表
     * @param type 车位类型（可选，为空则查询全部）
     * @param keyword 搜索关键字（车位编号，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页车位列表
     */
    PageInfo<ParkingSpot> pageByType(String type, String keyword, Integer pageNum, Integer pageSize);
}