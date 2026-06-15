package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.entity.Tag;
import com.codevault.mapper.TagMapper;
import com.codevault.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 标签业务逻辑实现类
 * 处理标签的创建和查询操作
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    /**
     * 查询所有标签
     * @return 包含标签列表的成功响应
     */
    @Override
    public Result findAll() {
        List<Tag> tags = tagMapper.findAll();
        log.info("查询所有标签，共{}条", tags.size());
        return Result.success(tags);
    }

    /**
     * 创建标签
     * 如果标签已存在则直接返回已有的标签，避免重复创建
     * @param name 标签名称
     * @return 操作结果（包含标签信息）
     */
    @Override
    public Result create(String name) {
        // 参数校验
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("标签名称不能为空");
        }

        String tagName = name.trim();

        // 检查标签是否已存在
        Tag existingTag = tagMapper.findByName(tagName);
        if (existingTag != null) {
            log.info("标签已存在，直接返回，名称：{}，ID：{}", tagName, existingTag.getId());
            return Result.success("标签已存在", existingTag);
        }

        // 创建新标签
        Tag tag = new Tag();
        tag.setName(tagName);

        int rows = tagMapper.insert(tag);
        if (rows <= 0) {
            throw new BusinessException("创建标签失败");
        }

        log.info("创建标签成功，名称：{}，ID：{}", tagName, tag.getId());
        return Result.success("创建标签成功", tag);
    }

    /**
     * 查询指定代码片段关联的标签
     * @param snippetId 代码片段ID
     * @return 包含标签列表的成功响应
     */
    @Override
    public Result findBySnippetId(Long snippetId) {
        List<Tag> tags = tagMapper.findBySnippetId(snippetId);
        log.info("查询片段关联标签，片段ID：{}，标签数：{}", snippetId, tags.size());
        return Result.success(tags);
    }
}
