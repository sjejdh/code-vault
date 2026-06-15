package com.codevault.service;

import com.codevault.common.result.Result;
import com.codevault.entity.Tag;

import java.util.List;

/**
 * 标签业务逻辑接口
 * 定义标签模块的核心业务方法
 */
public interface TagService {

    /**
     * 查询所有标签
     * @return 标签列表
     */
    Result findAll();

    /**
     * 创建标签（已存在则返回已有的）
     * @param name 标签名称
     * @return 操作结果
     */
    Result create(String name);

    /**
     * 查询指定代码片段关联的标签
     * @param snippetId 代码片段ID
     * @return 操作结果
     */
    Result findBySnippetId(Long snippetId);
}
