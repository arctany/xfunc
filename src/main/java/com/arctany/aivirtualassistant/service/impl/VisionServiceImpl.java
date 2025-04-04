package com.arctany.aivirtualassistant.service.impl;

import com.arctany.aivirtualassistant.model.FaceFeatures;
import com.arctany.aivirtualassistant.service.VisionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

/**
 * 增强版视觉服务实现类，支持高级面部特征识别
 */
@Slf4j
@Service
public class VisionServiceImpl implements VisionService {

    private final List<FaceDetectionListener> faceListeners = new ArrayList<>();
    private final ScheduledExecutorService frameExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService faceAnalysisExecutor = Executors.newSingleThreadExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private ImageView cameraView;
    private BufferedImage cameraFrame;
    private boolean isCameraActive = false;
    private Path imagesDir;
    private boolean isFaceTracking = false;
    
    // 当前检测到的面部特征
    private FaceFeatures currentFaceFeatures;
    private long lastFaceDetectionTime = 0;
    
    // 面部分析API配置
    @Value("${vision.face.detection.enabled:true}")
    private boolean faceDetectionEnabled;
    
    @Value("${vision.face.detection.interval:500}")
    private long faceDetectionInterval;
    
    @Value("${vision.api.use.local:true}")
    private boolean useLocalDetection;
    
    @Value("${vision.api.key:}")
    private String faceApiKey;

