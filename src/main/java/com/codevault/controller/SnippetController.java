package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.dto.SnippetDTO;
import com.codevault.service.SnippetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 代码片段控制器
 * 提供代码片段的增删改查REST接口
 * 公开接口路径为 /api/snippet/public/**，在JwtInterceptor中已排除拦截
 */
@Slf4j
@RestController
@RequestMapping("/api/snippet")
public class SnippetController {

    @Resource
    private SnippetService snippetService;

    /**
     * 分页查询公开片段（无需登录）
     * 支持关键词搜索、分类和编程语言筛选
     *
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param language   编程语言
     * @param page       页码（默认1）
     * @param pageSize   每页大小（默认10）
     * @return 分页结果
     */
    @GetMapping("/public")
    public Result getPublicSnippets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("分页查询公开片段，关键词：{}，分类：{}，语言：{}", keyword, categoryId, language);
        return snippetService.getPublicSnippets(keyword, categoryId, language, page, pageSize);
    }

    /**
     * 查询热门片段（无需登录）
     *
     * @param limit 返回条数（默认10）
     * @return 热门片段列表
     */
    @GetMapping("/public/hot")
    public Result getHotSnippets(@RequestParam(required = false) Integer limit) {
        log.info("查询热门片段，条数：{}", limit);
        return snippetService.getHotSnippets(limit);
    }

    /**
     * 获取公开片段详情（无需登录）
     * 每次访问会增加浏览量
     *
     * @param id 片段ID
     * @return 片段详情
     */
    @GetMapping("/public/{id}")
    public Result getSnippetDetail(@PathVariable Long id) {
        log.info("查看公开片段详情，片段ID：{}", id);
        return snippetService.getSnippetDetail(id);
    }

    /**
     * 查询我的代码片段（需登录）
     * 从request attribute中获取当前登录用户ID
     *
     * @param request   HTTP请求（包含userId属性）
     * @param page      页码（默认1）
     * @param pageSize  每页大小（默认10）
     * @return 我的片段分页列表
     */
    @GetMapping("/my")
    public Result getMySnippets(HttpServletRequest request,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我的片段，用户ID：{}", userId);
        return snippetService.getUserSnippets(userId, page, pageSize);
    }

    /**
     * 创建代码片段（需登录）
     *
     * @param request HTTP请求（包含userId属性）
     * @param dto     代码片段数据（带参数校验）
     * @return 操作结果
     */
    @PostMapping
    public Result createSnippet(HttpServletRequest request,
                                @RequestBody @Validated SnippetDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("创建代码片段，用户ID：{}，标题：{}", userId, dto.getTitle());
        return snippetService.createSnippet(userId, dto);
    }

    /**
     * 更新代码片段（需登录，只能更新自己的片段）
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @param dto      代码片段数据（带参数校验）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result updateSnippet(HttpServletRequest request,
                                @PathVariable("id") Long snippetId,
                                @RequestBody @Validated SnippetDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新代码片段，用户ID：{}，片段ID：{}", userId, snippetId);
        return snippetService.updateSnippet(userId, snippetId, dto);
    }

    /**
     * 删除代码片段（需登录，只能删除自己的片段）
     * 逻辑删除，将status设为0
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result deleteSnippet(HttpServletRequest request,
                                @PathVariable("id") Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除代码片段，用户ID：{}，片段ID：{}", userId, snippetId);
        return snippetService.deleteSnippet(userId, snippetId);
    }
}
