package com.arctany.aivirtualassistant;

import com.arctany.aivirtualassistant.util.I18nUtils;
import com.arctany.aivirtualassistant.util.ResourceDownloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
@Component
public class JavaFXApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static String[] savedArgs;
    private ResourceBundle resources;

    public static void launchApp(ConfigurableApplicationContext context, String[] args) {
        JavaFXApplication.springContext = context;
        savedArgs = args;
        Application.launch(JavaFXApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // 初始化资源束，根据系统语言获取对应的资源
            initializeResourceBundle();
            showLoadingStage(primaryStage);
        } catch (Exception e) {
            log.error("启动应用程序时出错", e);
            showErrorAndExit(e);
        }
    }
    
    /**
     * 初始化资源束
     */
    private void initializeResourceBundle() {
        try {
            // 获取系统默认语言
            Locale locale = Locale.getDefault();
            log.info("当前系统语言: {}", locale);
            
            // 根据系统语言选择资源束，如果没有对应的语言资源，则使用中文
            if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                resources = ResourceBundle.getBundle("i18n.messages", Locale.US);
            } else {
                resources = ResourceBundle.getBundle("i18n.messages", Locale.CHINA);
            }
        } catch (Exception e) {
            log.error("加载语言资源出错，使用默认资源", e);
            resources = ResourceBundle.getBundle("i18n.messages", Locale.CHINA);
        }
    }
    
    /**
     * 显示加载界面并准备资源
     * @param primaryStage 主舞台
     */
    private void showLoadingStage(Stage primaryStage) {
        // 创建加载界面
        BorderPane loadingRoot = new BorderPane();
        VBox loadingBox = new VBox(10);
        loadingBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(resources.getString("app.title"));
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        javafx.scene.control.Label loadingLabel = new javafx.scene.control.Label(resources.getString("app.loading"));
        loadingLabel.setStyle("-fx-font-size: 16px;");
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        
        loadingBox.getChildren().addAll(titleLabel, loadingLabel, progressBar);
        loadingRoot.setCenter(loadingBox);
        
        Scene loadingScene = new Scene(loadingRoot, 450, 200);
        primaryStage.setTitle(resources.getString("app.loading") + " - " + resources.getString("app.title"));
        primaryStage.setScene(loadingScene);
        primaryStage.show();
        
        // 创建资源准备任务
        Task<Void> resourceTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // 获取资源下载器
                    ResourceDownloader downloader = springContext.getBean(ResourceDownloader.class);
                    // 准备所有资源
                    downloader.prepareAllResources();
                } catch (Exception e) {
                    log.error("准备资源时出错", e);
                    throw e;
                }
                return null;
            }
        };
        
        // 任务完成后加载主界面
        resourceTask.setOnSucceeded(event -> {
            try {
                loadMainUI(primaryStage);
            } catch (Exception e) {
                log.error("加载主界面时出错", e);
                showErrorAndExit(e);
            }
        });
        
        // 任务出错时显示错误
        resourceTask.setOnFailed(event -> {
            Throwable exception = resourceTask.getException();
            log.error("资源准备失败", exception);
            showErrorAndExit(exception);
        });
        
        // 启动任务
        new Thread(resourceTask).start();
    }
    
    /**
     * 加载主界面
     * @param primaryStage 主舞台
     * @throws Exception 加载异常
     */
    private void loadMainUI(Stage primaryStage) throws Exception {
        try {
            // 创建资源目录
            new java.io.File("src/main/resources/fxml").mkdirs();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(springContext::getBean);
            // 设置资源束
            loader.setResources(resources);
            Parent root = loader.load();
            
            primaryStage.setTitle(resources.getString("app.title"));
            primaryStage.setScene(new Scene(root, 1024, 768));
            primaryStage.show();
            
            log.info("AI虚拟助手平台成功启动");
        } catch (Exception e) {
            log.error("加载FXML界面出错，使用默认界面", e);
            // 如果FXML文件不存在，创建默认界面
            createDefaultUI(primaryStage);
        }
    }
    
    /**
     * 显示错误并退出
     * @param e 异常
     */
    private void showErrorAndExit(Throwable e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resources.getString("system.error"));
            alert.setHeaderText(resources.getString("system.error"));
            alert.setContentText(resources.getString("app.title") + " " + e.getMessage());
            alert.showAndWait();
            Platform.exit();
        });
    }
    
    private void createDefaultUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        javafx.scene.control.Label label = new javafx.scene.control.Label(resources.getString("app.welcome"));
        label.setStyle("-fx-font-size: 24px;");
        root.setCenter(label);
        
        primaryStage.setTitle(resources.getString("app.title"));
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
} 