package com.codevault.config;

import com.codevault.entity.Snippet;
import com.codevault.mapper.SnippetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 热门片段缓存管理组件
 * 使用Redis缓存热门代码片段数据，减少数据库查询压力
 * 缓存过期时间：30分钟
 */
@Slf4j
@Component
public class HotSnippetCache {

    /** 缓存key */
    private static final String CACHE_KEY = "hot_snippets";

    /** 缓存过期时间：30分钟 */
    private static final long CACHE_EXPIRE_MINUTES = 30;

    /** 热门片段默认返回条数 */
    private static final int DEFAULT_LIMIT = 10;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SnippetMapper snippetMapper;

    /**
     * 刷新缓存：从数据库查询热门片段并存入Redis
     * 取前10条热门片段，设置30分钟过期时间
     */
    public void refreshCache() {
        // 从数据库查询热门片段（按浏览量降序，取前10条）
        List<Snippet> hotSnippets = snippetMapper.findHotSnippets(DEFAULT_LIMIT);
        // 存入Redis，设置30分钟过期时间
        redisTemplate.opsForValue().set(CACHE_KEY, hotSnippets, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("刷新热门片段缓存，共{}条", hotSnippets.size());
    }

    /**
     * 获取热门片段（带缓存）
     * 1. 先从Redis获取缓存
     * 2. 缓存命中则直接返回
     * 3. 缓存未命中则刷新缓存并返回
     *
     * @return 热门片段列表
     */
    @SuppressWarnings("unchecked")
    public List<Snippet> getHotSnippets() {
        // 先从Redis获取缓存
        Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            log.info("热门片段缓存命中，直接返回缓存数据");
            return (List<Snippet>) cached;
        }
        // 缓存未命中，刷新缓存
        log.info("热门片段缓存未命中，从数据库刷新");
        refreshCache();
        // 重新获取缓存数据
        return (List<Snippet>) redisTemplate.opsForValue().get(CACHE_KEY);
    }

    /**
     * 手动清除缓存
     * 在创建/更新/删除片段时调用，确保数据一致性
     */
    public void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        log.info("清除热门片段缓存");
    }
}
