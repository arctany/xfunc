package com.arctany.aivirtualassistant.controller;

import com.arctany.aivirtualassistant.model.AvatarModel;
import com.arctany.aivirtualassistant.service.AIService;
import com.arctany.aivirtualassistant.service.AvatarService;
import com.arctany.aivirtualassistant.service.SpeechService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.List;

/**
 * 自定义形象控制器类
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomizeController {

    private final AvatarService avatarService;
    private final SpeechService speechService;
    private final AIService aiService;

    @FXML
    private Button load2DButton;
    
    @FXML
    private Button load3DButton;
    
    @FXML
    private TextArea descriptionTextArea;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private Button selectImageButton;
    
    @FXML
    private Button selectVoiceButton;
    
    @FXML
    private Button loadVoiceButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private TextField avatarNameField;

    @FXML
    public void initialize() {
        // 设置加载2D形象按钮事件
        load2DButton.setOnAction(e -> load2DAvatar());
        
        // 设置加载3D形象按钮事件
        load3DButton.setOnAction(e -> load3DAvatar());
        
        // 设置AI生成形象按钮事件
        generateButton.setOnAction(e -> generateAvatar());
        
        // 设置选择图片按钮事件
        selectImageButton.setOnAction(e -> selectImage());
        
        // 设置选择语音按钮事件
        selectVoiceButton.setOnAction(e -> selectVoice());
        
        // 设置加载语音按钮事件
        loadVoiceButton.setOnAction(e -> loadVoice());
        
        // 设置取消按钮事件
        cancelButton.setOnAction(e -> closeDialog());
        
        // 设置保存按钮事件
        saveButton.setOnAction(e -> saveChanges());
    }

    private void load2DAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择2D形象图片");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            // 使用服务加载2D形象
            avatarService.load2DAvatar(selectedFile.getAbsolutePath());
            
            // 设置自定义名称 (如果有)
            if (avatarNameField != null && !avatarNameField.getText().trim().isEmpty()) {
                // 这里可以设置头像名称，但需要修改服务接口
            }
            
            // 关闭对话框
            closeDialog();
        }
    }

    private void load3DAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择3D模型文件");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("3D模型文件", "*.gltf", "*.glb", "*.obj", "*.fbx", "*.html")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            // 使用服务加载3D形象
            avatarService.load3DAvatar(selectedFile.getAbsolutePath());
            
            // 设置自定义名称 (如果有)
            if (avatarNameField != null && !avatarNameField.getText().trim().isEmpty()) {
                // 这里可以设置头像名称，但需要修改服务接口
            }
            
            // 关闭对话框
            closeDialog();
        }
    }

    private void generateAvatar() {
        String description = descriptionTextArea.getText().trim();
        if (!description.isEmpty()) {
            // 使用AI服务解析用户描述，生成更详细的描述
            String enhancedDescription = description;
            
            // 如果用户输入很短，尝试扩展描述
            if (description.length() < 50) {
                log.info("尝试扩展形象描述...");
                
                aiService.processMessage("请把以下形象描述扩展成更详细的描述，包括发型、面部特征、表情、服装等：" + description)
                    .thenAccept(aiResponse -> {
                        generateAvatarWithDescription(aiResponse);
                    });
            } else {
                generateAvatarWithDescription(description);
            }
            
            // 关闭对话框
            closeDialog();
        }
    }
    
    private void generateAvatarWithDescription(String description) {
        // 调用头像生成服务
        avatarService.generateAvatarFromDescription(description);
    }

    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择人物图片");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            // 使用服务从图片创建形象
            avatarService.createAvatarFromImage(selectedFile.getAbsolutePath());
            
            // 关闭对话框
            closeDialog();
        }
    }

    private void selectVoice() {
        // 在实际实现中，这里应该显示一个可用语音列表
        
        // 显示一个简单的选择对话框
        javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(
            "默认语音", List.of("默认语音", "男声", "女声", "儿童声", "机器人声")
        );
        dialog.setTitle("选择语音");
        dialog.setHeaderText("请选择AI助手的语音");
        dialog.setContentText("可用语音:");
        
        dialog.showAndWait().ifPresent(voice -> {
            // 设置语音
            speechService.setVoice(voice);
            log.info("选择语音: {}", voice);
        });
    }

    private void loadVoice() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择语音样本");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("音频文件", "*.wav", "*.mp3")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            // 使用服务从音频样本自定义语音
            speechService.customizeVoiceFromSample(selectedFile.getAbsolutePath());
            
            // 提示用户语音已自定义
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("语音自定义");
            alert.setHeaderText("语音已自定义");
            alert.setContentText("已成功从音频样本自定义语音。");
            alert.showAndWait();
        }
    }

    private void closeDialog() {
        getStage().close();
    }

    private void saveChanges() {
        // 保存更改（这里实际上没有什么需要额外保存的，因为更改是实时应用的）
        log.info("保存自定义设置");
        closeDialog();
    }

    private Stage getStage() {
        return (Stage) cancelButton.getScene().getWindow();
    }
} 