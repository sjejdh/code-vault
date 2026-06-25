package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 分类控制器
 * 提供分类的增删改查REST接口
 */
@Tag(name = "分类模块")
@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取所有分类（按排序序号升序）
     * @return 分类列表
     */
    @Operation(summary = "获取所有分类")
    @GetMapping
    public Result findAll() {
        return categoryService.findAll();
    }

    /**
     * 创建分类（仅管理员可操作）
     * @param params 请求参数，包含name和icon
     * @return 操作结果
     */
    @Operation(summary = "创建分类")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public Result create(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String icon = params.get("icon");
        log.info("创建分类请求，名称：{}", name);
        return categoryService.create(name, icon);
    }

    /**
     * 更新分类（仅管理员可操作）
     * @param id     分类ID
     * @param params 请求参数，包含name和icon
     * @return 操作结果
     */
    @Operation(summary = "更新分类")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String name = params.get("name");
        String icon = params.get("icon");
        log.info("更新分类请求，ID：{}，名称：{}", id, name);
        return categoryService.update(id, name, icon);
    }

    /**
     * 删除分类（仅管理员可操作）
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除分类")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除分类请求，ID：{}", id);
        return categoryService.delete(id);
    }
}
