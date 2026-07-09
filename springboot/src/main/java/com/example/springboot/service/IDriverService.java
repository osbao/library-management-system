package com.example.springboot.service;

import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.DriverPageRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.controller.request.SignRequest;
import com.example.springboot.entity.Driver;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IDriverService {
    List<Driver> list();

    PageInfo<Driver> page(DriverPageRequest baseRequest);

    void save(Driver driver);

    Driver getById(Integer id);

    void update(Driver driver);

    void deleteById(Integer id);

    void handleAccount(Driver driver);

    LoginDTO login(LoginRequest request);

    void changePass(PasswordRequest request);

    void sign(SignRequest request);
}