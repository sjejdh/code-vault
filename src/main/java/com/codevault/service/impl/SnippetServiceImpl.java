package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.config.HotSnippetCache;
import com.codevault.dto.SnippetDTO;
import com.codevault.entity.Snippet;
import com.codevault.entity.Tag;
import com.codevault.mapper.SnippetMapper;
import com.codevault.mapper.TagMapper;
import com.codevault.service.SnippetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 代码片段业务逻辑实现类
 * 处理代码片段的创建、更新、删除、查询等核心操作
 */
@Slf4j
@Service
public class SnippetServiceImpl implements SnippetService {

    @Resource
    private SnippetMapper snippetMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private HotSnippetCache hotSnippetCache;

    /**
     * 创建代码片段
     * 1. 设置userId、createTime等基础字段
     * 2. 插入snippet到数据库
     * 3. 处理tagNames：对每个标签名查找或创建tag，然后插入snippet_tag关联
     *
     * @param userId 当前登录用户ID
     * @param dto    片段数据传输对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createSnippet(Long userId, SnippetDTO dto) {
        // 构造片段实体
        Snippet snippet = new Snippet();
        snippet.setUserId(userId);
        snippet.setTitle(dto.getTitle());
        snippet.setDescription(dto.getDescription());
        snippet.setContent(dto.getContent());
        snippet.setLanguage(dto.getLanguage());
        snippet.setCategoryId(dto.getCategoryId());
        snippet.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : 1);
        snippet.setViewCount(0);
        snippet.setLikeCount(0);
        snippet.setCollectCount(0);
        snippet.setStatus(1);
        snippet.setCreateTime(new Date());
        snippet.setUpdateTime(new Date());

        // 插入数据库
        int rows = snippetMapper.insert(snippet);
        if (rows <= 0) {
            throw new BusinessException("创建代码片段失败");
        }

        // 处理标签关联
        processTags(snippet.getId(), dto.getTags());

        // 清除热门片段缓存（新片段可能影响热门排名）
        hotSnippetCache.clearCache();

        log.info("创建代码片段成功，用户ID：{}，片段ID：{}，标题：{}", userId, snippet.getId(), snippet.getTitle());
        return Result.success("创建代码片段成功", snippet);
    }

    /**
     * 更新代码片段
     * 1. 验证snippet属于当前用户
     * 2. 更新snippet信息
     * 3. 先删除旧的snippet_tag关联，再重新关联新标签
     *
     * @param userId    当前登录用户ID
     * @param snippetId 片段ID
     * @param dto       片段数据传输对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateSnippet(Long userId, Long snippetId, SnippetDTO dto) {
        // 查询并验证片段归属
        Snippet existing = snippetMapper.findById(snippetId);
        if (existing == null || existing.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权修改他人的代码片段");
        }

        // 构造更新实体
        Snippet snippet = new Snippet();
        snippet.setId(snippetId);
        snippet.setTitle(dto.getTitle());
        snippet.setDescription(dto.getDescription());
        snippet.setContent(dto.getContent());
        snippet.setLanguage(dto.getLanguage());
        snippet.setCategoryId(dto.getCategoryId());
        snippet.setIsPublic(dto.getIsPublic());
        snippet.setUpdateTime(new Date());

        // 执行更新
        int rows = snippetMapper.update(snippet);
        if (rows <= 0) {
            throw new BusinessException("更新代码片段失败");
        }

        // 处理标签关联：先删除旧关联，再创建新关联
        tagMapper.deleteBySnippetId(snippetId);
        processTags(snippetId, dto.getTags());

        // 清除热门片段缓存（更新可能影响热门排名）
        hotSnippetCache.clearCache();

        log.info("更新代码片段成功，用户ID：{}，片段ID：{}", userId, snippetId);
        return Result.success("更新代码片段成功");
    }

    /**
     * 删除代码片段（逻辑删除，将status设为0）
     * 验证片段属于当前用户后执行删除
     *
     * @param userId    当前登录用户ID
     * @param snippetId 片段ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteSnippet(Long userId, Long snippetId) {
        // 查询并验证片段归属
        Snippet existing = snippetMapper.findById(snippetId);
        if (existing == null || existing.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人的代码片段");
        }

        // 逻辑删除（将status设为0）
        int rows = snippetMapper.deleteById(snippetId);
        if (rows <= 0) {
            throw new BusinessException("删除代码片段失败");
        }

        // 同时删除关联的标签
        tagMapper.deleteBySnippetId(snippetId);

        // 清除热门片段缓存（删除可能影响热门排名）
        hotSnippetCache.clearCache();

        log.info("删除代码片段成功，用户ID：{}，片段ID：{}", userId, snippetId);
        return Result.success("删除代码片段成功");
    }

    /**
     * 获取代码片段详情（公开接口）
     * 1. 查询snippet详情
     * 2. 增加view_count浏览量
     * 3. 查询关联标签
     * 4. 组装返回
     *
     * @param id 片段ID
     * @return 包含片段详情和标签的操作结果
     */
    @Override
    public Result getSnippetDetail(Long id) {
        // 查询片段详情
        Snippet snippet = snippetMapper.findById(id);
        if (snippet == null || snippet.getStatus() == 0) {
            throw new BusinessException("代码片段不存在");
        }

        // 非公开片段不允许通过公开接口查看
        if (snippet.getIsPublic() != 1) {
            throw new BusinessException("该代码片段为私密片段，无法查看");
        }

        // 增加浏览量
        snippetMapper.updateViewCount(id);

        // 查询关联标签
        List<Tag> tagList = tagMapper.findBySnippetId(id);
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : tagList) {
            tagNames.add(tag.getName());
        }
        snippet.setTags(tagNames);

