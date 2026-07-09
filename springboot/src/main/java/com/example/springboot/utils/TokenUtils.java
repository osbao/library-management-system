package com.example.springboot.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Driver;
import com.example.springboot.entity.GarageAdmin;
import com.example.springboot.service.IDriverService;
import com.example.springboot.service.IGarageAdminService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class TokenUtils {

    private static IGarageAdminService staticAdminService;
    private static IDriverService staticDriverService;

    @Resource
    private IGarageAdminService adminService;

    @Resource
    private IDriverService driverService;

    /** 固定服务端密钥，由 application.yml 中的 jwt.secret 注入 */
    private static String SECRET;

    @Value("${jwt.secret:libra-mind-default-secret-key-2026}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    @PostConstruct
    public void setUserService() {
        staticAdminService = adminService;
        staticDriverService = driverService;
    }

    /** 生成 HMAC-SHA256 密钥（固定密钥，不依赖用户密码） */
    private static SecretKey getKey() {
        // jjwt 要求密钥长度至少256位（32字节）
        byte[] keyBytes = new byte[32];
        byte[] src = SECRET.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, keyBytes, 0, Math.min(src.length, 32));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** 生成token，12小时过期 */
    public static String genToken(String userId) {
        return Jwts.builder()
                .audience().add(userId).and()
                .expiration(DateUtil.offsetHour(new Date(), 12))
                .signWith(getKey())
                .compact();
    }

    /** 生成token，自定义过期天数 */
    public static String genToken(String userId, int days) {
        return Jwts.builder()
                .audience().add(userId).and()
                .expiration(DateUtil.offsetDay(new Date(), days))
                .signWith(getKey())
                .compact();
    }

    /** 验证签名并解析token，返回Claims */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从token中获取userId（先验签再解码） */
    public static String getUserIdFromToken(String token) {
        return parseToken(token).getAudience().iterator().next();
    }

    /** 从请求中提取token */
    private static String extractToken() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    /** 获取当前登录的车主信息（先验签） */
    public static Driver getCurrentDriver() {
        String token = null;
        try {
            token = extractToken();
            if (StrUtil.isBlank(token)) {
                log.error("获取token失败");
                return null;
            }
            String userId = getUserIdFromToken(token);
            return staticDriverService.getById(Integer.valueOf(userId));
        } catch (Exception e) {
            log.error("获取车主信息失败, token={}", token, e);
            return null;
        }
    }

    /** 获取当前登录的车库管理员信息（先验签） */
    public static GarageAdmin getCurrentAdmin() {
        String token = null;
        try {
            token = extractToken();
            if (StrUtil.isBlank(token)) {
                log.error("获取token失败");
                return null;
            }
            String adminId = getUserIdFromToken(token);
            return staticAdminService.getById(Integer.valueOf(adminId));
        } catch (Exception e) {
            log.error("获取管理员信息失败, token={}", token, e);
            return null;
        }
    }
}

