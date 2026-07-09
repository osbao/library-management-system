package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SpotType {
    private Integer id;
    private String name;             // 类型名称：普通车位/新能源充电/大型SUV/豪华VIP
    private String remark;
    private Integer pid;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updatetime;
    private List<SpotType> children;
}