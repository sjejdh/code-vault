package com.codevault.config;

import com.codevault.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置跨域CORS、JWT拦截器和BCrypt密码编码器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 构造方法注入JWT拦截器
     */
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * 注册BCryptPasswordEncoder为Bean
     * 用于用户注册时加密密码和登录时校验密码
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置跨域CORS
     * 允许所有来源、所有请求头和所有请求方法，方便前端调试
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 注册JWT拦截器
     * 拦截所有请求，排除不需要认证的公开接口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要JWT认证的公开接口
                .excludePathPatterns(
                        "/api/user/login",       // 登录接口
                        "/api/user/register",    // 注册接口
                        "/api/snippet/public/**",// 公开的代码片段查询接口
                        "/api/snippet/detail/**",// 公开的代码片段详情接口
                        "/api/category/**",      // 公开的分类列表接口
                        "/api/tag/**",           // 公开的标签列表接口
                        // Swagger 接口文档相关路径
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        // Actuator 健康检查
                        "/actuator/**"
                );
    }
}
