package com.example.springboot.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTop10ReportVo {
    // 分类名称列表，以逗号分隔，例如：文学,科技,历史
    private String nameList;

    // 借阅数量列表，以逗号分隔，例如：260,215,200
    private String numberList;
}
