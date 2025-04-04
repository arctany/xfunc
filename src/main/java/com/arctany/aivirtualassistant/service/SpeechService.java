package com.arctany.aivirtualassistant.service;

/**
 * 语音服务接口，管理AI助手的语音输入输出
 */
public interface SpeechService {
    
    /**
     * 初始化语音服务
     */
    void initialize();
    
    /**
     * 让AI助手说出文本
     *
     * @param text 要说出的文本
     */
    void speak(String text);
    
    /**
     * 开始语音识别
     */
    void startListening();
    
    /**
     * 停止语音识别
     */
    void stopListening();
    
    /**
     * 添加语音识别结果监听器
     *
     * @param listener 语音识别结果监听器
     */
    void addRecognitionListener(SpeechRecognitionListener listener);
    
    /**
     * 设置语音合成声音
     *
     * @param voiceName 声音名称
     */
    void setVoice(String voiceName);
    
    /**
     * 从音频样本自定义声音
     *
     * @param samplePath 音频样本路径
     */
    void customizeVoiceFromSample(String samplePath);
    
    /**
     * 语音识别结果监听器接口
     */
    interface SpeechRecognitionListener {
        /**
         * 当识别到语音时调用
         *
         * @param text 识别出的文本
         */
        void onSpeechRecognized(String text);
    }
} 