package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 用户控制器
 * Controller 层负责将 Service 返回的业务对象包装为统一的 Result<T> 响应
 */
@Tag(name = "用户模块", description = "用户注册、登录相关接口")
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody @Validated RegisterDTO dto) {
        log.info("收到注册请求，用户名：{}", dto.getUsername());
        userService.register(dto);
        return Result.success("注册成功");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginDTO dto) {
        log.info("收到登录请求，用户名：{}", dto.getUsername());
        String token = userService.login(dto);
        return Result.success("登录成功", token);
    }
}
