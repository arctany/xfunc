package com.arctany.aivirtualassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AIVirtualAssistantApplication {

    public static void main(String[] args) {
        // 启动Spring应用
        ConfigurableApplicationContext context = SpringApplication.run(AIVirtualAssistantApplication.class, args);
        // 启动JavaFX界面
        JavaFXApplication.launchApp(context, args);
    }
} 