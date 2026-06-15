package com.codevault.mapper;

import com.codevault.entity.Snippet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码片段数据访问层接口
 * 提供代码片段的增删改查、分页查询、统计等操作
 */
@Mapper
public interface SnippetMapper {

    /**
     * 根据ID查询代码片段
     * @param id 片段ID
     * @return 片段信息
     */
    Snippet findById(Long id);

    /**
     * 分页查询公开片段（带搜索、分类、语言筛选）
     * @param keyword    搜索关键词（匹配标题和描述）
     * @param categoryId 分类ID（可选）
     * @param language   编程语言（可选）
     * @param offset     偏移量
     * @param pageSize   每页大小
     * @return 片段列表
     */
    List<Snippet> findPublicSnippets(@Param("keyword") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("language") String language,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);

    /**
     * 统计公开片段总数（带筛选条件）
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param language   编程语言
     * @return 总数
     */
    int countPublicSnippets(@Param("keyword") String keyword,
                            @Param("categoryId") Long categoryId,
                            @Param("language") String language);

    /**
     * 分页查询指定用户的片段
     * @param userId   用户ID
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @return 片段列表
     */
    List<Snippet> findByUserId(@Param("userId") Long userId,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    /**
     * 统计指定用户的片段总数
     * @param userId 用户ID
     * @return 总数
     */
    int countByUserId(Long userId);

    /**
     * 查询热门片段（按浏览量降序，取前N条）
     * @param limit 返回条数
     * @return 热门片段列表
     */
    List<Snippet> findHotSnippets(@Param("limit") int limit);

    /**
     * 新增代码片段
     * @param snippet 片段实体
     * @return 影响行数
     */
    int insert(Snippet snippet);

    /**
     * 更新代码片段信息
     * @param snippet 片段实体
     * @return 影响行数
     */
    int update(Snippet snippet);

    /**
     * 浏览量+1
     * @param id 片段ID
     * @return 影响行数
     */
    int updateViewCount(Long id);

    /**
     * 点赞数+1
     * @param id 片段ID
     * @return 影响行数
     */
    int updateLikeCount(Long id);

    /**
     * 收藏数+1
     * @param id 片段ID
     * @return 影响行数
     */
    int updateCollectCount(Long id);

    /**
     * 点赞数-1（使用GREATEST确保不小于0）
     * @param id 片段ID
     * @return 影响行数
     */
    int updateLikeCountMinus(Long id);

    /**
     * 收藏数-1（使用GREATEST确保不小于0）
     * @param id 片段ID
     * @return 影响行数
     */
    int updateCollectCountMinus(Long id);

    /**
     * 根据ID删除代码片段
     * @param id 片段ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