        // 更新片段的浏览量（返回给前端时显示最新值）
        snippet.setViewCount(snippet.getViewCount() + 1);

        log.info("查看代码片段详情，片段ID：{}", id);
        return Result.success(snippet);
    }

    /**
     * 分页查询公开片段
     * 1. 计算offset = (page - 1) * pageSize
     * 2. 查询列表和总数
     * 3. 对每个snippet查询关联标签
     * 4. 返回分页数据
     *
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param language   编程语言
     * @param page       页码（从1开始）
     * @param pageSize   每页大小
     * @return 分页结果
     */
    @Override
    public Result getPublicSnippets(String keyword, Long categoryId, String language,
                                   Integer page, Integer pageSize) {
        // 设置默认分页参数
        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        // 查询列表和总数
        List<Snippet> snippets = snippetMapper.findPublicSnippets(keyword, categoryId, language, offset, size);
        int total = snippetMapper.countPublicSnippets(keyword, categoryId, language);

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

        log.info("分页查询公开片段，关键词：{}，分类：{}，语言：{}，页码：{}，每页：{}，总计：{}",
                keyword, categoryId, language, currentPage, size, total);
        return Result.success(data);
    }

    /**
     * 查询热门片段（按浏览量降序）
     * 优先从Redis缓存获取，缓存未命中则从数据库查询
     *
     * @param limit 返回条数
     * @return 热门片段列表
     */
    @Override
    public Result getHotSnippets(Integer limit) {
        int count = (limit != null && limit > 0) ? limit : 10;

        // 先从缓存获取热门片段
        List<Snippet> snippets = hotSnippetCache.getHotSnippets();

        // 根据请求的limit截取返回条数
        if (snippets.size() > count) {
            snippets = snippets.subList(0, count);
        }

        // 为每个片段查询关联标签
        for (Snippet snippet : snippets) {
            List<Tag> tagList = tagMapper.findBySnippetId(snippet.getId());
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tagList) {
                tagNames.add(tag.getName());
            }
            snippet.setTags(tagNames);
        }

        log.info("查询热门片段，条数：{}", count);
        return Result.success(snippets);
    }

    /**
     * 查询用户自己的片段（分页）
     *
     * @param userId   用户ID
     * @param page     页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public Result getUserSnippets(Long userId, Integer page, Integer pageSize) {
        // 设置默认分页参数
        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        // 查询列表和总数
        List<Snippet> snippets = snippetMapper.findByUserId(userId, offset, size);
        int total = snippetMapper.countByUserId(userId);

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

        log.info("查询用户片段，用户ID：{}，页码：{}，每页：{}，总计：{}", userId, currentPage, size, total);
        return Result.success(data);
    }

    /**
     * 处理标签关联：对每个标签名查找或创建tag，然后插入snippet_tag关联
     *
     * @param snippetId 代码片段ID
     * @param tagNames  标签名称列表
     */
    private void processTags(Long snippetId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String tagName : tagNames) {
            // 去除空白标签名
            String trimmedName = tagName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }

            // 查找标签，不存在则创建
            Tag tag = tagMapper.findByName(trimmedName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(trimmedName);
                tagMapper.insert(tag);
            }

            // 插入片段-标签关联
            tagMapper.insertSnippetTag(snippetId, tag.getId());
        }
    }
}
