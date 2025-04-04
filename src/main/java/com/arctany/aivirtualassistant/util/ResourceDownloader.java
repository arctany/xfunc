package com.arctany.aivirtualassistant.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 资源下载器 - 用于下载和准备必要的资源文件
 */
@Slf4j
@Component
public class ResourceDownloader {

    // 3D模型资源
    private static final String DEFAULT_3D_MODEL_URL = "https://models.readyplayer.me/64266fe8c1cb86ec0a89a884.glb";
    private static final String DEFAULT_3D_MODEL_PATH = "src/main/resources/avatars/3d/default_avatar.glb";
    private static final String DEFAULT_3D_HTML_PATH = "src/main/resources/avatars/3d/default_avatar.html";

    // 2D头像资源
    private static final String DEFAULT_2D_AVATAR_URL = "https://avatars.githubusercontent.com/u/9919?s=400&v=4";
    private static final String DEFAULT_2D_AVATAR_PATH = "src/main/resources/avatars/2d/default_avatar.png";
    private static final String DEFAULT_2D_HTML_PATH = "src/main/resources/avatars/2d/default_avatar.html";

    // 语音样本资源
    private static final String DEFAULT_VOICE_SAMPLE_URL = "https://www.voiptroubleshooter.com/open_speech/american/OSR_us_000_0010_8k.wav";
    private static final String DEFAULT_VOICE_SAMPLE_PATH = "src/main/resources/voices/default_voice.wav";

    // 安装目录
    private static final String RESOURCES_DIR = "src/main/resources";
    private static final String AVATARS_3D_DIR = RESOURCES_DIR + "/avatars/3d";
    private static final String AVATARS_2D_DIR = RESOURCES_DIR + "/avatars/2d";
    private static final String VOICES_DIR = RESOURCES_DIR + "/voices";

    /**
     * 准备所有必要资源
     */
    public void prepareAllResources() throws IOException {
        log.info("开始准备应用资源...");
        
        // 确保目录存在
        createDirectoriesIfNotExist();
        
        // 下载3D模型
        prepareDefaultThreeDModel();
        
        // 下载2D头像
        prepareDefaultTwoDAvatar();
        
        // 下载语音样本
        prepareDefaultVoiceSample();
        
        log.info("所有资源准备完成！");
    }

    /**
     * 创建必要的目录
     */
    private void createDirectoriesIfNotExist() throws IOException {
        log.info("检查并创建必要的目录...");
        
        Path threeDPath = Paths.get(AVATARS_3D_DIR);
        Path twoDPath = Paths.get(AVATARS_2D_DIR);
        Path voicesPath = Paths.get(VOICES_DIR);
        
        if (!Files.exists(threeDPath)) {
            Files.createDirectories(threeDPath);
            log.info("创建目录: {}", AVATARS_3D_DIR);
        }
        
        if (!Files.exists(twoDPath)) {
            Files.createDirectories(twoDPath);
            log.info("创建目录: {}", AVATARS_2D_DIR);
        }
        
        if (!Files.exists(voicesPath)) {
            Files.createDirectories(voicesPath);
            log.info("创建目录: {}", VOICES_DIR);
        }
    }

    /**
     * 准备默认3D模型
     */
    private void prepareDefaultThreeDModel() throws IOException {
        // 检查3D模型是否已存在
        Path modelPath = Paths.get(DEFAULT_3D_MODEL_PATH);
        if (!Files.exists(modelPath)) {
            log.info("3D模型文件不存在，创建本地默认模型");
            // 创建一个简单的HTML文件而不尝试下载模型
            createLocalDefaultThreeDModel();
        }
        
        // 创建HTML文件用于显示3D模型
        createThreeDModelHtml();
    }
    
    /**
     * 创建本地默认3D模型
     */
    private void createLocalDefaultThreeDModel() throws IOException {
        // 创建一个默认的GLB模型文件（这里只创建一个空文件作为占位符）
        Path modelPath = Paths.get(DEFAULT_3D_MODEL_PATH);
        Files.createDirectories(modelPath.getParent());
        Files.createFile(modelPath);
    }
    
    /**
     * 创建3D模型HTML文件
     */
    private void createThreeDModelHtml() throws IOException {
        Path htmlPath = Paths.get(DEFAULT_3D_HTML_PATH);
        
        if (!Files.exists(htmlPath)) {
            log.info("创建3D模型HTML文件: {}", DEFAULT_3D_HTML_PATH);
            
            String modelFileName = Paths.get(DEFAULT_3D_MODEL_PATH).getFileName().toString();
            String htmlContent = ThreeDUtils.createGLTFViewerHtml(modelFileName);
            
            Files.writeString(htmlPath, htmlContent);
        }
    }

