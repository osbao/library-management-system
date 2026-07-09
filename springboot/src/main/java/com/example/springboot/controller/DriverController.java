package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.SignRequest;
import com.example.springboot.controller.request.DriverPageRequest;
import com.example.springboot.entity.Driver;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    IDriverService driverService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        LoginDTO login = driverService.login(request);
        return Result.success(login);
    }

    @PostMapping("/sign")
    public Result sign(@RequestBody SignRequest request) {
        try {
            driverService.sign(request);
            return Result.success("注册成功");
        } catch (ServiceException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PostMapping("/save")
    public Result save(@RequestBody Driver driver) {
        driverService.save(driver);
        return Result.success();
    }

    @PostMapping("/account")
    public Result account(@RequestBody Driver driver) {
        driverService.handleAccount(driver);
        return Result.success();
    }

    @PutMapping("/password")
    public Result password(@RequestBody com.example.springboot.controller.request.PasswordRequest request) {
        driverService.changePass(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        driverService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Driver driver) {
        driverService.update(driver);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Driver driver = driverService.getById(id);
        return Result.success(driver);
    }

    @GetMapping("/list")
    public Result list() {
        List<Driver> drivers = driverService.list();
        return Result.success(drivers);
    }

    @GetMapping("/page")
    public Result page(DriverPageRequest driverPageRequest) {
        return Result.success(driverService.page(driverPageRequest));
    }
}