package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.*;
import com.example.springboot.entity.GarageAdmin;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.IGarageAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class GarageAdminController {

    @Autowired
    IGarageAdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        LoginDTO login = adminService.login(request);
        return Result.success(login);
    }

    @PostMapping("/sign")
    public Result sign(@RequestBody SignRequest request) {
        try {
            adminService.sign(request);
            return Result.success("注册成功");
        } catch (ServiceException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result password(@RequestBody PasswordRequest request) {
        adminService.changePass(request);
        return Result.success();
    }

    @PostMapping("/save")
    public Result save(@RequestBody GarageAdmin obj) {
        adminService.save(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody GarageAdmin obj) {
        adminService.update(obj);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        GarageAdmin obj = adminService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<GarageAdmin> list = adminService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(AdminPageRequest pageRequest) {
        return Result.success(adminService.page(pageRequest));
    }
}