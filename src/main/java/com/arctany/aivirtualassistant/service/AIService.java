package com.arctany.aivirtualassistant.service;

import com.arctany.aivirtualassistant.model.KnowledgeBase;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI服务接口，处理自然语言交互和多模态输入
 */
public interface AIService {
    
    /**
     * 处理用户文本消息并返回AI响应
     *
     * @param message 用户消息
     * @return 包含AI响应的CompletableFuture
     */
    CompletableFuture<String> processMessage(String message);
    
    /**
     * 处理用户图像和文本输入并返回AI响应
     *
     * @param text 文本描述
     * @param imagePath 图像路径
     * @return 包含AI响应的CompletableFuture
     */
    CompletableFuture<String> processImageAndText(String text, String imagePath);
    
    /**
     * 初始化AI服务
     */
    void initialize();
    
    /**
     * 设置AI模型
     *
     * @param modelName 模型名称
     */
    void setModel(String modelName);
    
    /**
     * 设置AI个性
     *
     * @param personality AI个性描述
     */
    void setPersonality(String personality);
    
    /**
     * 重置对话历史
     */
    void resetConversation();
    
    /**
     * 创建新的知识库
     *
     * @param name 知识库名称
     * @param description 知识库描述
     * @return 是否创建成功
     */
    boolean createKnowledgeBase(String name, String description);
    
    /**
     * 向知识库添加文档
     *
     * @param knowledgeBaseName 知识库名称
     * @param documentPath 文档路径
     * @return 是否添加成功
     */
    boolean addDocumentToKnowledgeBase(String knowledgeBaseName, String documentPath);
    
    /**
     * 设置当前活动的知识库
     *
     * @param knowledgeBaseName 知识库名称，null表示不使用知识库
     * @return 是否设置成功
     */
    boolean setActiveKnowledgeBase(String knowledgeBaseName);
    
    /**
     * 列出所有可用的知识库
     *
     * @return 知识库列表
     */
    List<KnowledgeBase> listKnowledgeBases();
} 