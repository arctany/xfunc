package com.arctany.aivirtualassistant.service;

import javafx.scene.web.WebView;

/**
 * 虚拟形象服务接口，管理AI助手的视觉表现
 */
public interface AvatarService {
    
    /**
     * 初始化虚拟形象
     *
     * @param webView 用于显示虚拟形象的WebView
     */
    void initializeAvatar(WebView webView);
    
    /**
     * 根据AI响应更新虚拟形象的表情和动作
     *
     * @param message AI响应消息
     */
    void updateAvatarExpression(String message);
    
    /**
     * 打开自定义对话框
     */
    void openCustomizeDialog();
    
    /**
     * 加载2D形象
     *
     * @param avatarPath 形象路径
     */
    void load2DAvatar(String avatarPath);
    
    /**
     * 加载3D形象
     *
     * @param avatarPath 形象路径
     */
    void load3DAvatar(String avatarPath);
    
    /**
     * 根据文本描述生成虚拟形象
     *
     * @param description 文本描述
     */
    void generateAvatarFromDescription(String description);
    
    /**
     * 从图片创建虚拟形象
     *
     * @param imagePath 图片路径
     */
    void createAvatarFromImage(String imagePath);
    
    /**
     * 设置背景图片
     *
     * @param backgroundPath 背景图片路径
     */
    void setBackgroundImage(String backgroundPath);
    
    /**
     * 设置3D背景场景
     *
     * @param sceneType 场景类型（如: "office", "nature", "space", "custom"）
     * @param customPath 自定义场景路径（当sceneType为"custom"时使用）
     */
    void setBackground3DScene(String sceneType, String customPath);
    
    /**
     * 设置背景颜色
     *
     * @param colorHex 颜色的十六进制表示（如: "#FF0000"为红色）
     */
    void setBackgroundColor(String colorHex);
    
    /**
     * 启用或禁用动态背景效果
     *
     * @param enabled 是否启用
     * @param effectType 效果类型（如: "particles", "waves", "gradient"）
     */
    void setDynamicBackground(boolean enabled, String effectType);
    
    /**
     * 根据用户的面部表情同步虚拟形象表情
     *
     * @param faceFeatures 面部特征对象
     */
    void syncWithUserFace(Object faceFeatures);
} 