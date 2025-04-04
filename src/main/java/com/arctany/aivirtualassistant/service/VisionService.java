package com.arctany.aivirtualassistant.service;

import com.arctany.aivirtualassistant.model.FaceFeatures;
import javafx.scene.image.ImageView;

/**
 * 视觉服务接口，管理摄像头和计算机视觉功能
 */
public interface VisionService {
    
    /**
     * 初始化摄像头
     *
     * @param imageView 用于显示摄像头预览的ImageView
     */
    void initializeCamera(ImageView imageView);
    
    /**
     * 启动摄像头
     */
    void startCamera();
    
    /**
     * 停止摄像头
     */
    void stopCamera();
    
    /**
     * 拍照
     *
     * @return 图片路径
     */
    String captureImage();
    
    /**
     * 添加人脸检测监听器
     *
     * @param listener 人脸检测监听器
     */
    void addFaceDetectionListener(FaceDetectionListener listener);
    
    /**
     * 获取当前检测到的面部特征
     * 
     * @return 面部特征对象，如果未检测到人脸则为null
     */
    FaceFeatures getCurrentFaceFeatures();
    
    /**
     * 检查是否检测到人脸
     * 
     * @return 是否检测到人脸
     */
    boolean isFaceDetected();
    
    /**
     * 人脸检测监听器接口
     */
    interface FaceDetectionListener {
        /**
         * 当检测到人脸时调用
         *
         * @param facePosition 人脸位置坐标 [x, y, width, height]
         * @param faceFeatures 人脸特征数据
         */
        void onFaceDetected(int[] facePosition, Object faceFeatures);
        
        /**
         * 当人脸消失时调用
         */
        void onFaceLost();
    }
} 