package com.example.springboot.service;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ParkingRecord;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IParkingRecordService {
    List<ParkingRecord> list();

    PageInfo<ParkingRecord> page(BaseRequest baseRequest);

    void save(ParkingRecord obj);

    ParkingRecord getById(Integer id);

    void update(ParkingRecord obj);

    void deleteById(Integer id);
}