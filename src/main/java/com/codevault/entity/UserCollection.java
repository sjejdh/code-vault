package com.codevault.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体类，对应 collection 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollection {

    /** 主键ID */
    private Long id;

    /** 收藏用户ID */
    private Long userId;

    /** 被收藏的代码片段ID */
    private Long snippetId;

    /** 收藏时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}