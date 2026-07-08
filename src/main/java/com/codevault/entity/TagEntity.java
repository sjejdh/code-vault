package com.codevault.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类，对应 tag 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    /** 主键ID */
    private Long id;

    /** 标签名称 */
    private String name;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
