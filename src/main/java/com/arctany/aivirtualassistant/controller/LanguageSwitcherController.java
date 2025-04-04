package com.arctany.aivirtualassistant.controller;

import com.arctany.aivirtualassistant.util.I18nUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 语言切换控制器，负责处理语言切换功能
 */
@Slf4j
@Controller
public class LanguageSwitcherController implements Initializable {

    @FXML private Button chineseButton;
    @FXML private Button englishButton;
    @FXML private Label currentLanguageLabel;
    
    private ResourceBundle resources;
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public LanguageSwitcherController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        updateLanguageLabel();
    }
    
    /**
     * 切换到中文
     */
    @FXML
    public void switchToChinese() {
        log.info("切换语言到中文");
        I18nUtils.setLanguage("zh_CN");
        updateLanguageLabel();
        showRestartRequiredMessage();
    }
    
    /**
     * 切换到英文
     */
    @FXML
    public void switchToEnglish() {
        log.info("切换语言到英文");
        I18nUtils.setLanguage("en_US");
        updateLanguageLabel();
        showRestartRequiredMessage();
    }
    
    /**
     * 更新语言标签
     */
    private void updateLanguageLabel() {
        String currentLanguage = I18nUtils.getCurrentLanguage();
        Platform.runLater(() -> {
            if (currentLanguage.startsWith("zh")) {
                currentLanguageLabel.setText("当前语言：中文");
            } else {
                currentLanguageLabel.setText("Current Language: English");
            }
        });
    }
    
    /**
     * 显示需要重启的消息
     */
    private void showRestartRequiredMessage() {
        String title, message;
        Locale locale = Locale.getDefault();
        
        if (locale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
            title = "需要重启";
            message = "语言已更改，完整的界面更新需要重启应用程序。\n请关闭并重新启动应用程序以应用更改。";
        } else {
            title = "Restart Required";
            message = "Language has been changed. A full UI update requires restarting the application.\nPlease close and restart the application to apply changes.";
        }
        
        // 使用JavaFX的Alert显示消息
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            // 关闭当前窗口
            Stage stage = (Stage) chineseButton.getScene().getWindow();
            stage.close();
        });
    }
} 