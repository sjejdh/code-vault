package com.codevault.controller;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.entity.UserCollection;
import com.codevault.entity.Like;
import com.codevault.entity.Snippet;
import com.codevault.entity.SnippetTagRelation;
import com.codevault.mapper.CollectionMapper;
import com.codevault.mapper.LikeMapper;
import com.codevault.mapper.SnippetMapper;
import com.codevault.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 互动控制器
 * 提供点赞、收藏、取消点赞、取消收藏等REST接口
 * 所有接口均需登录（JWT认证）
 */
@Tag(name = "互动模块", description = "点赞、收藏相关接口")
@Slf4j
@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private SnippetMapper snippetMapper;

    @Resource
    private TagMapper tagMapper;

    @Operation(summary = "点赞代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/like/{snippetId}")
    public Result likeSnippet(HttpServletRequest request,
                              @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        Snippet snippet = snippetMapper.findById(snippetId);
        if (snippet == null || snippet.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }

        Like existing = likeMapper.findByUserAndSnippet(userId, snippetId);
        if (existing != null) {
            throw new BusinessException("已点赞，请勿重复操作");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setSnippetId(snippetId);
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);

        snippetMapper.updateLikeCount(snippetId);

        log.info("用户{}点赞片段{}", userId, snippetId);
        return Result.success("点赞成功");
    }

    @Operation(summary = "取消点赞")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/like/{snippetId}")
    public Result unlikeSnippet(HttpServletRequest request,
                                @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        Like existing = likeMapper.findByUserAndSnippet(userId, snippetId);
        if (existing == null) {
            throw new BusinessException("尚未点赞");
        }

        likeMapper.deleteByUserAndSnippet(userId, snippetId);
        snippetMapper.updateLikeCountMinus(snippetId);

        log.info("用户{}取消点赞片段{}", userId, snippetId);
        return Result.success("取消点赞成功");
    }

    @Operation(summary = "收藏代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/collect/{snippetId}")
    public Result collectSnippet(HttpServletRequest request,
                                 @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        Snippet snippet = snippetMapper.findById(snippetId);
        if (snippet == null || snippet.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }

        UserCollection existing = collectionMapper.findByUserAndSnippet(userId, snippetId);
        if (existing != null) {
            throw new BusinessException("已收藏，请勿重复操作");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setSnippetId(snippetId);
        collection.setCreateTime(LocalDateTime.now());
        collectionMapper.insert(collection);

        snippetMapper.updateCollectCount(snippetId);

        log.info("用户{}收藏片段{}", userId, snippetId);
        return Result.success("收藏成功");
    }

    @Operation(summary = "取消收藏")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/collect/{snippetId}")
    public Result uncollectSnippet(HttpServletRequest request,
                                  @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        UserCollection existing = collectionMapper.findByUserAndSnippet(userId, snippetId);
        if (existing == null) {
            throw new BusinessException("尚未收藏");
        }

        collectionMapper.deleteByUserAndSnippet(userId, snippetId);
        snippetMapper.updateCollectCountMinus(snippetId);

        log.info("用户{}取消收藏片段{}", userId, snippetId);
        return Result.success("取消收藏成功");
    }

    @Operation(summary = "查询我的收藏列表")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/collect/my")
    public Result getMyCollections(HttpServletRequest request,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");

        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        List<Snippet> snippets = collectionMapper.findUserCollections(userId, offset, size);
        int total = collectionMapper.countUserCollections(userId);

        // 批量填充标签（解决N+1查询问题）
        fillTagsForSnippets(snippets);

        Map<String, Object> data = new HashMap<>();
        data.put("list", snippets);
        data.put("total", total);
        data.put("page", currentPage);
        data.put("pageSize", size);

        log.info("查询用户收藏列表，用户ID：{}，页码：{}，每页：{}，总计：{}", userId, currentPage, size, total);
        return Result.success(data);
    }

    /**
     * 批量填充片段的标签列表（解决N+1查询问题）
     */
    private void fillTagsForSnippets(List<Snippet> snippets) {
        if (snippets == null || snippets.isEmpty()) {
            return;
        }

        List<Long> snippetIds = snippets.stream()
                .map(Snippet::getId)
                .collect(Collectors.toList());

        List<SnippetTagRelation> tagRelations = tagMapper.findBySnippetIds(snippetIds);

        Map<Long, List<String>> tagMap = new HashMap<>();
        for (SnippetTagRelation relation : tagRelations) {
            Long snippetId = relation.getSnippetId();
            String tagName = relation.getTagName();
            tagMap.computeIfAbsent(snippetId, k -> new ArrayList<>()).add(tagName);
        }

        for (Snippet snippet : snippets) {
            snippet.setTags(tagMap.getOrDefault(snippet.getId(), new ArrayList<>()));
        }
    }
}
