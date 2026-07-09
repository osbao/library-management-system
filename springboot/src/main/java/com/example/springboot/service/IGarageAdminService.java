package com.example.springboot.service;

import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.AdminPageRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.controller.request.SignRequest;
import com.example.springboot.entity.GarageAdmin;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IGarageAdminService {
    List<GarageAdmin> list();

    PageInfo<GarageAdmin> page(AdminPageRequest adminPageRequest);

    void save(GarageAdmin obj);

    GarageAdmin getById(Integer id);

    void update(GarageAdmin obj);

    void deleteById(Integer id);

    LoginDTO login(LoginRequest obj);

    void changePass(PasswordRequest request);

    void sign(SignRequest request);
}