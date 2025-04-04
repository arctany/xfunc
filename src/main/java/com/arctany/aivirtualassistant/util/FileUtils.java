package com.arctany.aivirtualassistant.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtils {
    
    /**
     * 确保目录存在，如不存在则创建
     *
     * @param dirPath 目录路径
     * @return 是否成功创建或已存在
     */
    public static boolean ensureDirectoryExists(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("已创建目录: {}", dirPath);
            }
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}", dirPath, e);
            return false;
        }
    }
    
    /**
     * 复制文件
     *
     * @param source 源文件路径
     * @param target 目标文件路径
     * @return 是否复制成功
     */
    public static boolean copyFile(String source, String target) {
        try {
            Path sourcePath = Paths.get(source);
            Path targetPath = Paths.get(target);
            
            // 确保目标目录存在
            ensureDirectoryExists(targetPath.getParent().toString());
            
            // 复制文件
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("已复制文件: {} -> {}", source, target);
            return true;
        } catch (IOException e) {
            log.error("复制文件失败: {} -> {}", source, target, e);
            return false;
        }
    }
    
    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（不包含点号）
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
    
    /**
     * 获取不带扩展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        
        return fileName;
    }
} 