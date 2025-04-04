package com.arctany.aivirtualassistant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户设置模型类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    
    /**
     * 当前选择的虚拟形象
     */
    private AvatarModel currentAvatar;
    
    /**
     * 当前选择的声音
     */
    private String currentVoice;
    
    /**
     * AI助手个性设置
     */
    private String aiPersonality;
    
    /**
     * 是否启用摄像头
     */
    private boolean cameraEnabled;
    
    /**
     * 是否启用麦克风
     */
    private boolean microphoneEnabled;
    
    /**
     * 是否启用自动语音回应
     */
    private boolean autoVoiceResponse;
} 