package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.ParkingSpot;
import com.example.springboot.service.IParkingSpotService;
import com.example.springboot.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/report")
public class GarageReportController {

    @Autowired
    IParkingSpotService spotService;

    @Autowired
    IReportService reportService;

    /**
     * 停车场收入Top10车位
     * @return Top10数据
     */
    @GetMapping("/top10")
    public Result getTop10() {
        return Result.success(reportService.top10(null, null));
    }

    /**
     * 高频车主Top10
     * @return Top10数据
     */
    @GetMapping("/drivers")
    public Result getDriversTop10() {
        return Result.success(reportService.top10Drivers(null, null));
    }

    /**
     * 热门车位类型Top10
     * @return Top10数据
     */
    @GetMapping("/spotTypes")
    public Result getSpotTypesTop10() {
        return Result.success(reportService.top10SpotTypes(null, null));
    }
}