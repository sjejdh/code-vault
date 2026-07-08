package com.codevault.service;

import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.entity.User;

/**
 * 用户服务接口
 * Service 层返回业务对象，由 Controller 层统一包装为 Result<T>
 */
public interface UserService {

    /**
     * 用户注册
     * @param dto 注册请求参数
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录请求参数
     * @return JWT令牌
     */
    String login(LoginDTO dto);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);
}
