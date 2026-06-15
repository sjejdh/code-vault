package com.codevault.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 代码片段数据传输对象
 * 用于接收前端创建/更新代码片段的请求参数
 */
@Data
public class SnippetDTO {

    /** 片段标题 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不超过100个字符")
    private String title;

    /** 片段描述 */
    private String description;

    /** 代码内容 */
    @NotBlank(message = "代码内容不能为空")
    private String content;

    /** 编程语言 */
    @NotBlank(message = "编程语言不能为空")
    private String language;

    /** 所属分类ID */
    private Long categoryId;

    /** 是否公开：1-公开，0-私有 */
    private Integer isPublic;

    /** 标签名称列表 */
    private List<String> tags;
}
