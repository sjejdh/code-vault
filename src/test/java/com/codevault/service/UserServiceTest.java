package com.codevault.service;

import com.codevault.common.exception.BusinessException;
import com.codevault.dto.LoginDTO;
import com.codevault.dto.RegisterDTO;
import com.codevault.entity.User;
import com.codevault.mapper.UserMapper;
import com.codevault.service.impl.UserServiceImpl;
import com.codevault.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 使用 Mockito 模拟依赖，验证业务逻辑正确性
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("测试用户");

        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$encodedpassword");
        mockUser.setNickname("测试用户");
        mockUser.setRole("USER");
        mockUser.setStatus(1);
    }

    @Test
    @DisplayName("注册成功 - 昵称为空时使用用户名作为昵称")
    void register_success_whenNicknameIsNull() {
        registerDTO.setNickname(null);
        when(userMapper.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.register(registerDTO));

        verify(userMapper).findByUsername("testuser");
        verify(userMapper).insert(argThat(user ->
                user.getNickname().equals("testuser") &&
                user.getRole().equals("USER") &&
                user.getStatus() == 1
        ));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_fail_whenUsernameExists() {
        when(userMapper.findByUsername("testuser")).thenReturn(mockUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(registerDTO));
        assertEquals("用户名已存在", exception.getMessage());

        verify(userMapper, never()).insert(any());
    }

    @Test
    @DisplayName("注册失败 - 数据库插入失败")
    void register_fail_whenInsertFails() {
        when(userMapper.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(userMapper.insert(any(User.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(registerDTO));
        assertEquals("注册失败，请稍后重试", exception.getMessage());
    }

    @Test
    @DisplayName("登录成功 - 返回JWT令牌")
    void login_success() {
        when(userMapper.findByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedpassword")).thenReturn(true);
        when(jwtUtils.generateToken(1L, "testuser")).thenReturn("mock-jwt-token");

        String token = userService.login(loginDTO);
        assertEquals("mock-jwt-token", token);

        verify(jwtUtils).generateToken(1L, "testuser");
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_fail_whenUserNotFound() {
        when(userMapper.findByUsername("testuser")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(loginDTO));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_fail_whenPasswordWrong() {
        when(userMapper.findByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedpassword")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(loginDTO));
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 账号已被禁用")
    void login_fail_whenAccountDisabled() {
        mockUser.setStatus(0);
        when(userMapper.findByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedpassword")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(loginDTO));
        assertEquals("账号已被禁用", exception.getMessage());
    }
}
