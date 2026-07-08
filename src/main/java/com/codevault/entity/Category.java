package com.codevault.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 分类实体类，对应 category 表
 */
public class Category {

    /** 主键ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类图标 */
    private String icon;

    /** 排序序号 */
    private Integer sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Category() {
    }

    public Category(Long id, String name, String icon, Integer sortOrder, LocalDateTime createTime) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
