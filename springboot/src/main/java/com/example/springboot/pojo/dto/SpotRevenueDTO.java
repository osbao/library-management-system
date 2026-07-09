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
public class SpotRevenueDTO implements Serializable {
    /** 车位编号 */
    private String name;
    /** 营收金额 */
    private Double number;
}