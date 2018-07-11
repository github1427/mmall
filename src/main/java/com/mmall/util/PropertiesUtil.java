package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:43 2018/6/20
 * @ Description：读取配置文件的工具类
 */
@Slf4j
public class PropertiesUtil {
    private static Properties properties;
    static {
        String fileName="mmall.properties";
        properties=new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        String value=properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        String value=properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            value=defaultValue;
        }
        return value.trim();
    }

}
