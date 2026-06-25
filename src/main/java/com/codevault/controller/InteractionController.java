package com.codevault.controller;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.entity.Collection;
import com.codevault.entity.Like;
import com.codevault.entity.Snippet;
import com.codevault.entity.Tag;
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
import java.util.*;

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

    /**
     * 点赞代码片段
     * 检查是否已点赞，未点赞则插入点赞记录并更新片段的like_count+1
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @Operation(summary = "点赞代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/like/{snippetId}")
    public Result likeSnippet(HttpServletRequest request,
                              @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        // 检查片段是否存在
        Snippet snippet = snippetMapper.findById(snippetId);
        if (snippet == null || snippet.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }

        // 检查是否已点赞
        Like existing = likeMapper.findByUserAndSnippet(userId, snippetId);
        if (existing != null) {
            throw new BusinessException("已点赞，请勿重复操作");
        }

        // 插入点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setSnippetId(snippetId);
        like.setCreateTime(new Date());
        likeMapper.insert(like);

        // 更新片段点赞数+1
        snippetMapper.updateLikeCount(snippetId);

        log.info("用户{}点赞片段{}", userId, snippetId);
        return Result.success("点赞成功");
    }

    /**
     * 取消点赞
     * 删除点赞记录并更新片段的like_count-1
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @Operation(summary = "取消点赞")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/like/{snippetId}")
    public Result unlikeSnippet(HttpServletRequest request,
                                @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        // 检查是否已点赞
        Like existing = likeMapper.findByUserAndSnippet(userId, snippetId);
        if (existing == null) {
            throw new BusinessException("尚未点赞");
        }

        // 删除点赞记录
        likeMapper.deleteByUserAndSnippet(userId, snippetId);

        // 更新片段点赞数-1（使用SQL直接-1，避免count为0时出现负数）
        // 通过update语句实现：UPDATE snippet SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}
        // 此处简化处理，直接调用-1方法
        snippetMapper.updateLikeCountMinus(snippetId);

        log.info("用户{}取消点赞片段{}", userId, snippetId);
        return Result.success("取消点赞成功");
    }

    /**
     * 收藏代码片段
     * 检查是否已收藏，未收藏则插入收藏记录并更新片段的collect_count+1
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @Operation(summary = "收藏代码片段")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/collect/{snippetId}")
    public Result collectSnippet(HttpServletRequest request,
                                 @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        // 检查片段是否存在
        Snippet snippet = snippetMapper.findById(snippetId);
        if (snippet == null || snippet.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }

        // 检查是否已收藏
        Collection existing = collectionMapper.findByUserAndSnippet(userId, snippetId);
        if (existing != null) {
            throw new BusinessException("已收藏，请勿重复操作");
        }

        // 插入收藏记录
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setSnippetId(snippetId);
        collection.setCreateTime(new Date());
        collectionMapper.insert(collection);

        // 更新片段收藏数+1
        snippetMapper.updateCollectCount(snippetId);

        log.info("用户{}收藏片段{}", userId, snippetId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     * 删除收藏记录并更新片段的collect_count-1
     *
     * @param request   HTTP请求（包含userId属性）
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @Operation(summary = "取消收藏")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/collect/{snippetId}")
    public Result uncollectSnippet(HttpServletRequest request,
                                  @PathVariable Long snippetId) {
        Long userId = (Long) request.getAttribute("userId");

        // 检查是否已收藏
        Collection existing = collectionMapper.findByUserAndSnippet(userId, snippetId);
        if (existing == null) {
            throw new BusinessException("尚未收藏");
        }

        // 删除收藏记录
        collectionMapper.deleteByUserAndSnippet(userId, snippetId);

        // 更新片段收藏数-1
        snippetMapper.updateCollectCountMinus(snippetId);

        log.info("用户{}取消收藏片段{}", userId, snippetId);
        return Result.success("取消收藏成功");
    }

    /**
     * 查询我的收藏列表（分页）
     * 返回用户收藏的所有代码片段，每个片段附带关联标签
     *
     * @param request  HTTP请求（包含userId属性）
     * @param page     页码（默认1）
     * @param pageSize 每页大小（默认10）
     * @return 收藏的分页列表
     */
    @Operation(summary = "查询我的收藏列表")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/collect/my")
    public Result getMyCollections(HttpServletRequest request,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");

        // 设置默认分页参数
        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        // 查询收藏列表和总数
        List<Snippet> snippets = collectionMapper.findUserCollections(userId, offset, size);
        int total = collectionMapper.countUserCollections(userId);

        // 为每个片段查询关联标签
        for (Snippet snippet : snippets) {
            List<Tag> tagList = tagMapper.findBySnippetId(snippet.getId());
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tagList) {
                tagNames.add(tag.getName());
            }
            snippet.setTags(tagNames);
        }

        // 组装分页数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", snippets);
        data.put("total", total);
        data.put("page", currentPage);
        data.put("pageSize", size);

        log.info("查询用户收藏列表，用户ID：{}，页码：{}，每页：{}，总计：{}", userId, currentPage, size, total);
        return Result.success(data);
    }
}
