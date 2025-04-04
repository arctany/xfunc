package com.arctany.aivirtualassistant.config;

import com.arctany.aivirtualassistant.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.Executor;

/**
 * 应用程序配置类
 */
@Slf4j
@Configuration
public class AppConfig {

    @Value("${avatar.resources.path:src/main/resources/avatars}")
    private String avatarResourcesPath;
    
    @Value("${voice.resources.path:src/main/resources/voices}")
    private String voiceResourcesPath;
    
    @Value("${image.resources.path:src/main/resources/images}")
    private String imageResourcesPath;

    /**
     * 初始化资源目录
     */
    @PostConstruct
    public void init() {
        // 初始化必要的资源目录
        initializeResourceDirectories();
        
        // 检测系统环境
        checkSystemEnvironment();
        
        log.info("应用程序配置初始化完成");
    }
    
    /**
     * 初始化所有资源目录
     */
    private void initializeResourceDirectories() {
        // 创建虚拟形象资源目录
        ensureDirectoryExists(avatarResourcesPath);
        
        // 创建语音资源目录
        ensureDirectoryExists(voiceResourcesPath);
        
        // 创建图像资源目录
        ensureDirectoryExists(imageResourcesPath);
        
        // 创建FXML资源目录
        ensureDirectoryExists("src/main/resources/fxml");
        
        log.info("资源目录初始化完成");
    }
    
    /**
     * 确保目录存在，如果不存在则创建
     */
    private void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("已创建目录: {}", dirPath);
            } else {
                log.warn("无法创建目录: {}", dirPath);
            }
        }
    }
    
    /**
     * 检测系统环境
     */
    private void checkSystemEnvironment() {
        try {
            // 检测Java版本
            String javaVersion = System.getProperty("java.version");
            log.info("Java版本: {}", javaVersion);
            
            // 检测操作系统
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            log.info("操作系统: {} {} {}", osName, osVersion, osArch);
            
            // 检测可用处理器核心数
            int processors = Runtime.getRuntime().availableProcessors();
            log.info("可用处理器核心数: {}", processors);
            
            // 检测内存
            long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
            log.info("最大可用内存: {} MB", maxMemory);
            
            // 检测图形环境
            boolean hasGraphicsEnvironment = false;
            try {
                hasGraphicsEnvironment = java.awt.GraphicsEnvironment.isHeadless() == false;
            } catch (Exception e) {
                log.warn("检测图形环境时出错", e);
            }
            
            if (hasGraphicsEnvironment) {
                log.info("检测到图形环境，可使用GUI功能");
            } else {
                log.warn("未检测到图形环境，GUI功能可能受限");
            }
        } catch (Exception e) {
            log.error("系统环境检测出错", e);
        }
    }
    
    /**
     * 创建应用程序主线程池
     */
    @Bean
    @Primary
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("app-task-");
        executor.initialize();
        return executor;
    }
    
    /**
     * 创建AI处理线程池
     */
    @Bean(name = "aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-task-");
        executor.initialize();
        return executor;
    }
    
    /**
     * 创建媒体处理线程池
     */
    @Bean(name = "mediaExecutor")
    public Executor mediaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("media-task-");
        executor.initialize();
        return executor;
    }
} 