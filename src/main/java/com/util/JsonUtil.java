package com.util;

import com.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //序列化

        //对象的所有属性都列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消默认转换timestamp
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //设置date的格式"yyyy-MM-dd HH:mm:ss";
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //反序列化

        //忽略在json存在，但是在javabean中不存在对应属性的情况
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("parse object to string error ", e);
            return null;
        }
    }

    public static <T> String obj2StringPrrety(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("parse object to string error ", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.warn("parse string to object error ", e);
            return null;
        }
    }

    /**
     *
     * @param str
     * @param typeReference 这里面的T表示返回值的类型
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            log.warn("parse string to object error ", e);
            return null;
        }
    }

    /**
     * 如果？变为T则返回值必须为T
     *
     * @param str
     * @param collectionClass 集合的类型
     * @param elementClasses  集合里面元素的类型
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("parse string to object error", e);
            return null;
        }
    }


    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("root");
        User u2 = new User();
        u2.setId(2);
        u2.setUsername("root2");
        List<User> userList = Arrays.asList(u1, u2);
        String str = obj2StringPrrety(userList);
        List<User> users = string2Obj(str, new TypeReference<List<User>>() {
        });
//        System.out.println(users);


        List<User> users1 = string2Obj(str, List.class, User.class);
        System.out.println(users1);
//        String s1 = obj2String(u1);
//        String s2 = obj2StringPrrety(u1);
//        User user = string2Obj(s1, User.class);
//        System.out.println(user);
//        System.out.println(s1);
//        System.out.println(s2);
    }
}
