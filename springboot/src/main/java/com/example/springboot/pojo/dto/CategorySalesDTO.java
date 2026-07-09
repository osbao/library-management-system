package com.example.springboot.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesDTO {
    // 分类名称
    private String name;
    
    // 借阅数量
    private Integer number;
}