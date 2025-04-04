package com.arctany.aivirtualassistant.service.impl;

import com.arctany.aivirtualassistant.model.AvatarModel;
import com.arctany.aivirtualassistant.service.AIService;
import com.arctany.aivirtualassistant.service.AvatarService;
import com.arctany.aivirtualassistant.util.FileUtils;
import com.arctany.aivirtualassistant.util.ResourceDownloader;
import com.arctany.aivirtualassistant.util.ThreeDUtils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 虚拟形象服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final AIService aiService;
    private final ResourceDownloader resourceDownloader;
    
    @Value("${avatar.resources.path:src/main/resources/avatars}")
    private String avatarResourcesPath;
    
    private WebView webView;
    private WebEngine webEngine;
    private AvatarModel currentAvatar;

    @Override
    public void initializeAvatar(WebView webView) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
        
        try {
            // 使用ResourceDownloader准备资源
            Path avatarsDir = Paths.get(avatarResourcesPath);
            if (!Files.exists(avatarsDir)) {
                Files.createDirectories(avatarsDir);
            }
            
            // 创建默认头像模型
            currentAvatar = AvatarModel.builder()
                    .type("3d")
                    .path(resourceDownloader.getDefaultThreeDHtmlPath())
                    .name("默认3D头像")
                    .description("一个高质量的3D默认头像")
                    .expression("neutral")
                    .build();
            
            // 加载默认3D模型
            loadDefaultAvatar();
        } catch (IOException e) {
            log.error("初始化虚拟形象目录时出错", e);
        }
    }

    private void loadDefaultAvatar() {
        try {
            // 使用ResourceDownloader中的默认3D HTML文件路径
            String defaultAvatarPath = resourceDownloader.getDefaultThreeDHtmlPath();
            File htmlFile = new File(defaultAvatarPath);
            
            if (htmlFile.exists()) {
                // 使用file://协议加载本地HTML文件
                String fileUrl = htmlFile.toURI().toURL().toString();
                Platform.runLater(() -> {
                    webEngine.load(fileUrl);
                    log.info("加载默认3D形象: {}", fileUrl);
                });
            } else {
                log.error("默认3D形象HTML文件不存在: {}", defaultAvatarPath);
                // 创建基本的3D头像作为备选方案
                createFallbackAvatar();
            }
        } catch (Exception e) {
            log.error("加载默认虚拟形象时出错", e);
            // 创建基本的3D头像作为备选方案
            createFallbackAvatar();
        }
    }
    
    /**
     * 创建备选虚拟形象
     */
    private void createFallbackAvatar() {
        log.info("创建备选虚拟形象...");
        try {
            // 使用ThreeDUtils创建基本的3D球体
            String script = ThreeDUtils.createBasicSphereScript();
            Platform.runLater(() -> {
                webEngine.loadContent(script);
                log.info("加载备选3D形象");
            });
        } catch (Exception e) {
            log.error("创建备选虚拟形象时出错", e);
        }
    }

    @Override
    public void updateAvatarExpression(String message) {
        if (webEngine != null) {
            // 简单情感分析
            String expression = analyzeExpression(message);
            
            // 更新当前表情
            if (currentAvatar != null) {
                currentAvatar.setExpression(expression);
            }
            
            // 调用JavaScript更新表情
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript("if(typeof updateExpression === 'function') { updateExpression('" + expression + "'); }");
                } catch (Exception e) {
                    log.debug("更新表情时出错 (可能是JavaScript接口未就绪): {}", e.getMessage());
                }
            });
        }
    }
    
    /**
     * 简单的情感分析
     */
    private String analyzeExpression(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("谢谢") || 
            lowerMessage.contains("感谢") || 
            lowerMessage.contains("很高兴") || 
            lowerMessage.contains("太好了") ||
            lowerMessage.contains("哈哈") ||
            lowerMessage.contains("😊") ||
            lowerMessage.contains("😄") ||
            lowerMessage.contains("👍")) {
            return "happy";
        } else if (lowerMessage.contains("抱歉") || 
                   lowerMessage.contains("对不起") || 
                   lowerMessage.contains("遗憾") || 
                   lowerMessage.contains("失败") ||
                   lowerMessage.contains("错误") ||
                   lowerMessage.contains("😔") ||
                   lowerMessage.contains("😢")) {
            return "sad";
        } else if (lowerMessage.contains("思考") || 
                   lowerMessage.contains("考虑") || 
                   lowerMessage.contains("分析") || 
                   lowerMessage.contains("让我想想") ||
                   lowerMessage.contains("处理中") ||
                   lowerMessage.contains("🤔")) {
            return "thinking";
        } else {
            return "neutral";
        }
    }

    @Override
    public void openCustomizeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customize_avatar.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("自定义虚拟助手");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.error("打开自定义对话框时出错", e);
        }
    }

    @Override
    public void load2DAvatar(String avatarPath) {
        try {
            // 确保目标目录存在
            FileUtils.ensureDirectoryExists(avatarResourcesPath);
            
            // 处理外部文件路径
            String targetHtmlPath;
            if (avatarPath.startsWith("file:") || (new File(avatarPath)).exists()) {
                File sourceFile = new File(avatarPath.startsWith("file:") ? 
                    new URL(avatarPath).getFile() : avatarPath);
                
                if (sourceFile.exists()) {
                    // 如果是HTML文件直接使用，否则创建HTML包装
                    if (avatarPath.endsWith(".html")) {
                        String fileName = sourceFile.getName();
                        File targetFile = new File(avatarResourcesPath, fileName);
                        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        targetHtmlPath = targetFile.getAbsolutePath();
                    } else {
                        // 复制图片文件
                        String imgFileName = "avatar_" + System.currentTimeMillis() + "_" + sourceFile.getName();
                        File targetImgFile = new File(avatarResourcesPath + "/2d", imgFileName);
                        if (!targetImgFile.getParentFile().exists()) {
                            targetImgFile.getParentFile().mkdirs();
                        }
                        Files.copy(sourceFile.toPath(), targetImgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        // 创建HTML文件
                        String htmlFileName = "avatar_" + System.currentTimeMillis() + ".html";
                        File htmlFile = new File(avatarResourcesPath + "/2d", htmlFileName);
                        
                        // 简单的HTML，显示图片
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
                                "    <img id=\"avatar\" class=\"avatar\" src=\"" + imgFileName + "\" alt=\"AI Assistant Avatar\">\n" +
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
                        Files.writeString(htmlFile.toPath(), htmlContent);
                        targetHtmlPath = htmlFile.getAbsolutePath();
                    }
                    
                    // 更新当前头像信息
                    currentAvatar = AvatarModel.builder()
                            .type("2d")
                            .path(targetHtmlPath)
                            .name("2D自定义头像")
                            .description("从图片创建的2D头像")
                            .expression("neutral")
                            .build();
                    
                    // 加载2D形象
                    File htmlFile = new File(targetHtmlPath);
                    String fileUrl = htmlFile.toURI().toURL().toString();
                    Platform.runLater(() -> {
                        webEngine.load(fileUrl);
                        log.info("加载2D形象: {}", fileUrl);
                    });
                } else {
                    log.error("源文件不存在: {}", avatarPath);
                    
                    // 如果外部文件不存在，尝试使用默认2D头像
                    useDefault2DAvatar();
                }
            } else {
                // 尝试使用默认2D头像
                useDefault2DAvatar();
            }
        } catch (Exception e) {
            log.error("加载2D形象时出错", e);
            
            // 尝试使用默认2D头像
            useDefault2DAvatar();
        }
    }
    
    /**
     * 使用默认2D头像
     */
    private void useDefault2DAvatar() {
        try {
            String defaultAvatarPath = resourceDownloader.getDefaultTwoDHtmlPath();
            File htmlFile = new File(defaultAvatarPath);
            
            if (htmlFile.exists()) {
                String fileUrl = htmlFile.toURI().toURL().toString();
                Platform.runLater(() -> {
                    webEngine.load(fileUrl);
                    log.info("加载默认2D形象: {}", fileUrl);
                });
                
                // 更新当前头像信息
                currentAvatar = AvatarModel.builder()
                        .type("2d")
                        .path(defaultAvatarPath)
                        .name("默认2D头像")
                        .description("系统默认的2D头像")
                        .expression("neutral")
                        .build();
            } else {
                log.error("默认2D头像不存在: {}", defaultAvatarPath);
            }
        } catch (Exception e) {
            log.error("加载默认2D头像时出错", e);
        }
    }

    @Override
    public void load3DAvatar(String avatarPath) {
        try {
            // 处理文件路径
            if (avatarPath.startsWith("file:") || (new File(avatarPath)).exists()) {
                File sourceFile = new File(avatarPath.startsWith("file:") ? 
                    new URL(avatarPath).getFile() : avatarPath);
                
                if (sourceFile.exists()) {
                    String fileName = sourceFile.getName();
                    String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                    
                    // 根据文件类型处理
                    if (extension.equals("html")) {
                        // 如果是HTML文件，直接复制
                        File targetFile = new File(avatarResourcesPath + "/3d", fileName);
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }
                        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        // 加载HTML
                        String fileUrl = targetFile.toURI().toURL().toString();
                        Platform.runLater(() -> {
                            webEngine.load(fileUrl);
                            log.info("加载3D形象HTML: {}", fileUrl);
                        });
                    } else if (extension.equals("glb") || extension.equals("gltf") || extension.equals("obj")) {
                        // 如果是3D模型文件，复制文件并创建HTML包装
                        File targetModelFile = new File(avatarResourcesPath + "/3d", fileName);
                        if (!targetModelFile.getParentFile().exists()) {
                            targetModelFile.getParentFile().mkdirs();
                        }
                        Files.copy(sourceFile.toPath(), targetModelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        // 创建HTML文件
                        String htmlFileName = "model_" + System.currentTimeMillis() + ".html";
                        File htmlFile = new File(avatarResourcesPath + "/3d", htmlFileName);
                        
                        // 创建GLTF查看器HTML
                        String htmlContent = ThreeDUtils.createGLTFViewerHtml(fileName);
                        Files.writeString(htmlFile.toPath(), htmlContent);
                        
                        // 加载HTML
                        String fileUrl = htmlFile.toURI().toURL().toString();
                        Platform.runLater(() -> {
                            webEngine.load(fileUrl);
                            log.info("加载3D模型: {}", fileUrl);
                        });
                    } else {
                        log.error("不支持的文件类型: {}", extension);
                        useDefault3DAvatar();
                        return;
                    }
                    
                    // 更新当前头像信息
                    currentAvatar = AvatarModel.builder()
                            .type("3d")
                            .path(avatarPath)
                            .name("3D自定义头像")
                            .description("用户上传的3D头像")
                            .expression("neutral")
                            .build();
                } else {
                    log.error("源文件不存在: {}", avatarPath);
                    useDefault3DAvatar();
                }
            } else {
                useDefault3DAvatar();
            }
        } catch (Exception e) {
            log.error("加载3D形象时出错", e);
            useDefault3DAvatar();
        }
    }
    
    /**
     * 使用默认3D头像
     */
    private void useDefault3DAvatar() {
        try {
            String defaultAvatarPath = resourceDownloader.getDefaultThreeDHtmlPath();
            File htmlFile = new File(defaultAvatarPath);
            
            if (htmlFile.exists()) {
                String fileUrl = htmlFile.toURI().toURL().toString();
                Platform.runLater(() -> {
                    webEngine.load(fileUrl);
                    log.info("加载默认3D形象: {}", fileUrl);
                });
                
                // 更新当前头像信息
                currentAvatar = AvatarModel.builder()
                        .type("3d")
                        .path(defaultAvatarPath)
                        .name("默认3D头像")
                        .description("系统默认的3D头像")
                        .expression("neutral")
                        .build();
            } else {
                log.error("默认3D头像不存在: {}", defaultAvatarPath);
                createFallbackAvatar();
            }
        } catch (Exception e) {
            log.error("加载默认3D头像时出错", e);
            createFallbackAvatar();
        }
    }

    @Override
    public void generateAvatarFromDescription(String description) {
        log.info("根据描述生成形象: {}", description);
        
        // 调用AI生成形象描述
        aiService.processMessage("请根据以下描述生成一个虚拟形象的外观特征详细描述: " + description)
            .thenAccept(aiResponse -> {
                log.info("AI生成的形象描述: {}", aiResponse);
                
                // 目前，我们可以创建一个基本的3D形象作为示例
                Platform.runLater(() -> {
                    createBasic3DAvatarFromDescription(aiResponse);
                });
            });
    }
    
    /**
     * 根据AI描述创建基本3D头像
     */
    private void createBasic3DAvatarFromDescription(String description) {
        try {
            // 创建一个基于描述的3D头像（实际上应该集成更高级的模型生成API）
            // 这里仅为示例，创建一个带有描述的基本3D头像
            
            String htmlFileName = "generated_" + System.currentTimeMillis() + ".html";
            Path htmlFile = Paths.get(avatarResourcesPath, "3d", htmlFileName);
            if (!htmlFile.getParent().toFile().exists()) {
                Files.createDirectories(htmlFile.getParent());
            }
            
            // 创建一个基本的3D头像，并在注释中包含描述
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Generated Avatar</title>\n" +
                    "    <style>\n" +
                    "        body { margin: 0; overflow: hidden; background-color: #2a2a2a; }\n" +
                    "        #info { position: absolute; bottom: 10px; width: 100%; text-align: center; color: #ffffff; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <!-- Generated from description: " + description.replace("-->", "-\\->") + " -->\n" +
                    "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/build/three.min.js\"></script>\n" +
                    "    <script>\n" +
                    "        // 创建3D场景\n" +
                    "        const scene = new THREE.Scene();\n" +
                    "        scene.background = new THREE.Color(0x2a2a2a);\n" +
                    "        \n" +
                    "        // 创建相机\n" +
                    "        const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);\n" +
                    "        camera.position.z = 5;\n" +
                    "        \n" +
                    "        // 创建渲染器\n" +
                    "        const renderer = new THREE.WebGLRenderer({ antialias: true });\n" +
                    "        renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                    "        document.body.appendChild(renderer.domElement);\n" +
                    "        \n" +
                    "        // 创建球体几何体\n" +
                    "        const geometry = new THREE.SphereGeometry(2, 32, 32);\n" +
                    "        \n" +
                    "        // 创建材质\n" +
                    "        const material = new THREE.MeshPhongMaterial({\n" +
                    "            color: 0x156289,\n" +
                    "            emissive: 0x072534,\n" +
                    "            side: THREE.DoubleSide,\n" +
                    "            flatShading: false\n" +
                    "        });\n" +
                    "        \n" +
                    "        // 创建网格\n" +
                    "        const sphere = new THREE.Mesh(geometry, material);\n" +
                    "        scene.add(sphere);\n" +
                    "        \n" +
                    "        // 添加光源\n" +
                    "        const light = new THREE.DirectionalLight(0xffffff, 1);\n" +
                    "        light.position.set(1, 1, 1).normalize();\n" +
                    "        scene.add(light);\n" +
                    "        \n" +
                    "        const ambientLight = new THREE.AmbientLight(0x404040);\n" +
                    "        scene.add(ambientLight);\n" +
                    "        \n" +
                    "        // 处理窗口大小变化\n" +
                    "        window.addEventListener('resize', () => {\n" +
                    "            camera.aspect = window.innerWidth / window.innerHeight;\n" +
                    "            camera.updateProjectionMatrix();\n" +
                    "            renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                    "        });\n" +
                    "        \n" +
                    "        // 显示描述\n" +
                    "        const info = document.createElement('div');\n" +
                    "        info.id = 'info';\n" +
                    "        info.innerHTML = 'AI生成的头像<br><small>" + description.substring(0, 100) + "...</small>';\n" +
                    "        document.body.appendChild(info);\n" +
                    "        \n" +
                    "        // 动画循环\n" +
                    "        function animate() {\n" +
                    "            requestAnimationFrame(animate);\n" +
                    "            \n" +
                    "            sphere.rotation.x += 0.005;\n" +
                    "            sphere.rotation.y += 0.01;\n" +
                    "            \n" +
                    "            renderer.render(scene, camera);\n" +
                    "        }\n" +
                    "        \n" +
                    "        animate();\n" +
                    "        \n" +
                    "        // 表情更新接口\n" +
                    "        window.updateExpression = function(expression) {\n" +
                    "            console.log('Expression updated:', expression);\n" +
                    "            \n" +
                    "            if (expression === 'happy') {\n" +
                    "                material.color.set(0x2ca02c); // 绿色\n" +
                    "            } else if (expression === 'sad') {\n" +
                    "                material.color.set(0x9467bd); // 紫色\n" +
                    "            } else if (expression === 'thinking') {\n" +
                    "                material.color.set(0xd62728); // 红色\n" +
                    "            } else {\n" +
                    "                material.color.set(0x156289); // 默认蓝色\n" +
                    "            }\n" +
                    "        };\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
            
            Files.writeString(htmlFile, htmlContent);
            
            // 更新当前头像信息
            currentAvatar = AvatarModel.builder()
                    .type("3d")
                    .path(htmlFile.toString())
                    .name("AI生成头像")
                    .description(description.length() > 100 ? description.substring(0, 100) + "..." : description)
                    .expression("neutral")
                    .build();
            
            // 加载生成的头像
            File file = htmlFile.toFile();
            String fileUrl = file.toURI().toURL().toString();
            Platform.runLater(() -> {
                webEngine.load(fileUrl);
                log.info("加载AI生成的形象: {}", fileUrl);
            });
        } catch (Exception e) {
            log.error("创建生成头像时出错", e);
        }
    }

    @Override
    public void createAvatarFromImage(String imagePath) {
        log.info("从图片创建形象: {}", imagePath);
        
        // 处理图片，作为2D头像加载
        try {
            Path sourceFile = Paths.get(imagePath);
            if (Files.exists(sourceFile)) {
                String fileName = "user_avatar_" + System.currentTimeMillis() + ".jpg";
                Path targetDir = Paths.get(avatarResourcesPath, "2d");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                Path targetFile = targetDir.resolve(fileName);
                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                
                // 创建2D HTML文件
                String htmlFileName = "2d_avatar_user_" + System.currentTimeMillis() + ".html";
                Path htmlFile = targetDir.resolve(htmlFileName);
                
                // 创建HTML内容
                String htmlContent = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>User Avatar</title>\n" +
                        "    <style>\n" +
                        "        body { margin: 0; overflow: hidden; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #2a2a2a; }\n" +
                        "        .avatar { width: 300px; height: 300px; border-radius: 50%; object-fit: cover; transition: all 0.3s ease; }\n" +
                        "        .avatar:hover { transform: scale(1.05); box-shadow: 0 0 20px rgba(255,255,255,0.5); }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <img id=\"avatar\" class=\"avatar\" src=\"" + fileName + "\" alt=\"User Avatar\">\n" +
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
                        "            // 根据表情调整图片效果\n" +
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
                
                Files.writeString(htmlFile, htmlContent);
                
                // 更新当前头像信息
                currentAvatar = AvatarModel.builder()
                        .type("2d")
                        .path(htmlFile.toString())
                        .name("用户自定义头像")
                        .description("从用户图片创建的2D头像")
                        .expression("neutral")
                        .build();
                
                // 加载2D形象
                File file = htmlFile.toFile();
                String fileUrl = file.toURI().toURL().toString();
                Platform.runLater(() -> {
                    webEngine.load(fileUrl);
                    log.info("加载用户图片形象: {}", fileUrl);
                });
            } else {
                log.error("源文件不存在: {}", imagePath);
                useDefault2DAvatar();
            }
        } catch (IOException e) {
            log.error("处理图片时出错", e);
            useDefault2DAvatar();
        }
    }

    @Override
    public void setDynamicBackground(boolean enabled, String effectType) {
        // 实现动态背景效果
        if (webEngine != null) {
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript(
                        "if(typeof setDynamicBackground === 'function') { " +
                        "setDynamicBackground(" + enabled + ", '" + effectType + "'); }");
                    log.info("设置动态背景: enabled={}, type={}", enabled, effectType);
                } catch (Exception e) {
                    log.error("设置动态背景时出错", e);
                }
            });
        }
    }
    
    @Override
    public void syncWithUserFace(Object faceFeatures) {
        if (webEngine != null && faceFeatures != null) {
            // 提取面部特征
            try {
                Platform.runLater(() -> {
                    try {
                        // 构造JavaScript命令来同步表情
                        StringBuilder jsCommand = new StringBuilder("if(typeof syncFacialExpression === 'function') { syncFacialExpression({");
                        
                        // 尝试从faceFeatures中提取面部表情数据，这里以JSON格式传递
                        // 如果faceFeatures是FaceFeatures类型，可以获取其属性
                        if (faceFeatures.getClass().getSimpleName().equals("FaceFeatures")) {
                            // 通过反射获取属性
                            try {
                                java.lang.reflect.Method getEmotion = faceFeatures.getClass().getMethod("getEmotion");
                                Object emotion = getEmotion.invoke(faceFeatures);
                                jsCommand.append("emotion:'").append(emotion == null ? "neutral" : emotion).append("',");
                                
                                java.lang.reflect.Method getMouthOpenness = faceFeatures.getClass().getMethod("getMouthOpenness");
                                Object mouthOpenness = getMouthOpenness.invoke(faceFeatures);
                                jsCommand.append("mouthOpenness:").append(mouthOpenness == null ? 0.0 : mouthOpenness).append(",");
                                
                                java.lang.reflect.Method getEyeOpenness = faceFeatures.getClass().getMethod("getEyeOpenness");
                                Object eyeOpenness = getEyeOpenness.invoke(faceFeatures);
                                if (eyeOpenness != null && eyeOpenness.getClass().isArray()) {
                                    double[] eyeOpennessArray = (double[]) eyeOpenness;
                                    if (eyeOpennessArray.length > 0) {
                                        jsCommand.append("leftEyeOpenness:").append(eyeOpennessArray[0]).append(",");
                                    }
                                    if (eyeOpennessArray.length > 1) {
                                        jsCommand.append("rightEyeOpenness:").append(eyeOpennessArray[1]).append(",");
                                    }
                                }
                                
                                java.lang.reflect.Method getHeadPose = faceFeatures.getClass().getMethod("getHeadPose");
                                Object headPose = getHeadPose.invoke(faceFeatures);
                                if (headPose != null && headPose.getClass().isArray()) {
                                    double[] headPoseArray = (double[]) headPose;
                                    if (headPoseArray.length > 0) {
                                        jsCommand.append("headYaw:").append(headPoseArray[0]).append(",");
                                    }
                                    if (headPoseArray.length > 1) {
                                        jsCommand.append("headPitch:").append(headPoseArray[1]).append(",");
                                    }
                                    if (headPoseArray.length > 2) {
                                        jsCommand.append("headRoll:").append(headPoseArray[2]).append(",");
                                    }
                                }
                            } catch (Exception e) {
                                log.debug("通过反射获取面部特征属性时出错", e);
                            }
                        } else {
                            // 如果是简单的表情字符串
                            jsCommand.append("emotion:'").append(faceFeatures.toString()).append("',");
                        }
                        
                        // 移除最后的逗号并关闭命令
                        if (jsCommand.charAt(jsCommand.length() - 1) == ',') {
                            jsCommand.setLength(jsCommand.length() - 1);
                        }
                        jsCommand.append("}); }");
                        
                        // 执行JavaScript
                        webEngine.executeScript(jsCommand.toString());
                    } catch (Exception e) {
                        log.debug("同步面部表情时出错", e);
                    }
                });
            } catch (Exception e) {
                log.error("处理面部特征时出错", e);
            }
        }
    }

    @Override
    public void setBackgroundImage(String backgroundPath) {
        if (webEngine != null) {
            Platform.runLater(() -> {
                try {
                    // 构造JavaScript命令设置背景图片
                    String jsCommand = "if(typeof setBackgroundImage === 'function') { " +
                                      "setBackgroundImage('" + backgroundPath + "'); }";
                    webEngine.executeScript(jsCommand);
                    log.info("设置背景图片: {}", backgroundPath);
                } catch (Exception e) {
                    log.error("设置背景图片时出错", e);
                }
            });
        }
    }
    
    @Override
    public void setBackground3DScene(String sceneType, String customPath) {
        if (webEngine != null) {
            Platform.runLater(() -> {
                try {
                    // 构造JavaScript命令设置3D背景场景
                    String jsCommand = "if(typeof setBackground3DScene === 'function') { " +
                                      "setBackground3DScene('" + sceneType + "', '" + 
                                      (customPath != null ? customPath : "") + "'); }";
                    webEngine.executeScript(jsCommand);
                    log.info("设置3D背景场景: type={}, customPath={}", sceneType, customPath);
                } catch (Exception e) {
                    log.error("设置3D背景场景时出错", e);
                }
            });
        }
    }
    
    @Override
    public void setBackgroundColor(String colorHex) {
        if (webEngine != null) {
            Platform.runLater(() -> {
                try {
                    // 构造JavaScript命令设置背景颜色
                    String jsCommand = "if(typeof setBackgroundColor === 'function') { " +
                                      "setBackgroundColor('" + colorHex + "'); }";
                    webEngine.executeScript(jsCommand);
                    log.info("设置背景颜色: {}", colorHex);
                } catch (Exception e) {
                    log.error("设置背景颜色时出错", e);
                }
            });
        }
    }
} 