package com.arctany.aivirtualassistant.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 知识库模型类，表示AI助手的知识库
 */
@Data
public class KnowledgeBase {
    
    /**
     * 知识库名称
     */
    private String name;
    
    /**
     * 知识库描述
     */
    private String description;
    
    /**
     * 知识库在文件系统中的路径
     */
    private String path;
    
    /**
     * 知识库中的文件列表
     */
    private List<String> files = new ArrayList<>();
} 