package com.arctany.aivirtualassistant.service.impl;

import com.arctany.aivirtualassistant.service.SpeechService;
import com.arctany.aivirtualassistant.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 语音服务高质量实现类
 * 支持高质量语音合成和识别
 */
@Slf4j
@Service
public class SpeechServiceImpl implements SpeechService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<SpeechRecognitionListener> recognitionListeners = new ArrayList<>();
    private TargetDataLine microphoneLine;
    private boolean isListening = false;
    private String currentVoice = "default";
    private File voiceDirectory;
    private Map<String, VoiceProfile> voiceProfiles = new HashMap<>();
    
    @Value("${speech.sample.rate:44100}")
    private float sampleRate;
    
    @Value("${speech.quality:high}")
    private String speechQuality;

    /**
     * 语音配置文件类
     */
    private static class VoiceProfile {
        String name;
        String gender;
        String language;
        float pitch;
        float rate;
        float volume;
        String customSamplePath;
        
        VoiceProfile(String name, String gender, String language) {
            this.name = name;
            this.gender = gender;
            this.language = language;
            this.pitch = 1.0f;
            this.rate = 1.0f;
            this.volume = 1.0f;
        }
    }

    @Override
    public void initialize() {
        try {
            // 初始化语音目录
            voiceDirectory = new File("src/main/resources/voices");
            if (!voiceDirectory.exists()) {
                boolean created = voiceDirectory.mkdirs();
                if (created) {
                    log.info("成功创建语音目录: {}", voiceDirectory.getAbsolutePath());
                } else {
                    log.warn("无法创建语音目录: {}", voiceDirectory.getAbsolutePath());
                    // 尝试使用临时目录
                    voiceDirectory = new File(System.getProperty("java.io.tmpdir"), "ai_assistant_voices");
                    if (!voiceDirectory.exists()) {
                        voiceDirectory.mkdirs();
                    }
                    log.info("使用临时目录作为语音目录: {}", voiceDirectory.getAbsolutePath());
                }
            }
            
            // 初始化默认语音配置
            initializeVoiceProfiles();
            
            log.info("高质量语音服务初始化完成，使用质量级别: {}, 采样率: {}", speechQuality, sampleRate);
        } catch (Exception e) {
            log.error("初始化语音服务出错", e);
        }
    }
    
    /**
     * 初始化语音配置文件
     */
    private void initializeVoiceProfiles() {
        // 添加默认中文女声
        VoiceProfile defaultFemale = new VoiceProfile("默认女声", "female", "zh-CN");
        defaultFemale.pitch = 1.1f;
        defaultFemale.rate = 1.0f;
        voiceProfiles.put("default", defaultFemale);
        
        // 添加默认中文男声
        VoiceProfile defaultMale = new VoiceProfile("默认男声", "male", "zh-CN");
        defaultMale.pitch = 0.9f;
        defaultMale.rate = 0.95f;
        voiceProfiles.put("male", defaultMale);
        
        // 添加轻声女声
        VoiceProfile softFemale = new VoiceProfile("轻柔女声", "female", "zh-CN");
        softFemale.pitch = 1.2f;
        softFemale.rate = 0.9f;
        softFemale.volume = 0.8f;
        voiceProfiles.put("soft_female", softFemale);
        
        // 添加英文女声
        VoiceProfile englishFemale = new VoiceProfile("英文女声", "female", "en-US");
        englishFemale.pitch = 1.0f;
        englishFemale.rate = 1.0f;
        voiceProfiles.put("english_female", englishFemale);
    }

    @Override
    public void speak(String text) {
        executorService.submit(() -> {
            try {
                log.info("AI助手说: {}", text);
                
                // 使用高质量TTS合成语音
                synthesizeHighQualitySpeech(text);
            } catch (Exception e) {
                log.error("语音合成出错", e);
            }
        });
    }

    /**
     * 高质量语音合成
     * 
     * @param text 要合成的文本
     */
    private void synthesizeHighQualitySpeech(String text) {
        // 获取当前语音配置
        VoiceProfile profile = voiceProfiles.getOrDefault(currentVoice, voiceProfiles.get("default"));
        
        // 检查是否有自定义语音样本
        if (profile.customSamplePath != null) {
            log.info("使用自定义语音样本: {}", profile.customSamplePath);
            // 这里可以集成支持声音克隆的TTS API
        }
        
        // 根据操作系统选择合适的TTS引擎
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            synthesizeWithWindowsSAPI(text, profile);
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            synthesizeWithMacOSSay(text, profile);
        } else {
            log.info("当前平台不支持内置高质量TTS，尝试使用外部TTS服务");
            synthesizeWithExternalService(text, profile);
        }
    }
    
    /**
     * 使用Windows SAPI进行高质量语音合成
     * 
     * @param text 要合成的文本
     * @param profile 语音配置
     */
    private void synthesizeWithWindowsSAPI(String text, VoiceProfile profile) {
        try {
            // 创建临时音频文件路径
            Path audioPath = Paths.get(voiceDirectory.getAbsolutePath(), "temp_speech.wav");
            
            // 确保父目录存在
            Files.createDirectories(audioPath.getParent());
            
            // 创建高级VBS脚本文件，支持配置语音参数
            StringBuilder vbsScript = new StringBuilder();
            vbsScript.append("Dim sapi\n");
            vbsScript.append("Set sapi = CreateObject(\"SAPI.SpVoice\")\n");
            
            // 设置音量 (0-100)
            vbsScript.append("sapi.Volume = ").append((int)(profile.volume * 100)).append("\n");
            
            // 设置语速 (-10 to 10)
            int rate = (int)((profile.rate - 1.0) * 10);
            vbsScript.append("sapi.Rate = ").append(rate).append("\n");
            
            // 选择声音
            vbsScript.append("Dim voices\n");
            vbsScript.append("Set voices = sapi.GetVoices\n");
            
            // 根据性别和语言选择合适的声音
            if (profile.gender.equals("female")) {
                vbsScript.append("For i = 0 To voices.Count - 1\n");
                vbsScript.append("  If InStr(voices.Item(i).GetDescription, \"Female\") > 0 Then\n");
                vbsScript.append("    If InStr(voices.Item(i).GetAttribute(\"Language\"), \"" + getLanguageCode(profile.language) + "\") > 0 Then\n");
                vbsScript.append("      Set sapi.Voice = voices.Item(i)\n");
                vbsScript.append("      Exit For\n");
                vbsScript.append("    End If\n");
                vbsScript.append("  End If\n");
                vbsScript.append("Next\n");
            } else {
                vbsScript.append("For i = 0 To voices.Count - 1\n");
                vbsScript.append("  If InStr(voices.Item(i).GetDescription, \"Male\") > 0 Then\n");
                vbsScript.append("    If InStr(voices.Item(i).GetAttribute(\"Language\"), \"" + getLanguageCode(profile.language) + "\") > 0 Then\n");
                vbsScript.append("      Set sapi.Voice = voices.Item(i)\n");
                vbsScript.append("      Exit For\n");
                vbsScript.append("    End If\n");
                vbsScript.append("  End If\n");
                vbsScript.append("Next\n");
            }
            
            // 创建音频流并保存到文件
            vbsScript.append("Dim stream\n");
            vbsScript.append("Set stream = CreateObject(\"SAPI.SpFileStream\")\n");
            vbsScript.append("stream.Format.Type = 22\n"); // SAFT22kHz16BitMono
            
            if (speechQuality.equalsIgnoreCase("high")) {
                vbsScript.append("stream.Format.Type = 39\n"); // SAFT44kHz16BitStereo
            } else if (speechQuality.equalsIgnoreCase("medium")) {
                vbsScript.append("stream.Format.Type = 22\n"); // SAFT22kHz16BitMono
            }
            
            vbsScript.append("stream.Open \"").append(audioPath.toString().replace("\\", "\\\\")).append("\", 3, false\n");
            vbsScript.append("Set sapi.AudioOutputStream = stream\n");
            vbsScript.append("sapi.Speak \"").append(text.replace("\"", "\"\"")).append("\", 1\n");
            vbsScript.append("stream.Close\n");
            
            // 写入VBS脚本文件
            Path vbsPath = Paths.get("speak_high_quality.vbs");
            Files.write(vbsPath, vbsScript.toString().getBytes());
            
            // 执行VBS脚本
            ProcessBuilder pb = new ProcessBuilder("cscript", "//nologo", vbsPath.toString());
            Process process = pb.start();
            process.waitFor();
            
            // 播放生成的音频文件
            playAudioFile(audioPath.toString());
            
            // 删除临时VBS文件
            Files.delete(vbsPath);
        } catch (Exception e) {
            log.error("Windows 高质量TTS执行出错", e);
        }
    }
    
    /**
     * 使用MacOS say命令进行语音合成
     * 
     * @param text 要合成的文本
     * @param profile 语音配置
     */
    private void synthesizeWithMacOSSay(String text, VoiceProfile profile) {
        try {
            // 创建临时音频文件路径
            Path audioPath = Paths.get(voiceDirectory.getAbsolutePath(), "temp_speech.aiff");
            
            // 构建say命令
            List<String> command = new ArrayList<>();
            command.add("say");
            command.add("-o");
            command.add(audioPath.toString());
            
            // 设置语速
            command.add("-r");
            command.add(String.valueOf((int)(profile.rate * 180)));
            
            // 设置语音
            command.add("-v");
            if (profile.language.startsWith("zh")) {
                command.add(profile.gender.equals("female") ? "Ting-Ting" : "Sin-ji");
            } else {
                command.add(profile.gender.equals("female") ? "Samantha" : "Alex");
            }
            
            // 添加文本
            command.add(text);
            
            // 执行命令
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            process.waitFor();
            
            // 播放生成的音频文件
            playAudioFile(audioPath.toString());
        } catch (Exception e) {
            log.error("MacOS TTS执行出错", e);
        }
    }
    
    /**
     * 使用外部TTS服务合成语音
     * 
     * @param text 要合成的文本
     * @param profile 语音配置
     */
    private void synthesizeWithExternalService(String text, VoiceProfile profile) {
        // 这里可以集成各种云TTS服务，如百度、阿里云、Azure等
        log.info("尝试使用外部TTS服务 (未实现)");
        log.info("云TTS服务可以提供更高质量的语音合成，建议在生产环境中集成");
    }
    
    /**
     * 播放音频文件
     * 
     * @param audioFilePath 音频文件路径
     */
    private void playAudioFile(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();
            
            // 获取数据线信息
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            // 打开并启动数据线
            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(format);
                line.start();
                
                // 读取音频数据并播放
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }
                
                // 等待音频播放完成
                line.drain();
            }
            
            audioInputStream.close();
        } catch (Exception e) {
            log.error("播放音频文件出错: {}", e.getMessage());
        }
    }
    
    /**
     * 获取语言代码
     * 
     * @param language 语言标识符
     * @return 语言代码
     */
    private String getLanguageCode(String language) {
        if (language.equals("zh-CN")) {
            return "804"; // 简体中文
        } else if (language.equals("en-US")) {
            return "409"; // 美式英语
        } else {
            return "804"; // 默认简体中文
        }
    }

    @Override
    public void startListening() {
        if (isListening) {
            return;
        }
        
        executorService.submit(() -> {
            try {
                isListening = true;
                log.info("开始高质量语音识别");
                
                // 配置高质量麦克风录音
                float sampleRateHz = 44100.0f; // 高采样率
                int sampleSizeInBits = 16;     // 16位
                int channels = 1;              // 单声道
                boolean signed = true;
                boolean bigEndian = false;
                
                AudioFormat format = new AudioFormat(sampleRateHz, sampleSizeInBits, channels, signed, bigEndian);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                
                if (!AudioSystem.isLineSupported(info)) {
                    log.error("麦克风不支持指定的高质量音频格式");
                    return;
                }
                
                microphoneLine = (TargetDataLine) AudioSystem.getLine(info);
                microphoneLine.open(format);
                microphoneLine.start();
                
                // 创建临时音频文件
                File audioFile = new File(voiceDirectory.getAbsolutePath(), "recognition_input.wav");
                FileUtils.ensureDirectoryExists(voiceDirectory.getAbsolutePath());
                
                // 开始录音
                AudioInputStream audioStream = new AudioInputStream(microphoneLine);
                
                // 创建临时处理线程
                Thread recordingThread = new Thread(() -> {
                    try {
                        // 写入WAV文件
                        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
                    } catch (IOException e) {
                        if (isListening) {
                            log.error("录音过程出错", e);
                        }
                    }
                });
                
                recordingThread.start();
                
                log.info("语音识别准备就绪，请说话...");
                
            } catch (Exception e) {
                log.error("启动语音识别出错", e);
                isListening = false;
            }
        });
    }

    @Override
    public void stopListening() {
        isListening = false;
        if (microphoneLine != null && microphoneLine.isOpen()) {
            microphoneLine.stop();
            microphoneLine.close();
            
            // 识别录制的音频
            File audioFile = new File(voiceDirectory.getAbsolutePath(), "recognition_input.wav");
            if (audioFile.exists()) {
                String recognizedText = performHighQualitySpeechRecognition(audioFile);
                
                // 通知监听器
                if (recognizedText != null && !recognizedText.isEmpty()) {
                    for (SpeechRecognitionListener listener : recognitionListeners) {
                        listener.onSpeechRecognized(recognizedText);
                    }
                }
            }
        }
        log.info("停止语音识别");
    }
    
    /**
     * 执行高质量语音识别
     * 
     * @param audioFile 音频文件
     * @return 识别的文本
     */
    private String performHighQualitySpeechRecognition(File audioFile) {
        // 这里应该集成高质量语音识别引擎
        // 如Google Speech API、Azure Speech、阿里云、腾讯云等
        
        log.info("进行高质量语音识别: {}", audioFile.getAbsolutePath());
        
        // 示例: 在实际应用中，应该替换为真实的语音识别API调用
        return "这是一个高质量语音识别示例结果";
    }

    @Override
    public void addRecognitionListener(SpeechRecognitionListener listener) {
        recognitionListeners.add(listener);
    }

    @Override
    public void setVoice(String voiceName) {
        if (voiceProfiles.containsKey(voiceName)) {
            this.currentVoice = voiceName;
            log.info("设置高质量语音: {}", voiceName);
        } else {
            log.warn("未找到语音配置: {}，使用默认语音", voiceName);
            this.currentVoice = "default";
        }
    }

    @Override
    public void customizeVoiceFromSample(String samplePath) {
        try {
            log.info("从高质量样本自定义语音: {}", samplePath);
            
            // 创建自定义语音目录
            Path customVoiceDir = Paths.get(voiceDirectory.getAbsolutePath(), "custom");
            Files.createDirectories(customVoiceDir);
            
            // 复制样本文件到语音目录
            Path source = Paths.get(samplePath);
            String fileName = "custom_voice_" + System.currentTimeMillis() + "." + FileUtils.getFileExtension(samplePath);
            Path target = Paths.get(customVoiceDir.toString(), fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            // 创建自定义语音配置
            VoiceProfile customProfile = new VoiceProfile("自定义语音", "unknown", "zh-CN");
            customProfile.customSamplePath = target.toString();
            
            // 分析语音样本，提取特征
            analyzeVoiceSample(target.toString(), customProfile);
            
            // 添加到配置列表
            voiceProfiles.put("custom", customProfile);
            
            // 设置为当前语音
            setVoice("custom");
            
            log.info("高质量自定义语音设置完成");
        } catch (IOException e) {
            log.error("自定义语音出错", e);
        }
    }
    
    /**
     * 分析语音样本并提取语音特征
     * 
     * @param samplePath 样本路径
     * @param profile 语音配置
     */
    private void analyzeVoiceSample(String samplePath, VoiceProfile profile) {
        try {
            // 读取音频文件
            File audioFile = new File(samplePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();
            
            // 获取基本特征
            float sampleRate = format.getSampleRate();
            int channels = format.getChannels();
            
            log.info("分析语音样本: 采样率={}Hz, 声道数={}", sampleRate, channels);
            
            // 从音频特征推断性别
            // 这里需要实际的音频分析，简化版只是根据文件名猜测
            if (samplePath.toLowerCase().contains("female") || samplePath.toLowerCase().contains("woman")) {
                profile.gender = "female";
            } else if (samplePath.toLowerCase().contains("male") || samplePath.toLowerCase().contains("man")) {
                profile.gender = "male";
            }
            
            // 关闭输入流
            audioInputStream.close();
            
            log.info("语音样本分析完成，推断性别: {}", profile.gender);
        } catch (Exception e) {
            log.error("分析语音样本出错", e);
        }
    }
}