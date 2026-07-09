package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Driver {
    private Integer id;
    private String name;             // 车主姓名
    private String username;         // 用户名
    private String password;         // 密码
    private String phone;            // 电话
    private String plateNumber;      // 车牌号
    private Double balance;          // 账户余额
    private Integer score;           // 信用评分
    private String vehicleType;      // 车辆类型：轿车/SUV/新能源/豪华
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updatetime;
    private boolean status;          // 账号状态
}