package com.arctany.aivirtualassistant.controller;

import com.arctany.aivirtualassistant.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 语言控制器，用于处理语言切换
 */
@Slf4j
@Controller
@RequestMapping("/language")
public class LanguageController {

    /**
     * 切换语言
     * 
     * @param lang 语言代码，例如 zh_CN, en_US
     * @return 切换结果
     */
    @GetMapping("/change")
    @ResponseBody
    public Map<String, Object> changeLanguage(@RequestParam("lang") String lang) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            I18nUtils.setLanguage(lang);
            log.info("语言已切换为: {}", lang);
            
            result.put("success", true);
            result.put("message", "Language changed to " + lang);
        } catch (Exception e) {
            log.error("切换语言时出错: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to change language: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取当前语言
     * 
     * @return 当前语言
     */
    @GetMapping("/current")
    @ResponseBody
    public Map<String, Object> getCurrentLanguage() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String currentLang = I18nUtils.getCurrentLanguage();
            log.info("当前语言: {}", currentLang);
            
            result.put("success", true);
            result.put("language", currentLang);
        } catch (Exception e) {
            log.error("获取当前语言时出错: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to get current language: " + e.getMessage());
        }
        
        return result;
    }
} 