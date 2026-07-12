package com.codevault.service;

/**
 * AI 代码解释服务接口
 */
public interface AiService {

    /**
     * 使用 AI 分析代码片段并返回中文解释
     *
     * @param snippetId 代码片段 ID
     * @param code      代码内容
     * @param language  编程语言
     * @return AI 生成的代码解释
     */
    String explainCode(Long snippetId, String code, String language);
}