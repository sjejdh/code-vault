package com.codevault.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 点赞实体类，对应 like 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    /** 主键ID */
    private Long id;

    /** 点赞用户ID */
    private Long userId;

    /** 被点赞的代码片段ID */
    private Long snippetId;

    /** 点赞时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
