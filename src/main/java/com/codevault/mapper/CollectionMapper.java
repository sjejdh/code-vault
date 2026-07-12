package com.codevault.mapper;

import com.codevault.entity.UserCollection;
import com.codevault.entity.Snippet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏数据访问层接口
 * 提供收藏的增删改查操作
 */
@Mapper
public interface CollectionMapper {

    /**
     * 新增收藏记录
     * @param collection 收藏实体
     * @return 影响行数
     */
    int insert(UserCollection collection);

    /**
     * 查询用户是否已收藏指定片段
     * @param userId    用户ID
     * @param snippetId 片段ID
     * @return 收藏记录（未收藏则返回null）
     */
    UserCollection findByUserAndSnippet(@Param("userId") Long userId, @Param("snippetId") Long snippetId);

    /**
     * 取消收藏（根据用户ID和片段ID删除）
     * @param userId    用户ID
     * @param snippetId 片段ID
     * @return 影响行数
     */
    int deleteByUserAndSnippet(@Param("userId") Long userId, @Param("snippetId") Long snippetId);

    /**
     * 查询用户已收藏的片段ID列表
     * @param userId 用户ID
     * @return 片段ID列表
     */
    List<Long> findCollectedSnippetIds(Long userId);

    /**
     * 分页查询用户的收藏列表
     * @param userId   用户ID
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @return 收藏的片段列表
     */
    List<Snippet> findUserCollections(@Param("userId") Long userId,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);

    /**
     * 统计用户的收藏总数
     * @param userId 用户ID
     * @return 收藏总数
     */
    int countUserCollections(Long userId);
}
