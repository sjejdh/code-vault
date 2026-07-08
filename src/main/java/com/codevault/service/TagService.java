package com.codevault.service;

import com.codevault.entity.TagEntity;

import java.util.List;

/**
 * 标签服务接口
 * Service 层返回业务对象，由 Controller 层统一包装为 Result<T>
 */
public interface TagService {

    List<TagEntity> findAll();

    TagEntity create(String name);

    List<TagEntity> findBySnippetId(Long snippetId);
}
