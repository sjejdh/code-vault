package com.codevault.entity;

import lombok.Data;

/**
 * 片段-标签关联查询结果
 * 用于批量查询时接收 snippet_id + tag 信息
 */
@Data
public class SnippetTagRelation {
    private Long snippetId;
    private Long tagId;
    private String tagName;
}