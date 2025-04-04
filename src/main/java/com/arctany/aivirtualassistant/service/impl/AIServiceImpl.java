package com.arctany.aivirtualassistant.service.impl;

import com.arctany.aivirtualassistant.config.AIConfig;
import com.arctany.aivirtualassistant.model.KnowledgeBase;
import com.arctany.aivirtualassistant.service.AIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 增强型AI服务实现，支持多模态输入和本地知识库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIConfig aiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 基本API配置
    @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${ai.api.vision.url:https://api.openai.com/v1/chat/completions}")
    private String visionApiUrl;
    
    @Value("${ai.api.key:}")
    private String apiKey;
    
    @Value("${ai.api.model:gpt-3.5-turbo}")
    private String defaultModel;
    
    @Value("${ai.api.vision.model:gpt-4-vision-preview}")
    private String visionModel;
    
    // 扩展设置
    @Value("${ai.api.provider:openai}")
    private String apiProvider;
    
    @Value("${ai.knowledge.base.path:knowledge}")
    private String knowledgeBasePath;
    
    @Value("${ai.enable.rag:true}")
    private boolean enableRAG;
    
    @Value("${ai.temperature:0.7}")
    private double temperature;
    
    @Value("${ai.max.tokens:2000}")
    private int maxTokens;

    // 当前设置
    private String model = null;
    private String personality = "你是一个友好、有帮助的AI助手，名为「AI虚拟助手」。你的职责是提供有用的信息和帮助用户完成各种任务。你应该保持礼貌、专业和友好的态度。";
    
    // 对话历史
    private List<Map<String, String>> conversationHistory = new ArrayList<>();
    
    // 知识库
    private Map<String, KnowledgeBase> knowledgeBases = new HashMap<>();
    
    // 当前激活的知识库
    private String activeKnowledgeBase = null;

    @Override
    public void initialize() {
        // 如果模型为空，使用默认模型
        if (model == null) {
            model = defaultModel;
        }
        
        log.info("初始化AI服务，API提供商: {}, 使用模型: {}", apiProvider, model);
        
        // 初始化知识库目录
        Path knowledgeDir = Paths.get(knowledgeBasePath);
        try {
            if (!Files.exists(knowledgeDir)) {
                Files.createDirectories(knowledgeDir);
                log.info("已创建知识库目录: {}", knowledgeDir);
            }
            
            // 加载可用的知识库
            loadKnowledgeBases();
        } catch (IOException e) {
            log.error("创建知识库目录时出错: {}", e.getMessage());
        }
        
        // 清空对话历史
        resetConversation();
    }
    
    /**
     * 加载可用的知识库
     */
    private void loadKnowledgeBases() {
        try {
            Path knowledgeDir = Paths.get(knowledgeBasePath);
            List<Path> knowledgeDirs = Files.list(knowledgeDir)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
            
            for (Path dir : knowledgeDirs) {
                String name = dir.getFileName().toString();
                Path configFile = dir.resolve("config.json");
                
                if (Files.exists(configFile)) {
                    try {
                        JsonNode config = objectMapper.readTree(configFile.toFile());
                        KnowledgeBase kb = new KnowledgeBase();
                        kb.setName(name);
                        kb.setDescription(config.path("description").asText(""));
                        kb.setPath(dir.toString());
                        
                        // 加载知识库文件列表
                        kb.setFiles(Files.list(dir)
                            .filter(path -> !path.getFileName().toString().equals("config.json"))
                            .map(Path::toString)
                            .collect(Collectors.toList()));
                        
                        knowledgeBases.put(name, kb);
                        log.info("已加载知识库: {}, 包含 {} 个文件", name, kb.getFiles().size());
                    } catch (IOException e) {
                        log.error("读取知识库配置时出错: {}", e.getMessage());
                    }
                }
            }
            
            log.info("已加载 {} 个知识库", knowledgeBases.size());
        } catch (IOException e) {
            log.error("加载知识库时出错: {}", e.getMessage());
        }
    }

    @Override
    public CompletableFuture<String> processMessage(String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 添加用户消息到历史
                addMessageToHistory("user", message);
                
                // 根据知识库增强提示
                String enhancedPrompt = message;
                if (enableRAG && activeKnowledgeBase != null) {
                    enhancedPrompt = enhancePromptWithKnowledge(message);
                }
                
                // 调用API获取响应
                String response = callChatApi(enhancedPrompt);
                
                // 添加助手响应到历史
                addMessageToHistory("assistant", response);
                
                return response;
            } catch (Exception e) {
                log.error("处理消息时出错", e);
                return "抱歉，我现在无法回答您的问题。请稍后再试。";
            }
        });
    }
    
    @Override
    public CompletableFuture<String> processImageAndText(String text, String imagePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("处理多模态输入：文本={}，图像={}", text, imagePath);
                
                // 准备多模态请求
                String response = callVisionApi(text, imagePath);
                
                // 添加对话历史
                addMessageToHistory("user", "[包含图像] " + text);
                addMessageToHistory("assistant", response);
                
                return response;
            } catch (Exception e) {
                log.error("处理图像和文本时出错", e);
                return "抱歉，处理您提供的图像时出现问题。请稍后再试。";
            }
        });
    }
    
    /**
     * 调用多模态视觉API
     */
    private String callVisionApi(String text, String imagePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(visionApiUrl);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");
            
            // 准备请求体
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("model", visionModel);
            rootNode.put("max_tokens", maxTokens);
            rootNode.put("temperature", temperature);
            
            ArrayNode messagesArray = rootNode.putArray("messages");
            
            // 系统消息
            ObjectNode systemMessage = messagesArray.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", personality);
            
            // 用户消息（包含图像）
            ObjectNode userMessage = messagesArray.addObject();
            userMessage.put("role", "user");
            
            ArrayNode contentArray = userMessage.putArray("content");
            
            // 添加文本部分
            ObjectNode textPart = contentArray.addObject();
            textPart.put("type", "text");
            textPart.put("text", text);
            
            // 添加图像部分
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                byte[] fileContent = Files.readAllBytes(imageFile.toPath());
                String base64Image = Base64.getEncoder().encodeToString(fileContent);
                
                ObjectNode imagePart = contentArray.addObject();
                imagePart.put("type", "image_url");
                
                ObjectNode imageUrlObj = imagePart.putObject("image_url");
                imageUrlObj.put("url", "data:image/jpeg;base64," + base64Image);
            }
            
            StringEntity entity = new StringEntity(rootNode.toString(), "UTF-8");
            httpPost.setEntity(entity);
            
            // 发送请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseContent = EntityUtils.toString(response.getEntity());
                JsonNode jsonResponse = objectMapper.readTree(responseContent);
                
                return jsonResponse.path("choices").get(0).path("message").path("content").asText();
            }
        }
    }
    
    /**
     * 调用聊天API
     */
    private String callChatApi(String message) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            
            // 准备请求体
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("model", model);
            rootNode.put("temperature", temperature);
            rootNode.put("max_tokens", maxTokens);
            
            ArrayNode messagesArray = rootNode.putArray("messages");
            
            // 首先添加系统消息
            ObjectNode systemMessage = messagesArray.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", personality);
            
            // 然后添加历史消息
            for (Map<String, String> historyMessage : conversationHistory) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", historyMessage.get("role"));
                msgNode.put("content", historyMessage.get("content"));
            }
            
            // 最后添加当前消息
            ObjectNode userMessage = messagesArray.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            
            // 转换为JSON字符串
            String requestBody = rootNode.toString();
            
            StringEntity stringEntity = new StringEntity(requestBody, "UTF-8");
            httpPost.setEntity(stringEntity);
            
            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                
                // 解析响应
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.path("choices").get(0).path("message").path("content").asText();
            }
        }
    }
    
    /**
     * 使用知识库增强提示
     */
    private String enhancePromptWithKnowledge(String userMessage) {
        KnowledgeBase kb = knowledgeBases.get(activeKnowledgeBase);
        if (kb == null || kb.getFiles().isEmpty()) {
            return userMessage;
        }
        
        // 构建增强的提示
        StringBuilder enhancedPrompt = new StringBuilder();
        enhancedPrompt.append("根据以下背景信息回答用户问题，如果背景信息不包含相关内容，只使用您自己的知识回答：\n\n");
        
        // 简单实现：只添加前3个知识文件的内容
        int filesAdded = 0;
        for (String filePath : kb.getFiles()) {
            if (filesAdded >= 3) break;
            
            try {
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    String content = Files.readString(path);
                    
                    // 限制每个文件的内容大小
                    if (content.length() > 2000) {
                        content = content.substring(0, 2000) + "...";
                    }
                    
                    enhancedPrompt.append("---\n");
                    enhancedPrompt.append("文件：").append(path.getFileName()).append("\n");
                    enhancedPrompt.append(content).append("\n");
                    filesAdded++;
                }
            } catch (IOException e) {
                log.error("读取知识文件时出错: {}", e.getMessage());
            }
        }
        
        enhancedPrompt.append("---\n\n");
        enhancedPrompt.append("用户问题：").append(userMessage);
        
        return enhancedPrompt.toString();
    }
    
    /**
     * 添加消息到对话历史
     */
    private void addMessageToHistory(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        
        conversationHistory.add(message);
        
        // 限制历史消息数量，防止过长
        if (conversationHistory.size() > 20) {
            conversationHistory.remove(0);
        }
    }

    @Override
    public void setModel(String modelName) {
        this.model = modelName;
        log.info("AI模型已设置为: {}", modelName);
    }

    @Override
    public void setPersonality(String personality) {
        this.personality = personality;
        log.info("AI个性已设置为: {}", personality);
    }
    
    @Override
    public void resetConversation() {
        conversationHistory.clear();
        log.info("对话历史已重置");
    }
    
    @Override
    public boolean createKnowledgeBase(String name, String description) {
        if (knowledgeBases.containsKey(name)) {
            log.warn("知识库已存在: {}", name);
            return false;
        }
        
        try {
            // 创建知识库目录
            Path kbDir = Paths.get(knowledgeBasePath, name);
            Files.createDirectories(kbDir);
            
            // 创建配置文件
            Path configFile = kbDir.resolve("config.json");
            ObjectNode config = objectMapper.createObjectNode();
            config.put("name", name);
            config.put("description", description);
            config.put("created", System.currentTimeMillis());
            
            Files.writeString(configFile, config.toString());
            
            // 创建知识库对象
            KnowledgeBase kb = new KnowledgeBase();
            kb.setName(name);
            kb.setDescription(description);
            kb.setPath(kbDir.toString());
            kb.setFiles(new ArrayList<>());
            
            knowledgeBases.put(name, kb);
            log.info("已创建知识库: {}", name);
            
            return true;
        } catch (IOException e) {
            log.error("创建知识库时出错: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean addDocumentToKnowledgeBase(String knowledgeBaseName, String documentPath) {
        KnowledgeBase kb = knowledgeBases.get(knowledgeBaseName);
        if (kb == null) {
            log.warn("知识库不存在: {}", knowledgeBaseName);
            return false;
        }
        
        try {
            Path sourcePath = Paths.get(documentPath);
            if (!Files.exists(sourcePath)) {
                log.warn("文档不存在: {}", documentPath);
                return false;
            }
            
            // 复制文档到知识库目录
            Path targetPath = Paths.get(kb.getPath(), sourcePath.getFileName().toString());
            Files.copy(sourcePath, targetPath);
            
            // 更新知识库文件列表
            kb.getFiles().add(targetPath.toString());
            log.info("已添加文档到知识库 {}: {}", knowledgeBaseName, targetPath);
            
            return true;
        } catch (IOException e) {
            log.error("添加文档到知识库时出错: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean setActiveKnowledgeBase(String knowledgeBaseName) {
        if (knowledgeBaseName == null) {
            activeKnowledgeBase = null;
            log.info("已禁用知识库增强");
            return true;
        }
        
        if (!knowledgeBases.containsKey(knowledgeBaseName)) {
            log.warn("知识库不存在: {}", knowledgeBaseName);
            return false;
        }
        
        activeKnowledgeBase = knowledgeBaseName;
        log.info("已设置活动知识库: {}", knowledgeBaseName);
        return true;
    }
    
    @Override
    public List<KnowledgeBase> listKnowledgeBases() {
        return new ArrayList<>(knowledgeBases.values());
    }
} 