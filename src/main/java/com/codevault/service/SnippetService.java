package com.codevault.service;

import com.codevault.common.result.Result;
import com.codevault.dto.SnippetDTO;
import com.codevault.entity.Snippet;

/**
 * 代码片段业务逻辑接口
 * 定义代码片段模块的核心业务方法
 */
public interface SnippetService {

    /**
     * 创建代码片段
     * @param userId   当前登录用户ID
     * @param dto      片段数据传输对象
     * @return 操作结果
     */
    Result createSnippet(Long userId, SnippetDTO dto);

    /**
     * 更新代码片段
     * @param userId   当前登录用户ID
     * @param snippetId 片段ID
     * @param dto      片段数据传输对象
     * @return 操作结果
     */
    Result updateSnippet(Long userId, Long snippetId, SnippetDTO dto);

    /**
     * 删除代码片段（逻辑删除）
     * @param userId    当前登录用户ID
     * @param snippetId 片段ID
     * @return 操作结果
     */
    Result deleteSnippet(Long userId, Long snippetId);

    /**
     * 获取代码片段详情（公开接口，增加浏览量）
     * @param id 片段ID
     * @return 操作结果（包含片段详情和标签）
     */
    Result getSnippetDetail(Long id);

    /**
     * 分页查询公开片段
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param language   编程语言
     * @param page       页码（从1开始）
     * @param pageSize   每页大小
     * @return 分页结果
     */
    Result getPublicSnippets(String keyword, Long categoryId, String language,
                             Integer page, Integer pageSize);

    /**
     * 查询热门片段
     * @param limit 返回条数
     * @return 热门片段列表
     */
    Result getHotSnippets(Integer limit);

    /**
     * 查询用户自己的片段
     * @param userId   用户ID
     * @param page     页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Result getUserSnippets(Long userId, Integer page, Integer pageSize);
}
