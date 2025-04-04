package com.arctany.aivirtualassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    /**
     * API相关配置
     */
    private Api api = new Api();
    
    /**
     * 模型相关配置
     */
    private Model model = new Model();
    
    /**
     * 个性化配置
     */
    private String personality = "你是一个友好、有帮助的AI助手。";
    
    @Data
    public static class Api {
        /**
         * API URL
         */
        private String url = "https://api.openai.com/v1/chat/completions";
        
        /**
         * API 密钥
         */
        private String key = "";
        
        /**
         * 超时时间（毫秒）
         */
        private int timeout = 30000;
    }
    
    @Data
    public static class Model {
        /**
         * 模型名称
         */
        private String name = "gpt-3.5-turbo";
        
        /**
         * 温度参数，控制随机性
         */
        private double temperature = 0.7;
        
        /**
         * 最大生成令牌数
         */
        private int maxTokens = 1000;
    }
} 