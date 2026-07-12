package com.codevault.service.impl;

import com.codevault.service.AiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI 代码解释服务实现
 * 调用 DeepSeek API 分析代码，结果缓存到 Redis
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.max-tokens}")
    private int maxTokens;

    @Value("${deepseek.temperature}")
    private double temperature;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "ai:explain:";
    private static final long CACHE_TTL = 24; // 24 小时

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String explainCode(Long snippetId, String code, String language) {
        // 1. 先查缓存
        String cacheKey = CACHE_KEY_PREFIX + snippetId;
        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("AI 解释命中缓存，片段ID：{}", snippetId);
            return cached;
        }

        // 2. 缓存未命中，调用 AI API
        log.info("AI 解释未命中缓存，调用 DeepSeek API，片段ID：{}", snippetId);
        String explanation = callDeepSeekAPI(code, language);

        // 3. 缓存结果
        redisTemplate.opsForValue().set(cacheKey, explanation, CACHE_TTL, TimeUnit.HOURS);

        return explanation;
    }

    /**
     * 调用 DeepSeek API 分析代码
     */
    private String callDeepSeekAPI(String code, String language) {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 构建请求体（OpenAI Chat 兼容格式）
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", temperature,
                "max_tokens", maxTokens,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "你是一个资深程序员，擅长用简洁的中文解释代码。请分析以下" + language + "代码，"
                                + "用结构化的方式说明：\n"
                                + "1. 代码功能：这段代码实现了什么\n"
                                + "2. 核心逻辑：关键步骤和算法\n"
                                + "3. 技术要点：用到的技术或设计模式\n"
                                + "4. 使用场景：什么时候会用这段代码\n"
                                + "请控制在 500 字以内，简洁明了。"),
                        Map.of("role", "user", "content", code)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) {
                return "AI 服务返回为空，请稍后重试";
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "AI 未生成解释，请检查代码内容";
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            log.info("DeepSeek API 调用成功，片段解释长度：{}", content != null ? content.length() : 0);
            return content != null ? content : "AI 解释为空，请稍后重试";

        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败：{}", e.getMessage());
            // 返回友好的错误提示，不暴露异常细节
            if (e.getMessage() != null && e.getMessage().contains("Insufficient")) {
                return "AI 服务额度不足，请联系管理员充值";
            }
            return "AI 解释暂时不可用，请稍后重试";
        }
    }
}