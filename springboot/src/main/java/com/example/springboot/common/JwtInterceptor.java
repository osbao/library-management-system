package com.example.springboot.common;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.example.springboot.common.PathConfig.EXCLUDE_PATHS;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private static final String ERROR_CODE_401 = "401";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestURI = request.getRequestURI();

        // 白名单放行
        for (String excludePath : EXCLUDE_PATHS) {
            if (requestURI.contains(excludePath)) {
                return true;
            }
        }

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(ERROR_CODE_401, "无token，请重新登录");
        }

        // 使用固定服务端密钥验签（先验签再解析，防止伪造）
        try {
            TokenUtils.parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("token验证失败: {}", e.getMessage());
            throw new ServiceException(ERROR_CODE_401, "token验证失败");
        }
    }
}
