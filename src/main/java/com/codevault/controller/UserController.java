package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param dto 注册请求参数（带参数校验）
     * @return 统一响应结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Validated RegisterDTO dto) {
        log.info("收到注册请求，用户名：{}", dto.getUsername());
        return userService.register(dto);
    }

    /**
     * 用户登录
     * @param dto 登录请求参数（带参数校验）
     * @return 统一响应结果（包含token）
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginDTO dto) {
        log.info("收到登录请求，用户名：{}", dto.getUsername());
        return userService.login(dto);
    }
}
