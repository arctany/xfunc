package com.arctany.aivirtualassistant.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类，用于获取不同语言的消息
 */
@Component
public class I18nUtils {

    private static MessageSource messageSource;
    
    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }
    
    /**
     * 获取当前语言的消息
     * @param code 消息编码
     * @param args 替换参数
     * @return 本地化后的消息
     */
    public static String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(code, locale, args);
    }
    
    /**
     * 获取指定语言的消息
     * @param code 消息编码
     * @param locale 区域
     * @param args 替换参数
     * @return 本地化后的消息
     */
    public static String getMessage(String code, Locale locale, Object... args) {
        if (messageSource == null) {
            return code;
        }
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            return code;
        }
    }
    
    /**
     * 设置当前语言
     * @param languageCode 语言代码，例如 "zh_CN", "en_US"
     */
    public static void setLanguage(String languageCode) {
        String[] parts = languageCode.split("_");
        Locale locale;
        if (parts.length > 1) {
            locale = new Locale(parts[0], parts[1]);
        } else {
            locale = new Locale(parts[0]);
        }
        LocaleContextHolder.setLocale(locale);
    }
    
    /**
     * 获取当前语言代码
     * @return 当前语言代码，例如 "zh_CN"
     */
    public static String getCurrentLanguage() {
        return LocaleContextHolder.getLocale().toString();
    }
} 