package com.codevault.controller;

import com.codevault.common.result.Result;
import com.codevault.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 标签控制器
 * 提供标签的查询和创建REST接口
 */
@Slf4j
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping
    public Result findAll() {
        return tagService.findAll();
    }

    /**
     * 创建标签（已存在则返回已有的）
     * @param params 请求参数，包含name
     * @return 操作结果
     */
    @PostMapping
    public Result create(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        log.info("创建标签请求，名称：{}", name);
        return tagService.create(name);
    }
}
