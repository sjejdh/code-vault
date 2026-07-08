package com.codevault.service;

import com.codevault.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * Service 层返回业务对象，由 Controller 层统一包装为 Result<T>
 */
public interface CategoryService {

    List<Category> findAll();

    Category create(String name, String icon);

    void update(Long id, String name, String icon);

    void delete(Long id);
}
