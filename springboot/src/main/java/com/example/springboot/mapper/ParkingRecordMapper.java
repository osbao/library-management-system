package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ParkingRecordMapper {

    List<ParkingRecord> list();

    List<ParkingRecord> listByCondition(BaseRequest baseRequest);

    void save(ParkingRecord obj);

    ParkingRecord getById(Integer id);

    void updateById(ParkingRecord obj);

    void deleteById(Integer id);

    /**
     * 根据车牌号查询未离场的停车记录（status='在场'）
     */
    ParkingRecord getActiveByPlate(@Param("plate") String plate);

    /**
     * 处理离场：更新记录状态、实付金额、离场时间
     */
    void processExit(@Param("id") Integer id, @Param("actualFee") Double actualFee,
                     @Param("exitTime") LocalDateTime exitTime, @Param("status") String status);
}