package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.entity.TagEntity;
import com.codevault.mapper.TagMapper;
import com.codevault.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 标签业务逻辑实现类
 * Service 层返回业务对象，由 Controller 层统一包装为 Result<T>
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    public List<TagEntity> findAll() {
        List<TagEntity> tags = tagMapper.findAll();
        log.info("查询所有标签，共{}条", tags.size());
        return tags;
    }

    @Override
    public TagEntity create(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("标签名称不能为空");
        }

        String tagName = name.trim();

        TagEntity existingTag = tagMapper.findByName(tagName);
        if (existingTag != null) {
            log.info("标签已存在，直接返回，名称：{}，ID：{}", tagName, existingTag.getId());
            return existingTag;
        }

        TagEntity tag = new TagEntity();
        tag.setName(tagName);

        int rows = tagMapper.insert(tag);
        if (rows <= 0) {
            throw new BusinessException("创建标签失败");
        }

        log.info("创建标签成功，名称：{}，ID：{}", tagName, tag.getId());
        return tag;
    }

    @Override
    public List<TagEntity> findBySnippetId(Long snippetId) {
        List<TagEntity> tags = tagMapper.findBySnippetId(snippetId);
        log.info("查询片段关联标签，片段ID：{}，标签数：{}", snippetId, tags.size());
        return tags;
    }
}