    @Override
    public void initializeCamera(ImageView imageView) {
        this.cameraView = imageView;
        
        try {
            // 创建图片保存目录
            imagesDir = Paths.get("src/main/resources/images");
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }
            
            // 初始化摄像头和OpenCV库
            initializeOpenCV();
            
            log.info("视觉服务初始化完成，面部检测: {}", faceDetectionEnabled ? "启用" : "禁用");
        } catch (Exception e) {
            log.error("初始化视觉服务出错", e);
        }
    }

    private void initializeOpenCV() {
        try {
            // 尝试加载OpenCV本地库
            try {
                // 根据操作系统加载适当的库
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.contains("win")) {
                    System.load(new File("src/main/resources/natives/opencv_java455.dll").getAbsolutePath());
                } else if (osName.contains("mac")) {
                    System.load(new File("src/main/resources/natives/libopencv_java455.dylib").getAbsolutePath());
                } else if (osName.contains("nix") || osName.contains("nux")) {
                    System.load(new File("src/main/resources/natives/libopencv_java455.so").getAbsolutePath());
                }
                log.info("OpenCV本地库加载成功");
            } catch (UnsatisfiedLinkError | Exception e) {
                log.warn("无法加载OpenCV本地库，将使用备用方案: {}", e.getMessage());
            }
            
            // 创建默认图像
            BufferedImage defaultImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = defaultImage.createGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 640, 480);
            g.setColor(Color.BLACK);
            g.drawString("摄像头未启动", 280, 240);
            g.dispose();
            
            this.cameraFrame = defaultImage;
            updateCameraView(cameraFrame);
            
            log.info("OpenCV初始化完成");
        } catch (Exception e) {
            log.error("初始化OpenCV出错", e);
        }
    }

    private void updateCameraView(BufferedImage frame) {
        if (cameraView != null && frame != null) {
            Platform.runLater(() -> {
                Image fxImage = convertToFxImage(frame);
                cameraView.setImage(fxImage);
            });
        }
    }

    private Image convertToFxImage(BufferedImage image) {
        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter pw = writableImage.getPixelWriter();
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pw.setArgb(x, y, image.getRGB(x, y));
            }
        }
        
        return writableImage;
    }

    @Override
    public void startCamera() {
        if (isCameraActive) {
            return;
        }
        
        try {
            isCameraActive = true;
            
            // 尝试使用OpenCV或开源库连接摄像头
            boolean openCVLoaded = detectOpenCVLoaded();
            
            // 启动摄像头捕获线程
            frameExecutor.scheduleAtFixedRate(() -> {
                if (!isCameraActive) {
                    return;
                }
                
                try {
                    BufferedImage frame;
                    
                    if (openCVLoaded) {
                        // 使用OpenCV获取帧
                        frame = captureFrameOpenCV();
                    } else {
                        // 使用模拟的摄像头帧
                        frame = createSimulatedFrame();
                    }
                    
                    // 更新当前帧并显示
                    cameraFrame = frame;
                    updateCameraView(drawOverlays(frame));
                    
                    // 检测和跟踪面部
                    if (faceDetectionEnabled && 
                        (System.currentTimeMillis() - lastFaceDetectionTime) > faceDetectionInterval) {
                        detectFacesAsync(frame);
                        lastFaceDetectionTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    log.error("处理摄像头帧时出错", e);
                }
            }, 0, 33, TimeUnit.MILLISECONDS); // ~30 FPS
            
            log.info("摄像头已启动");
        } catch (Exception e) {
            log.error("启动摄像头出错", e);
            isCameraActive = false;
        }
    }
    
    /**
     * 检测OpenCV是否成功加载
     */
    private boolean detectOpenCVLoaded() {
        try {
            // 在这里执行OpenCV的简单调用来验证加载
            // 如：Core.VERSION 或类似操作
            return false; // 目前暂时返回false以使用模拟模式
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 使用OpenCV捕获摄像头帧
     */
    private BufferedImage captureFrameOpenCV() {
        // 实际应用中，这里应该使用OpenCV读取摄像头
        // 目前使用模拟帧
        return createSimulatedFrame();
    }
    
    /**
     * 创建模拟的摄像头帧
     */
    private BufferedImage createSimulatedFrame() {
        BufferedImage frame = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 640, 480);
        
        // 添加时间戳
        g.setColor(Color.BLACK);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        g.drawString(timestamp, 20, 30);
        
        // 添加一个简单的面部模拟
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(220, 140, 200, 200);
        
        // 添加眼睛和嘴巴
        g.setColor(Color.BLACK);
        g.fillOval(270, 200, 30, 20);
        g.fillOval(340, 200, 30, 20);
        
        // 根据面部特征绘制不同表情
        if (currentFaceFeatures != null) {
            if (currentFaceFeatures.getEmotion().equals("happy")) {
                // 微笑
                g.drawArc(270, 240, 100, 60, 0, -180);
            } else if (currentFaceFeatures.getEmotion().equals("sad")) {
                // 悲伤
                g.drawArc(270, 270, 100, 60, 0, 180);
            } else {
                // 中性
                g.drawLine(270, 260, 370, 260);
            }
        } else {
            g.drawLine(270, 260, 370, 260);
        }
        
        g.dispose();
        return frame;
    }
    
    /**
     * 在摄像头帧上绘制覆盖信息
     */
    private BufferedImage drawOverlays(BufferedImage frame) {
        if (frame == null) return null;
        
        // 创建副本以避免修改原始帧
        BufferedImage overlay = new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType());
        Graphics2D g = overlay.createGraphics();
        g.drawImage(frame, 0, 0, null);
        
        // 如果有检测到的面部，绘制面部框和信息
        if (currentFaceFeatures != null) {
            int[] faceRect = currentFaceFeatures.getFaceRectangle();
            if (faceRect != null && faceRect.length == 4) {
                // 绘制面部框
                g.setColor(Color.GREEN);
                g.setStroke(new BasicStroke(2));
                g.drawRect(faceRect[0], faceRect[1], faceRect[2], faceRect[3]);
                
                // 绘制情绪信息
                g.setColor(Color.WHITE);
                g.fillRect(faceRect[0], faceRect[1] - 20, 100, 20);
                g.setColor(Color.BLACK);
                g.drawString("情绪: " + currentFaceFeatures.getEmotion(), faceRect[0] + 5, faceRect[1] - 5);
                
                // 绘制眼睛点
                int[][] eyePoints = currentFaceFeatures.getEyePoints();
                if (eyePoints != null) {
                    g.setColor(Color.BLUE);
                    for (int[] point : eyePoints) {
                        g.fillOval(point[0] - 2, point[1] - 2, 4, 4);
                    }
                }
                
                // 绘制嘴巴点
                int[][] mouthPoints = currentFaceFeatures.getMouthPoints();
                if (mouthPoints != null) {
                    g.setColor(Color.RED);
                    for (int[] point : mouthPoints) {
                        g.fillOval(point[0] - 2, point[1] - 2, 4, 4);
                    }
                }
            }
        }
        
        g.dispose();
        return overlay;
    }

    /**
     * 异步检测人脸
     */
    private void detectFacesAsync(BufferedImage frame) {
        if (!isFaceTracking) {
            isFaceTracking = true;
            faceAnalysisExecutor.submit(() -> {
                try {
                    FaceFeatures features;
                    
                    if (useLocalDetection) {
                        features = detectFacesLocal(frame);
                    } else {
                        features = detectFacesAPI(frame);
                    }
                    
                    if (features != null) {
                        // 更新当前面部特征
                        currentFaceFeatures = features;
                        
                        // 通知监听器
                        notifyFaceListeners(features);
                    } else if (currentFaceFeatures != null) {
                        // 如果丢失了人脸跟踪
                        for (FaceDetectionListener listener : faceListeners) {
                            listener.onFaceLost();
                        }
                        currentFaceFeatures = null;
                    }
                } catch (Exception e) {
                    log.error("面部检测出错", e);
                } finally {
                    isFaceTracking = false;
                }
            });
        }
    }
    
    /**
     * 本地人脸检测（简化版）
     */
    private FaceFeatures detectFacesLocal(BufferedImage frame) {
        // 对于实际应用，这里应该使用OpenCV或其他本地库进行面部检测
        // 简化版本：返回模拟的面部特征
        
        // 模拟面部位置
        int[] faceRect = new int[] {220, 140, 200, 200};
        
        // 模拟眼睛位置
        int[][] eyePoints = new int[][] {
            {270, 200}, {290, 200}, // 左眼两点
            {340, 200}, {360, 200}  // 右眼两点
        };
        
        // 模拟嘴巴位置
        int[][] mouthPoints = new int[][] {
            {270, 260}, {320, 280}, {370, 260} // 嘴巴三点
        };
        
        // 创建面部特征对象
        FaceFeatures features = new FaceFeatures();
        features.setFaceRectangle(faceRect);
        features.setEyePoints(eyePoints);
        features.setMouthPoints(mouthPoints);
        
        // 模拟随机情绪
        String[] emotions = {"neutral", "happy", "sad"};
        features.setEmotion(emotions[(int)(Math.random() * emotions.length)]);
        
        return features;
    }
    
    /**
     * 使用远程API进行人脸检测
     */
    private FaceFeatures detectFacesAPI(BufferedImage frame) {
        if (faceApiKey == null || faceApiKey.isEmpty()) {
            log.warn("未配置人脸API密钥，无法使用远程人脸检测");
            return null;
        }
        
        try {
            // 将图像转换为Base64字符串
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(frame, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // 创建HTTP客户端
            HttpClient client = HttpClient.newHttpClient();
            
            // 构建请求体
            String requestBody = "{\"image\":\"" + base64Image + "\"}";
            
            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.example.com/face-detection")) // 替换为实际的API端点
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + faceApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // 解析响应（实际应用中需要根据API返回格式进行调整）
            if (response.statusCode() == 200) {
                FaceFeatures features = objectMapper.readValue(response.body(), FaceFeatures.class);
                return features;
            } else {
                log.error("人脸API返回错误: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("调用人脸API出错", e);
            return null;
        }
    }
    
    /**
     * 通知所有监听器有关人脸检测结果
     */
    private void notifyFaceListeners(FaceFeatures features) {
        if (features != null) {
            for (FaceDetectionListener listener : faceListeners) {
                listener.onFaceDetected(features.getFaceRectangle(), features);
            }
        }
    }

    @Override
    public void stopCamera() {
        isCameraActive = false;
        
        // 恢复默认图像
        BufferedImage defaultImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = defaultImage.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 640, 480);
        g.setColor(Color.BLACK);
        g.drawString("摄像头已停止", 280, 240);
        g.dispose();
        
        updateCameraView(defaultImage);
        
        log.info("摄像头已停止");
    }

    @Override
    public String captureImage() {
        if (cameraFrame == null) {
            log.warn("无法拍照：摄像头未启动");
            return null;
        }
        
        try {
            // 创建文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "capture_" + timestamp + ".jpg";
            Path filePath = imagesDir.resolve(fileName);
            
            // 保存图片
            ImageIO.write(cameraFrame, "jpg", filePath.toFile());
            
            log.info("拍照成功: {}", filePath);
            return filePath.toString();
        } catch (Exception e) {
            log.error("拍照出错", e);
            return null;
        }
    }

    @Override
    public void addFaceDetectionListener(FaceDetectionListener listener) {
        faceListeners.add(listener);
    }
    
    @Override
    public FaceFeatures getCurrentFaceFeatures() {
        return currentFaceFeatures;
    }
    
    @Override
    public boolean isFaceDetected() {
        return currentFaceFeatures != null;
    }
} 