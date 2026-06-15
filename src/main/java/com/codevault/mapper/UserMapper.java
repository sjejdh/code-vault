package com.codevault.mapper;

import com.codevault.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，不存在则返回null
     */
    User findByUsername(String username);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户对象，不存在则返回null
     */
    User findById(Long id);

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int insert(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);
}
