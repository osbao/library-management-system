package com.example.springboot.service;

import com.example.springboot.pojo.vo.SpotRevenueReportVo;
import com.example.springboot.pojo.vo.DriverVisitReportVo;
import com.example.springboot.pojo.vo.SpotTypeUsageReportVo;

import java.time.LocalDate;

public interface IReportService {
    SpotRevenueReportVo top10(LocalDate begin, LocalDate end);

    DriverVisitReportVo top10Drivers(LocalDate begin, LocalDate end);

    SpotTypeUsageReportVo top10SpotTypes(LocalDate begin, LocalDate end);
}