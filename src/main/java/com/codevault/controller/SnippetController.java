package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.dto.SnippetDTO;
import com.codevault.service.AiService;
import com.codevault.service.SnippetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 代码片段控制器
 * 提供代码片段的增删改查REST接口
 * 公开接口路径为 /api/snippet/public/**，在JwtInterceptor中已排除拦截
 * Controller 层负责将 Service 返回的业务对象包装为统一的 Result<T> 响应
 */
@Tag(name = "代码片段模块", description = "代码片段的查询、创建、更新、删除")
@Slf4j
@RestController
@RequestMapping("/api/snippet")
public class SnippetController {

    @Resource
    private SnippetService snippetService;

    @Resource
    private AiService aiService;

    @Operation(summary = "分页查询公开片段")
    @GetMapping("/public")
    public Result getPublicSnippets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("分页查询公开片段，关键词：{}，分类：{}，语言：{}", keyword, categoryId, language);
        Map<String, Object> data = snippetService.getPublicSnippets(keyword, categoryId, language, page, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "查询热门片段")
    @GetMapping("/public/hot")
    public Result getHotSnippets(@RequestParam(required = false) Integer limit) {
        log.info("查询热门片段，条数：{}", limit);
        List<?> snippets = snippetService.getHotSnippets(limit);
        return Result.success(snippets);
    }

    @Operation(summary = "获取片段详情")
    @GetMapping("/public/{id}")
    public Result getSnippetDetail(@PathVariable Long id) {
        log.info("查看公开片段详情，片段ID：{}", id);
        return Result.success(snippetService.getSnippetDetail(id));
    }

    @Operation(summary = "AI 解释代码片段")
    @GetMapping("/public/{id}/explain")
    public Result explainCode(@PathVariable Long id) {
        log.info("AI 解释代码片段，片段ID：{}", id);
        var snippet = snippetService.getSnippetDetail(id);
        if (snippet == null) {
            return Result.error("代码片段不存在");
        }
        String explanation = aiService.explainCode(id, snippet.getContent(), snippet.getLanguage());
        return Result.success(explanation);
    }

    @Operation(summary = "查询我的片段")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/my")
    public Result getMySnippets(HttpServletRequest request,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我的片段，用户ID：{}", userId);
        Map<String, Object> data = snippetService.getUserSnippets(userId, page, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "创建代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public Result createSnippet(HttpServletRequest request,
                                @RequestBody @Validated SnippetDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("创建代码片段，用户ID：{}，标题：{}", userId, dto.getTitle());
        return Result.success("创建代码片段成功", snippetService.createSnippet(userId, dto));
    }

    @Operation(summary = "更新代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/{id}")
    public Result updateSnippet(HttpServletRequest request,
                                @PathVariable("id") Long snippetId,
                                @RequestBody @Validated SnippetDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新代码片段，用户ID：{}，片段ID：{}", userId, snippetId);
        snippetService.updateSnippet(userId, snippetId, dto);
        return Result.success("更新代码片段成功");
    }

    @Operation(summary = "删除代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{id}")
    public Result deleteSnippet(HttpServletRequest request,
                                @PathVariable("id") Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除代码片段，用户ID：{}，片段ID：{}", userId, snippetId);
        snippetService.deleteSnippet(userId, snippetId);
        return Result.success("删除代码片段成功");
    }
}
