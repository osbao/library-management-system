package com.example.springboot.service.impl;

import com.example.springboot.mapper.ExitRecordMapper;
import com.example.springboot.pojo.dto.SpotRevenueDTO;
import com.example.springboot.pojo.dto.DriverVisitDTO;
import com.example.springboot.pojo.dto.SpotTypeUsageDTO;
import com.example.springboot.pojo.vo.SpotRevenueReportVo;
import com.example.springboot.pojo.vo.DriverVisitReportVo;
import com.example.springboot.pojo.vo.SpotTypeUsageReportVo;
import com.example.springboot.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {

    @Autowired
    ExitRecordMapper exitRecordMapper;

    @Override
    public SpotRevenueReportVo top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin != null ? begin.atStartOfDay() : null;
        LocalDateTime endTime = end != null ? end.atTime(LocalTime.MAX) : null;
        List<SpotRevenueDTO> list = exitRecordMapper.getSpotRevenueTop10(beginTime, endTime);
        String nameList = list.stream().map(SpotRevenueDTO::getName).collect(Collectors.joining(","));
        String numberList = list.stream().map(d -> d.getNumber().toString()).collect(Collectors.joining(","));
        return SpotRevenueReportVo.builder().nameList(nameList).numberList(numberList).build();
    }

    @Override
    public DriverVisitReportVo top10Drivers(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin != null ? begin.atStartOfDay() : null;
        LocalDateTime endTime = end != null ? end.atTime(LocalTime.MAX) : null;
        List<DriverVisitDTO> list = exitRecordMapper.getDriverVisitTop10(beginTime, endTime);
        String nameList = list.stream().map(DriverVisitDTO::getName).collect(Collectors.joining(","));
        String numberList = list.stream().map(d -> d.getNumber().toString()).collect(Collectors.joining(","));
        return DriverVisitReportVo.builder().nameList(nameList).numberList(numberList).build();
    }

    @Override
    public SpotTypeUsageReportVo top10SpotTypes(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin != null ? begin.atStartOfDay() : null;
        LocalDateTime endTime = end != null ? end.atTime(LocalTime.MAX) : null;
        List<SpotTypeUsageDTO> list = exitRecordMapper.getSpotTypeUsageTop10(beginTime, endTime);
        String nameList = list.stream().map(SpotTypeUsageDTO::getName).collect(Collectors.joining(","));
        String numberList = list.stream().map(d -> d.getNumber().toString()).collect(Collectors.joining(","));
        return SpotTypeUsageReportVo.builder().nameList(nameList).numberList(numberList).build();
    }
}