package com.arctany.aivirtualassistant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 虚拟形象模型类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarModel {
    
    /**
     * 形象类型: "2d" 或 "3d"
     */
    private String type;
    
    /**
     * 形象资源路径
     */
    private String path;
    
    /**
     * 形象名称
     */
    private String name;
    
    /**
     * 形象描述
     */
    private String description;
    
    /**
     * 当前表情状态
     */
    private String expression;
} 