package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.entity.TagEntity;
import com.codevault.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 标签控制器
 * Controller 层负责将 Service 返回的业务对象包装为统一的 Result<T> 响应
 */
@Tag(name = "标签模块")
@Slf4j
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result findAll() {
        List<TagEntity> tags = tagService.findAll();
        return Result.success(tags);
    }

    @Operation(summary = "创建标签")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public Result create(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        log.info("创建标签请求，名称：{}", name);
        TagEntity tag = tagService.create(name);
        return Result.success("创建标签成功", tag);
    }
}