    /**
     * 准备默认2D头像
     */
    private void prepareDefaultTwoDAvatar() throws IOException {
        // 检查2D头像是否已存在
        Path avatarPath = Paths.get(DEFAULT_2D_AVATAR_PATH);
        if (!Files.exists(avatarPath)) {
            log.info("2D头像文件不存在，创建本地默认头像");
            // 创建一个简单的头像文件而不尝试下载
            createLocalDefault2DAvatar();
        }
        
        // 创建HTML文件用于显示2D头像
        createTwoDModelHtml();
    }
    
    /**
     * 创建本地默认2D头像
     */
    private void createLocalDefault2DAvatar() throws IOException {
        // 创建一个默认的头像文件（这里只创建一个空文件作为占位符）
        Path avatarPath = Paths.get(DEFAULT_2D_AVATAR_PATH);
        Files.createDirectories(avatarPath.getParent());
        Files.createFile(avatarPath);
    }
    
    /**
     * 创建2D头像HTML文件
     */
    private void createTwoDModelHtml() throws IOException {
        Path htmlPath = Paths.get(DEFAULT_2D_HTML_PATH);
        
        if (!Files.exists(htmlPath)) {
            log.info("创建2D头像HTML文件: {}", DEFAULT_2D_HTML_PATH);
            
            String avatarFileName = Paths.get(DEFAULT_2D_AVATAR_PATH).getFileName().toString();
            
            // 简单的HTML，显示图片并提供一些基本的交互效果
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>2D Avatar</title>\n" +
                    "    <style>\n" +
                    "        body { margin: 0; overflow: hidden; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #2a2a2a; }\n" +
                    "        .avatar { width: 300px; height: 300px; border-radius: 50%; object-fit: cover; transition: all 0.3s ease; }\n" +
                    "        .avatar:hover { transform: scale(1.05); box-shadow: 0 0 20px rgba(255,255,255,0.5); }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <img id=\"avatar\" class=\"avatar\" src=\"" + avatarFileName + "\" alt=\"AI Assistant Avatar\">\n" +
                    "    <script>\n" +
                    "        // 简单的动画效果\n" +
                    "        const avatar = document.getElementById('avatar');\n" +
                    "        function pulse() {\n" +
                    "            avatar.style.transform = 'scale(1.02)';\n" +
                    "            setTimeout(() => {\n" +
                    "                avatar.style.transform = 'scale(1)';\n" +
                    "            }, 1000);\n" +
                    "        }\n" +
                    "        // 定期呼吸效果\n" +
                    "        setInterval(pulse, 3000);\n" +
                    "        \n" +
                    "        // 与父窗口通信的接口\n" +
                    "        window.updateExpression = function(expression) {\n" +
                    "            // 根据表情调整图片效果，这里只是简单示例\n" +
                    "            if (expression === 'happy') {\n" +
                    "                avatar.style.filter = 'brightness(1.2)';\n" +
                    "            } else if (expression === 'sad') {\n" +
                    "                avatar.style.filter = 'brightness(0.8) sepia(0.2)';\n" +
                    "            } else if (expression === 'thinking') {\n" +
                    "                avatar.style.filter = 'hue-rotate(15deg)';\n" +
                    "            } else {\n" +
                    "                avatar.style.filter = 'none';\n" +
                    "            }\n" +
                    "        };\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
            
            Files.writeString(htmlPath, htmlContent);
        }
    }

    /**
     * 准备默认语音样本
     */
    private void prepareDefaultVoiceSample() throws IOException {
        // 检查语音样本是否已存在
        Path samplePath = Paths.get(DEFAULT_VOICE_SAMPLE_PATH);
        if (!Files.exists(samplePath)) {
            log.info("语音样本文件不存在，创建本地默认样本");
            // 创建一个简单的语音样本文件而不尝试下载
            createLocalDefaultVoiceSample();
        }
    }
    
    /**
     * 创建本地默认语音样本
     */
    private void createLocalDefaultVoiceSample() throws IOException {
        // 创建一个默认的语音样本文件（这里只创建一个空文件作为占位符）
        Path samplePath = Paths.get(DEFAULT_VOICE_SAMPLE_PATH);
        Files.createDirectories(samplePath.getParent());
        Files.createFile(samplePath);
    }

    /**
     * 获取默认3D模型HTML路径
     */
    public String getDefaultThreeDHtmlPath() {
        return DEFAULT_3D_HTML_PATH;
    }
    
    /**
     * 获取默认2D头像HTML路径
     */
    public String getDefaultTwoDHtmlPath() {
        return DEFAULT_2D_HTML_PATH;
    }
    
    /**
     * 获取默认语音样本路径
     */
    public String getDefaultVoiceSamplePath() {
        return DEFAULT_VOICE_SAMPLE_PATH;
    }
} 