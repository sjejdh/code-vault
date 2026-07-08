package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.entity.Category;
import com.codevault.mapper.CategoryMapper;
import com.codevault.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 分类业务逻辑实现类
 * Service 层返回业务对象，由 Controller 层统一包装为 Result<T>
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findAll() {
        List<Category> categories = categoryMapper.findAll();
        log.info("查询所有分类，共{}条", categories.size());
        return categories;
    }

    @Override
    public Category create(String name, String icon) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }

        Category category = new Category();
        category.setName(name.trim());
        category.setIcon(icon != null ? icon.trim() : "");
        category.setSortOrder(0);

        int rows = categoryMapper.insert(category);
        if (rows <= 0) {
            throw new BusinessException("创建分类失败");
        }

        log.info("创建分类成功，名称：{}，ID：{}", name, category.getId());
        return category;
    }

    @Override
    public void update(Long id, String name, String icon) {
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        Category category = new Category();
        category.setId(id);
        if (name != null && !name.trim().isEmpty()) {
            category.setName(name.trim());
        }
        if (icon != null) {
            category.setIcon(icon.trim());
        }

        int rows = categoryMapper.update(category);
        if (rows <= 0) {
            throw new BusinessException("更新分类失败");
        }

        log.info("更新分类成功，ID：{}", id);
    }

    @Override
    public void delete(Long id) {
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        int rows = categoryMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException("删除分类失败");
        }

        log.info("删除分类成功，ID：{}，名称：{}", id, existing.getName());
    }
}
