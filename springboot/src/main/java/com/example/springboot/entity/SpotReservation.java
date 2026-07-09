package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车位预约记录实体类
 */
@Data
public class SpotReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;                    // 主键ID
    private String driverNo;            // 车主编号
    private String spotNo;              // 车位编号
    private String spotType;            // 车位类型
    private String plateNumber;         // 车牌号
    private String status;              // 预约状态：待入场/已入场/已取消/已超时
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reserveTime;           // 预约时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;            // 预约过期时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date arrivalTime;           // 实际到达时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;            // 取消时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;            // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;            // 更新时间
    private Integer deleted;            // 逻辑删除标记

    // 以下字段用于关联查询，不对应数据库字段
    private String driverName;          // 车主姓名
    private String driverPhone;         // 车主电话
}