package com.codevault.service.impl;

import com.codevault.common.exception.BusinessException;
import com.codevault.common.result.Result;
import com.codevault.entity.Category;
import com.codevault.mapper.CategoryMapper;
import com.codevault.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 分类业务逻辑实现类
 * 处理分类的增删改查操作
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 查询所有分类，按sort_order升序排列
     * @return 包含分类列表的成功响应
     */
    @Override
    public Result findAll() {
        List<Category> categories = categoryMapper.findAll();
        log.info("查询所有分类，共{}条", categories.size());
        return Result.success(categories);
    }

    /**
     * 创建新分类
     * @param name 分类名称
     * @param icon 分类图标
     * @return 操作结果
     */
    @Override
    public Result create(String name, String icon) {
        // 参数校验
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }

        // 构造分类实体
        Category category = new Category();
        category.setName(name.trim());
        category.setIcon(icon != null ? icon.trim() : "");
        category.setSortOrder(0);

        // 插入数据库
        int rows = categoryMapper.insert(category);
        if (rows <= 0) {
            throw new BusinessException("创建分类失败");
        }

        log.info("创建分类成功，名称：{}，ID：{}", name, category.getId());
        return Result.success("创建分类成功", category);
    }

    /**
     * 更新分类信息
     * @param id   分类ID
     * @param name 新的分类名称
     * @param icon 新的分类图标
     * @return 操作结果
     */
    @Override
    public Result update(Long id, String name, String icon) {
        // 检查分类是否存在
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        // 构造更新实体
        Category category = new Category();
        category.setId(id);
        if (name != null && !name.trim().isEmpty()) {
            category.setName(name.trim());
        }
        if (icon != null) {
            category.setIcon(icon.trim());
        }

        // 执行更新
        int rows = categoryMapper.update(category);
        if (rows <= 0) {
            throw new BusinessException("更新分类失败");
        }

        log.info("更新分类成功，ID：{}", id);
        return Result.success("更新分类成功");
    }

    /**
     * 根据ID删除分类
     * @param id 分类ID
     * @return 操作结果
     */
    @Override
    public Result delete(Long id) {
        // 检查分类是否存在
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        // 执行删除
        int rows = categoryMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException("删除分类失败");
        }

        log.info("删除分类成功，ID：{}，名称：{}", id, existing.getName());
        return Result.success("删除分类成功");
    }
}
