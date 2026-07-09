package com.example.springboot.common;

import lombok.Data;

@Data
public class PathConfig {
    // 定义需要排除的路径（白名单）
    public static final String[] EXCLUDE_PATHS = {
            "/admin/sign",  // 管理员注册接口
            "/admin/login",  // 管理员登录接口
            "/user/login",   // 用户登录接口
            "/user/sign",    // 用户注册接口
            "/book/page",    // 图书分页查询（允许未登录浏览）
            "/book/list",    // 图书列表（允许未登录浏览）
    };
}
