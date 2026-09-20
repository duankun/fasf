package org.fasf.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.TimeZone;

public class JSON {

    // Jackson 3 中 ObjectMapper 是不可变的，必须使用 final static 并通过 Builder 构建
    private static final JsonMapper objectMapper;

    static {
        objectMapper = JsonMapper.builder()
                // 所有字段/getter 可见（对应旧的 setVisibility(ALL, ANY)）
                .changeDefaultVisibility(vc -> vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
                // 忽略 null 字段（对应旧的 setSerializationInclusion(NON_NULL)）
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                //设置时区
                .defaultTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                .build();
    }

    /**
     * 将 JSON 字符串解析为 JsonNode
     */
    public static JsonNode readTree(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            // Jackson 3 中 readTree 依然可用，异常体系变为 JacksonException (RuntimeException)
            return objectMapper.readTree(json);
        } catch (Exception e) {
            // Jackson 3 异常多为非受检异常，但为了统一封装，这里捕获 Exception 或特定的 JacksonException
            throw new RuntimeException("JSON parse error", e);
        }
    }

    /**
     * 将对象序列化为 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize error", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定 Class 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(json);
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为复杂泛型对象 (使用 TypeReference)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为复杂泛型对象 (使用 JavaType)
     */
    public static <T> T fromJson(String json, JavaType javaType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        if (String.class.isAssignableFrom(javaType.getRawClass())) {
            //noinspection unchecked
            return (T) json;
        }
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error", e);
        }
    }

    /**
     * 构造泛型类型
     */
    public static JavaType constructType(Class<?> clazz, Class<?>... params) {
        // getTypeFactory() 在 Jackson 3 中依然可用，返回不可变的 TypeFactory
        return objectMapper.getTypeFactory().constructParametricType(clazz, params);
    }

    /**
     * 获取底层 Mapper 实例 (注意：返回的是不可变实例，无法再修改配置)
     */
    public static JsonMapper getObjectMapper() {
        return objectMapper;
    }
}
