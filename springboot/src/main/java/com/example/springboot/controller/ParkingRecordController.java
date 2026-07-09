package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.ExitRecord;
import com.example.springboot.entity.ParkingRecord;
import com.example.springboot.service.IParkingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/parking")
public class ParkingRecordController {

    @Autowired
    IParkingRecordService parkingRecordService;

    @PostMapping("/save")
    public Result save(@RequestBody ParkingRecord obj) {
        parkingRecordService.save(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        parkingRecordService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody ParkingRecord obj) {
        parkingRecordService.update(obj);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        ParkingRecord obj = parkingRecordService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<ParkingRecord> list = parkingRecordService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(BaseRequest baseRequest) {
        return Result.success(parkingRecordService.page(baseRequest));
    }
}