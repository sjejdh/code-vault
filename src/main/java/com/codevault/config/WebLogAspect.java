package com.codevault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Web请求日志切面：记录所有 Controller 接口的入参、出参和响应时间
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点：拦截所有 Controller 层的方法
     */
    @Pointcut("execution(* com.codevault.controller.*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：记录请求和响应信息
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        String clientIp = getClientIp(request);

        // 获取方法名和参数
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        // 记录请求开始
        long startTime = System.currentTimeMillis();
        log.info("[请求开始] {} {} | IP: {} | 方法: {} | 参数: {}", httpMethod, requestURI, clientIp, methodName, args);

        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("[请求异常] {} {} | 耗时: {}ms | 异常: {}", httpMethod, requestURI, costTime, e.getMessage());
            throw e;
        }

        // 记录请求结束
        long costTime = System.currentTimeMillis() - startTime;
        String responseStr;
        try {
            responseStr = objectMapper.writeValueAsString(result);
            // 截断过长的响应
            if (responseStr.length() > 1000) {
                responseStr = responseStr.substring(0, 1000) + "... (截断)";
            }
        } catch (Exception e) {
            responseStr = result.toString();
        }

        log.info("[请求完成] {} {} | 耗时: {}ms | 响应: {}", httpMethod, requestURI, costTime, responseStr);
        return result;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
