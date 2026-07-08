package com.codevault.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码片段实体类，对应 snippet 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snippet {

    /** 主键ID */
    private Long id;

    /** 发布用户ID */
    private Long userId;

    /** 片段标题 */
    private String title;

    /** 片段描述 */
    private String description;

    /** 片段内容 */
    private String content;

    /** 编程语言 */
    private String language;

    /** 所属分类ID */
    private Long categoryId;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer collectCount;

    /** 是否公开：0-私密，1-公开 */
    private Integer isPublic;

    /** 状态：0-删除，1-正常 */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 关联标签列表（非数据库字段，由业务层手动填充） */
    private List<String> tags;
}
