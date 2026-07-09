package com.example.springboot.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverVisitDTO implements Serializable {
    /** 车主姓名（或车牌号） */
    private String name;
    /** 停车次数 */
    private Integer number;
}