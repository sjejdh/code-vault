package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.entity.User;
import com.codevault.mapper.UserMapper;
import com.codevault.service.UserService;
import com.codevault.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 构造方法注入依赖
     */
    public UserServiceImpl(UserMapper userMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户注册
     * 1. 检查用户名是否已存在
     * 2. 使用BCrypt加密密码
     * 3. 构建User对象并插入数据库
     * 4. 返回注册成功结果
     */
    @Override
    public Result register(RegisterDTO dto) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 使用BCrypt加密密码
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 构建User对象
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setRole("USER");
        user.setStatus(1);

        // 插入数据库
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("注册失败，请稍后重试");
        }

        log.info("用户注册成功：{}", dto.getUsername());
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     * 1. 根据用户名查询用户
     * 2. 校验密码
     * 3. 检查用户状态
     * 4. 生成JWT令牌
     * 5. 返回登录成功结果（携带token）
     */
    @Override
    public Result login(LoginDTO dto) {
        // 根据用户名查询用户
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 检查用户状态（0-禁用，1-启用）
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功：{}", dto.getUsername());

        return Result.success("登录成功", token);
    }

    /**
     * 根据用户ID查询用户
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
