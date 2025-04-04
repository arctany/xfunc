package com.arctany.aivirtualassistant.model;

import lombok.Data;

/**
 * 面部特征模型类，表示检测到的面部特征和属性
 */
@Data
public class FaceFeatures {
    
    /**
     * 面部矩形区域 [x, y, width, height]
     */
    private int[] faceRectangle;
    
    /**
     * 眼睛关键点坐标 [[x1, y1], [x2, y2], ...]
     */
    private int[][] eyePoints;
    
    /**
     * 嘴巴关键点坐标 [[x1, y1], [x2, y2], ...]
     */
    private int[][] mouthPoints;
    
    /**
     * 面部表情情绪
     * 可选值: "neutral", "happy", "sad", "angry", "surprised", "disgusted", "fearful"
     */
    private String emotion = "neutral";
    
    /**
     * 面部朝向，欧拉角 [pitch, yaw, roll]
     */
    private double[] headPose;
    
    /**
     * 检测到的面部特征置信度 (0.0-1.0)
     */
    private double confidence = 1.0;
    
    /**
     * 嘴巴开合程度 (0.0-1.0)
     */
    private double mouthOpenness = 0.0;
    
    /**
     * 眼睛睁开程度 (0.0-1.0) [左眼, 右眼]
     */
    private double[] eyeOpenness = new double[] {1.0, 1.0};
    
    /**
     * 眨眼状态 [左眼是否眨眼, 右眼是否眨眼]
     */
    private boolean[] blinking = new boolean[] {false, false};
} 