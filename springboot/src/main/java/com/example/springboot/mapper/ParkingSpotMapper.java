package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ParkingSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingSpotMapper {

    List<ParkingSpot> list();

    List<ParkingSpot> listByCondition(BaseRequest baseRequest);

    void save(ParkingSpot obj);

    ParkingSpot getById(Integer id);

    void updateById(ParkingSpot obj);

    void deleteById(Integer id);

    ParkingSpot getBySpotNo(@Param("spotNo") String spotNo);

    List<ParkingSpot> listByType(@Param("type") String type, @Param("keyword") String keyword);
}