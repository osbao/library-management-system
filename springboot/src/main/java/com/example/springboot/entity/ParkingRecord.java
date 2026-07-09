package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingRecord {
    private Integer id;
    private String spotNo;               // 车位编号
    private String spotType;             // 车位类型
    private String driverNo;             // 车主编号
    private String driverName;           // 车主姓名
    private String driverPhone;          // 车主电话
    private String plateNumber;          // 车牌号
    private Integer days;                // 预计停车天数
    private String status;               // 停车状态：停车中/已出场/已超时/已缴费
    private Double fee;                  // 费用
    private Double overdueFee;           // 超时附加费
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime entryTime;     // 入场时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expectedExit;  // 预计出场时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualExit;    // 实际出场时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;
}