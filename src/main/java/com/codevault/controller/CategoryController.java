package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.entity.Category;
import com.codevault.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 * Controller 层负责将 Service 返回的业务对象包装为统一的 Result<T> 响应
 */
@Tag(name = "分类模块")
@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping
    public Result findAll() {
        List<Category> categories = categoryService.findAll();
        return Result.success(categories);
    }

    @Operation(summary = "创建分类")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public Result create(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String icon = params.get("icon");
        log.info("创建分类请求，名称：{}", name);
        Category category = categoryService.create(name, icon);
        return Result.success("创建分类成功", category);
    }

    @Operation(summary = "更新分类")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String name = params.get("name");
        String icon = params.get("icon");
        log.info("更新分类请求，ID：{}，名称：{}", id, name);
        categoryService.update(id, name, icon);
        return Result.success("更新分类成功");
    }

    @Operation(summary = "删除分类")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除分类请求，ID：{}", id);
        categoryService.delete(id);
        return Result.success("删除分类成功");
    }
}
