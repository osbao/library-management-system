package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExitRecord {
    private Integer id;
    private String spotNo;               // 车位编号
    private String driverNo;             // 车主编号
    private String driverName;           // 车主姓名
    private String driverPhone;          // 车主电话
    private String plateNumber;          // 车牌号
    private Double totalFee;             // 实际总费用
    private String paymentMethod;        // 支付方式：无感支付/扫码/现金/ETC
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime entryTime;     // 入场时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualExit;    // 实际出场时间
    private Integer parkedDays;          // 实际停车天数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;
}