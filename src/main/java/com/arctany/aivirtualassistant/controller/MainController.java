package com.arctany.aivirtualassistant.controller;

import com.arctany.aivirtualassistant.model.FaceFeatures;
import com.arctany.aivirtualassistant.service.AIService;
import com.arctany.aivirtualassistant.service.AvatarService;
import com.arctany.aivirtualassistant.service.SpeechService;
import com.arctany.aivirtualassistant.service.VisionService;
import com.arctany.aivirtualassistant.service.VisionService.FaceDetectionListener;
import com.arctany.aivirtualassistant.util.I18nUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
public class MainController implements Initializable, FaceDetectionListener {

    @FXML private WebView avatarView;
    @FXML private TextArea chatHistoryArea;
    @FXML private TextField userInputField;
    @FXML private Button sendButton;
    @FXML private Button customizeButton;
    @FXML private Label statusLabel;
    @FXML private ImageView cameraPreview;
    @FXML private Button languageButton;

    private final AIService aiService;
    private final AvatarService avatarService;
    private final SpeechService speechService;
    private final VisionService visionService;
    private final ApplicationContext applicationContext;
    
    private ResourceBundle resources;

    @Autowired
    public MainController(AIService aiService, AvatarService avatarService, 
                         SpeechService speechService, VisionService visionService,
                         ApplicationContext applicationContext) {
        this.aiService = aiService;
        this.avatarService = avatarService;
        this.speechService = speechService;
        this.visionService = visionService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        
        // 设置国际化文本
        initializeI18nText();
        
        // 初始化服务
        initializeServices();
        
        // 设置事件处理
        setupEventHandlers();
    }
    
    /**
     * 初始化国际化文本
     */
    private void initializeI18nText() {
        // 设置按钮文本
        if (sendButton != null) {
            sendButton.setText(resources.getString("main.send"));
        }
        if (customizeButton != null) {
            customizeButton.setText(resources.getString("main.customize"));
        }
        if (userInputField != null) {
            userInputField.setPromptText(resources.getString("main.message.placeholder"));
        }
        if (statusLabel != null) {
            statusLabel.setText(resources.getString("app.welcome"));
        }
        if (languageButton != null) {
            languageButton.setText(resources.getString("customize.language"));
        }
    }

    /**
     * 初始化服务
     */
    private void initializeServices() {
        try {
            // 初始化头像服务
            if (avatarView != null && avatarService != null) {
                avatarService.initializeAvatar(avatarView);
            }

            // 初始化摄像头服务并注册监听器
            if (visionService != null) {
                visionService.addFaceDetectionListener(this);
                visionService.initializeCamera(cameraPreview);
                visionService.startCamera();
            }

            // 初始化语音服务
            if (speechService != null) {
                speechService.initialize();
            }
        } catch (Exception e) {
            logError("初始化服务失败", e);
            updateStatus(resources.getString("system.error") + ": " + e.getMessage());
        }
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        if (sendButton != null) {
            sendButton.setOnAction(event -> sendMessage());
        }

        if (userInputField != null) {
            userInputField.setOnAction(event -> sendMessage());
        }

        if (customizeButton != null) {
            customizeButton.setOnAction(event -> showCustomizeDialog());
        }
    }
    
    /**
     * 显示语言切换界面
     */
    @FXML
    public void showLanguageSwitcher() {
        try {
            // 加载语言切换FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/language_switcher.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            loader.setResources(resources);
            
            Parent root = loader.load();
            
            // 创建新舞台
            Stage languageStage = new Stage();
            languageStage.initModality(Modality.APPLICATION_MODAL);
            languageStage.setTitle(resources.getString("customize.language"));
            languageStage.setScene(new Scene(root));
            
            // 显示语言切换界面
            languageStage.showAndWait();
        } catch (Exception e) {
            logError("打开语言切换界面失败", e);
            updateStatus(resources.getString("system.error") + ": " + e.getMessage());
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        String userMessage = userInputField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // 显示用户消息
        appendToChatHistory("You: " + userMessage);
        userInputField.clear();

        // 更新状态
        updateStatus(resources.getString("system.processing"));

        // 异步处理AI响应
        CompletableFuture<String> responseFuture = aiService.processMessage(userMessage);
        responseFuture.thenAccept(aiResponse -> {
            // 生成语音并同步口型
            if (speechService != null) {
                speechService.speak(aiResponse);
                // 语音完成后的处理可以在SpeechService的实现类中处理
                Platform.runLater(() -> updateStatus(resources.getString("app.welcome")));
            }
            
            // 在UI线程中更新聊天历史
            Platform.runLater(() -> {
                appendToChatHistory("AI: " + aiResponse);
            });
        }).exceptionally(e -> {
            logError("处理消息时出错", new Exception(e));
            Platform.runLater(() -> updateStatus(resources.getString("system.error") + ": " + e.getMessage()));
            return null;
        });
    }

    /**
     * 显示自定义对话框
     */
    private void showCustomizeDialog() {
        try {
            // 跳转到自定义界面的逻辑
        } catch (Exception e) {
            logError("打开自定义界面失败", e);
            updateStatus(resources.getString("system.error") + ": " + e.getMessage());
        }
    }

    /**
     * 添加消息到聊天历史
     */
    private void appendToChatHistory(String message) {
        if (chatHistoryArea != null) {
            chatHistoryArea.appendText(message + "\n\n");
            // 滚动到底部
            chatHistoryArea.setScrollTop(Double.MAX_VALUE);
        }
    }

    /**
     * 更新状态标签
     */
    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    /**
     * 记录错误日志
     */
    private void logError(String message, Exception e) {
        log.error(message + ": " + e.getMessage(), e);
    }

    /**
     * 当检测到人脸时的回调
     */
    @Override
    public void onFaceDetected(int[] facePosition, Object faceFeatures) {
        if (faceFeatures != null && faceFeatures instanceof FaceFeatures && avatarService != null) {
            // 同步AI头像表情
            avatarService.syncWithUserFace(faceFeatures);
        }
    }
    
    /**
     * 当人脸消失时的回调
     */
    @Override
    public void onFaceLost() {
        // 可以在这里添加人脸消失时的处理逻辑
        log.debug("人脸消失");
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        if (visionService != null) {
            visionService.stopCamera();
        }
    }
}