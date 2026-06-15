package com.codevault.mapper;

import com.codevault.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞数据访问层接口
 * 提供点赞的增删查操作
 */
@Mapper
public interface LikeMapper {

    /**
     * 新增点赞记录
     * @param like 点赞实体
     * @return 影响行数
     */
    int insert(Like like);

    /**
     * 查询用户是否已点赞指定片段
     * @param userId    用户ID
     * @param snippetId 片段ID
     * @return 点赞记录（未点赞则返回null）
     */
    Like findByUserAndSnippet(@Param("userId") Long userId, @Param("snippetId") Long snippetId);

    /**
     * 取消点赞（根据用户ID和片段ID删除）
     * @param userId    用户ID
     * @param snippetId 片段ID
     * @return 影响行数
     */
    int deleteByUserAndSnippet(@Param("userId") Long userId, @Param("snippetId") Long snippetId);
}
