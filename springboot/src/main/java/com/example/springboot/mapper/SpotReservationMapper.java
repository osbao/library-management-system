package com.example.springboot.mapper;

import com.example.springboot.entity.SpotReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpotReservationMapper {

    void save(SpotReservation obj);

    SpotReservation getById(Long id);

    void updateById(SpotReservation obj);

    void deleteById(Long id);

    List<SpotReservation> listByDriverNoAndStatus(@Param("driverNo") String driverNo, @Param("status") String status);

    List<SpotReservation> listByStatus(@Param("status") String status);

    List<SpotReservation> listByCondition(@Param("driverName") String driverName, @Param("driverNo") String driverNo,
            @Param("spotNo") String spotNo, @Param("status") String status);
}