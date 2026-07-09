package com.example.springboot.entity;

import com.example.springboot.controller.request.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ParkingSpot extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String spotNo;           // 车位编号
    private String floor;            // 楼层/区域
    private String spotType;         // 车位类型：普通/新能源充电/大型SUV/豪华VIP
    private String status;           // 状态：空闲/已占用/已预约/维护中
    private Double pricePerHour;     // 每小时价格
    private Integer score;           // 评分
    private List<String> spotTypes;  // 车位类型列表（用于筛选）
    private String description;      // 描述
}