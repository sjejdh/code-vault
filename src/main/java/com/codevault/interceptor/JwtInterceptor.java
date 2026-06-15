package com.codevault.interceptor;

import com.codevault.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codevault.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 * 拦截所有需要认证的请求，验证JWT令牌的有效性
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    /** JSON序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求预处理：验证JWT令牌
     * 从请求头 Authorization 中提取Bearer token，调用JwtUtils验证有效性
     * 有效则将userId放入request attribute，无效则返回401错误响应
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行OPTIONS预检请求（跨域时会先发OPTIONS请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取Authorization
        String authHeader = request.getHeader("Authorization");

        // 检查Authorization头是否存在且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求未携带有效的Authorization头，URI：{}", request.getRequestURI());
            sendUnauthorized(response, "未登录或token已过期");
            return false;
        }

        // 提取token（去掉 "Bearer " 前缀）
        String token = authHeader.substring(7);

        // 验证token有效性
        if (!jwtUtils.validateToken(token)) {
            log.warn("JWT令牌验证失败，URI：{}", request.getRequestURI());
            sendUnauthorized(response, "token无效或已过期");
            return false;
        }

        // 从token中解析userId并放入请求属性
        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        log.debug("JWT认证通过，userId：{}", userId);

        return true;
    }

    /**
     * 向客户端返回401未授权响应
     * @param response HTTP响应对象
     * @param message 错误信息
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
