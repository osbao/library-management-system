package com.example.springboot.controller.request;

import lombok.Data;

@Data
public class SignRequest {
    private String name;          // 姓名（用户注册必填）
    private String username;      // 用户名/卡号（管理员注册必填，用户注册时为空由系统生成）
    private String password;      // 密码
    private String newPassword;   // 确认密码
}
