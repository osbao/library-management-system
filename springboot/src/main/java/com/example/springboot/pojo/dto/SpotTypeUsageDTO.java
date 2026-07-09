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
public class SpotTypeUsageDTO implements Serializable {
    /** 车位类型名称 */
    private String name;
    /** 使用次数 */
    private Integer number;
}