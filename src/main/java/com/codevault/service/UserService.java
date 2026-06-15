package com.codevault.service;

import com.codevault.common.result.Result;
import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param dto 注册请求参数
     * @return 统一响应结果
     */
    Result register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录请求参数
     * @return 统一响应结果（包含token）
     */
    Result login(LoginDTO dto);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);
}
