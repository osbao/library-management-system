package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ExitRecord;
import com.example.springboot.pojo.dto.SpotRevenueDTO;
import com.example.springboot.pojo.dto.DriverVisitDTO;
import com.example.springboot.pojo.dto.SpotTypeUsageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExitRecordMapper {

    List<ExitRecord> list();

    List<ExitRecord> listByCondition(BaseRequest baseRequest);

    void save(ExitRecord obj);

    ExitRecord getById(Integer id);

    void updateById(ExitRecord obj);

    void deleteById(Integer id);

    /**
     * 按时间段统计离场记录（用于报表）
     */
    List<ExitRecord> listByTimeRange(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 按时间段和条件筛选离场记录
     */
    List<ExitRecord> listByConditionWithTimeRange(@Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end,
            @Param("plate") String plate,
            @Param("spotNo") String spotNo);

    /**
     * 车位收入Top10报表
     */
    List<SpotRevenueDTO> getSpotRevenueTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 高频车主Top10报表
     */
    List<DriverVisitDTO> getDriverVisitTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 热门车位类型Top10报表
     */
    List<SpotTypeUsageDTO> getSpotTypeUsageTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}