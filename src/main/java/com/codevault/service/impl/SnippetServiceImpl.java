package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.config.HotSnippetCache;
import com.codevault.dto.SnippetDTO;
import com.codevault.entity.Snippet;
import com.codevault.entity.TagEntity;
import com.codevault.mapper.SnippetMapper;
import com.codevault.mapper.TagMapper;
import com.codevault.service.SnippetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
     * @return 创建后的片段对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Snippet createSnippet(Long userId, SnippetDTO dto) {
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
        LocalDateTime now = LocalDateTime.now();
        snippet.setCreateTime(now);
        snippet.setUpdateTime(now);

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
        return snippet;
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
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSnippet(Long userId, Long snippetId, SnippetDTO dto) {
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
        snippet.setUpdateTime(LocalDateTime.now());

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
    }

    /**
     * 删除代码片段（逻辑删除，将status设为0）
     * 验证片段属于当前用户后执行删除
     *
     * @param userId    当前登录用户ID
     * @param snippetId 片段ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSnippet(Long userId, Long snippetId) {
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
    }

    /**
     * 获取代码片段详情（公开接口）
     * 1. 查询snippet详情
     * 2. 增加view_count浏览量
     * 3. 查询关联标签
     * 4. 组装返回
     *
     * @param id 片段ID
     * @return 片段详情（含标签）
     */
    @Override
    public Snippet getSnippetDetail(Long id) {
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
        fillTagsForSnippets(Collections.singletonList(snippet));

        // 更新片段的浏览量（返回给前端时显示最新值）
        snippet.setViewCount(snippet.getViewCount() + 1);

        log.info("查看代码片段详情，片段ID：{}", id);
        return snippet;
    }

    /**
     * 分页查询公开片段
     * 1. 计算offset = (page - 1) * pageSize
     * 2. 查询列表和总数
     * 3. 批量查询标签（避免N+1问题）
     * 4. 返回分页数据
     *
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param language   编程语言
     * @param page       页码（从1开始）
     * @param pageSize   每页大小
     * @return 分页数据Map
     */
    @Override
    public Map<String, Object> getPublicSnippets(String keyword, Long categoryId, String language,
                                                  Integer page, Integer pageSize) {
        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        List<Snippet> snippets = snippetMapper.findPublicSnippets(keyword, categoryId, language, offset, size);
        int total = snippetMapper.countPublicSnippets(keyword, categoryId, language);

        // 批量填充标签（解决N+1查询问题）
        fillTagsForSnippets(snippets);

        Map<String, Object> data = new HashMap<>();
        data.put("list", snippets);
        data.put("total", total);
        data.put("page", currentPage);
        data.put("pageSize", size);

        log.info("分页查询公开片段，关键词：{}，分类：{}，语言：{}，页码：{}，每页：{}，总计：{}",
                keyword, categoryId, language, currentPage, size, total);
        return data;
    }

    /**
     * 查询热门片段（按浏览量降序）
     * 优先从Redis缓存获取，缓存未命中则从数据库查询
     *
     * @param limit 返回条数
     * @return 热门片段列表
     */
    @Override
    public List<Snippet> getHotSnippets(Integer limit) {
        int count = (limit != null && limit > 0) ? limit : 10;

        List<Snippet> snippets = hotSnippetCache.getHotSnippets();
        if (snippets.size() > count) {
            snippets = snippets.subList(0, count);
        }

        // 批量填充标签（解决N+1查询问题）
        fillTagsForSnippets(snippets);

        log.info("查询热门片段，条数：{}", count);
        return snippets;
    }

    /**
     * 查询用户自己的片段（分页）
     *
     * @param userId   用户ID
     * @param page     页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页数据Map
     */
    @Override
    public Map<String, Object> getUserSnippets(Long userId, Integer page, Integer pageSize) {
        int currentPage = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        int offset = (currentPage - 1) * size;

        List<Snippet> snippets = snippetMapper.findByUserId(userId, offset, size);
        int total = snippetMapper.countByUserId(userId);

        // 批量填充标签（解决N+1查询问题）
        fillTagsForSnippets(snippets);

        Map<String, Object> data = new HashMap<>();
        data.put("list", snippets);
        data.put("total", total);
        data.put("page", currentPage);
        data.put("pageSize", size);

        log.info("查询用户片段，用户ID：{}，页码：{}，每页：{}，总计：{}", userId, currentPage, size, total);
        return data;
    }

    /**
     * 批量填充片段的标签列表（解决N+1查询问题）
     * 一次查询所有片段的关联标签，然后在内存中按snippetId分组装配
     *
     * @param snippets 需要填充标签的片段列表
     */
    private void fillTagsForSnippets(List<Snippet> snippets) {
        if (snippets == null || snippets.isEmpty()) {
            return;
        }

        // 提取所有片段ID
        List<Long> snippetIds = snippets.stream()
                .map(Snippet::getId)
                .collect(Collectors.toList());

        // 批量查询所有标签关联
        List<Map<String, Object>> tagRelations = tagMapper.findBySnippetIds(snippetIds);

        // 按 snippetId 分组
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (Map<String, Object> relation : tagRelations) {
            Long snippetId = ((Number) relation.get("snippetId")).longValue();
            String tagName = (String) relation.get("tagName");
            tagMap.computeIfAbsent(snippetId, k -> new ArrayList<>()).add(tagName);
        }

        // 为每个片段设置标签列表
        for (Snippet snippet : snippets) {
            snippet.setTags(tagMap.getOrDefault(snippet.getId(), new ArrayList<>()));
        }
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
            String trimmedName = tagName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }

            TagEntity tag = tagMapper.findByName(trimmedName);
            if (tag == null) {
                tag = new TagEntity();
                tag.setName(trimmedName);
                tagMapper.insert(tag);
            }

            tagMapper.insertSnippetTag(snippetId, tag.getId());
        }
    }
}
