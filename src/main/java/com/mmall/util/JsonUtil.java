package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:16 2018/7/9
 * @ Description：对象序列化和反序列化
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper=new ObjectMapper();
    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认将date类型转换成timestamps类型
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //所有日期格式都统一为以下样式，即"yyyy-MM-dd HH:mm:ss"
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略在json字符串中存在，在java对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }

    public static <T> String objToString(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj :objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> String objToStringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj :objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> T stringToObj(String s,Class<T> tClass){
        if (StringUtils.isEmpty(s)||tClass==null){
            return null;
        }
        try {
            return tClass.equals(String.class)? (T) s :objectMapper.readValue(s,tClass);
        } catch (IOException e) {
            log.warn("Parse String to object error",e);
            return null;
        }
    }

    public static <T> T stringToObj(String s, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(s)||typeReference==null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class)? (T) s : (T) objectMapper.readValue(s, typeReference);
        } catch (IOException e) {
            log.warn("Parse String to object error",e);
            return null;
        }
    }

    public static <T> T stringToObj(String s, Class<?> collectionClass,Class<?>...elementClasses){

        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(s,javaType);
        } catch (IOException e) {
            log.warn("Parse String to object error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1=new User();
        u1.setUsername("vain");
        u1.setId(1);
        User u2=new User();
        u2.setUsername("damon");
        u2.setId(2);
        String u1Json=objToStringPretty(u1);
        User user=stringToObj(u1Json,User.class);
        List<User> list= Lists.newArrayList();
        list.add(u1);
        list.add(u2);
        String listJson=objToString(list);
        List<User> list1=stringToObj(listJson, new TypeReference<List<User>>() {
        });
        List<User> list2=stringToObj(listJson,List.class,User.class);
        System.out.println(list1);
    }
}
