package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Driver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DriverMapper {

    List<Driver> list();

    List<Driver> listByCondition(BaseRequest baseRequest);

    void save(Driver driver);

    Driver getById(Integer id);

    void updateById(Driver driver);

    void deleteById(Integer id);

    Driver getByDriverNo(@Param("driverNo") String driverNo);

    Driver getByName(@Param("name") String name);

    /**
     * 根据手机号查询车主
     */
    Driver getByPhone(@Param("phone") String phone);

    /**
     * 更新车主积分（增加或减少）
     */
    void updateScore(@Param("driverNo") String driverNo, @Param("score") Integer score);
}