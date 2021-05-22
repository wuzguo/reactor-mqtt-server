package com.study.iot.mqtt.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

@UtilityClass
public class JsonUtil {

    public static final ObjectMapper MAPPER = newObjectMapper();

    public static ObjectMapper init(ObjectMapper mapper) {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.setSerializationInclusion(Include.NON_NULL);
        // 不知道的属性，不异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_TIME_ZONE));
        mapper.setTimeZone(DateUtil.TIME_ZONE);

        SimpleModule simpleModule = new SimpleModule("LongToStringModule");
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateUtil.DATE_TIME_FORMATTER));
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateUtil.DATE_TIME_FORMATTER));
        mapper.registerModule(timeModule);

        mapper.findAndRegisterModules();
        return mapper;
    }

    private static ObjectMapper newObjectMapper() {
        return init(new ObjectMapper());
    }

    public static String writerWithDefaultPrettyPrinter(Object value) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void writeValue(Writer writer, Object value) {
        try {
            MAPPER.writeValue(writer, value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] writerWithDefaultPrettyPrinterAsBytes(Object value) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] writeValueAsBytes(Object value) {
        try {
            return MAPPER.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将对象序列化成json字符串
     *
     * @param object javaBean
     * @return jsonString json字符串
     */
    @Nullable
    public static String toJson(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return writeValueAsString(object);
    }

    public static String toString(Object value) {
        return writeValueAsString(value);
    }

    public static String writeValueAsString(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 对象转换为map，如果是字符串，先转成json对象再转为map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return toMap((String) value);
        }
        return MAPPER.convertValue(value, Map.class);
    }

    /**
     * 返回结果不为空
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String value) {
        if (StringUtil.isEmpty(value)) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(value, Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(value, e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param content json 流（特殊处理：输入null返回null）
     * @return
     * @since 0.1.0
     */
    public static JsonNode readTree(InputStream content) {
        if (content == null) {
            return null;
        }
        try {
            return MAPPER.readTree(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("Json inputStream error", e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param content json 流（特殊处理：输入null返回null）
     * @param type    目标类型
     * @return
     * @since 0.1.0
     */
    public static <T> T readValue(InputStream content, Class<T> type) {
        if (content == null) {
            return null;
        }
        try {
            return MAPPER.readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Json inputStream error", e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param data json 流（特殊处理：输入null返回null）
     * @param type 目标类型
     * @return
     * @since 0.1.0
     */
    public static <T> T readValue(byte[] data, Class<T> type) {
        if (data == null) {
            return null;
        }
        try {
            return MAPPER.readValue(data, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Json byte[] data error", e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param content       json 流（特殊处理：输入null返回null）
     * @param typeReference 泛型
     * @return
     * @since 0.1.0
     */
    public static <T> T readValue(InputStream content, TypeReference<T> typeReference) {
        if (content == null) {
            return null;
        }
        try {
            return MAPPER.readValue(content, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException("Json inputStream error", e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param content json字符串（特殊处理：输入null返回null）
     * @param type    目标类型
     * @return
     * @since 0.1.0
     */
    public static <T> T readValue(String content, Class<T> type) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(content, e);
        }
    }

    /**
     * 读取为指定类型
     *
     * @param content json字符串（特殊处理：输入null返回null）
     * @param type    目标类型
     * @return
     * @since 0.1.0
     */
    public static <T> T readValue(String content, JavaType type) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(content, e);
        }
    }

    /**
     * 范型readValue json ==> Pager&lt;MyBean&gt;: readValue(json, Pager.class, MyBean.class)<br> json ==>
     * List<Set<Integer>>: readValue(json, List.class, Integer.class)<br>
     */
    public static <T> T readValue(String json, Class<?> parametrized, Class<?> parametersFor,
        Class<?>... parameterClasses) {
        if (StringUtil.isBlank(json)) {
            return null;
        }

        JavaType type;
        if (parameterClasses == null || parameterClasses.length == 0) {
            type = MAPPER.getTypeFactory().constructParametrizedType(parametrized, parametrized, parametersFor);
        } else {
            type = MAPPER.getTypeFactory().constructParametrizedType(parametrized, parametersFor, parameterClasses);
        }

        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <K, V> Map<K, V> readMap(InputStream content, Class<?> keyClass, Class<?> valueClass) {
        if (content == null) {
            return null;
        }
        try {
            return MAPPER.readValue(content, MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T> T readMap(String content, Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, MAPPER.getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readList(InputStream content, Class<T> elementClass) {
        if (content == null) {
            return null;
        }
        try {
            return MAPPER
                .readValue(content, MAPPER.getTypeFactory().constructCollectionLikeType(List.class, elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readList(String content, Class<?> collectionClass, Class<T> elementClass) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, MAPPER.getTypeFactory()
                .constructCollectionLikeType(collectionClass == null ? List.class : collectionClass, elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readList(String content, Class<T> elementClass) {
        return readList(content, null, elementClass);
    }

    /**
     * 转换为目标类，如果value是字符串，将被认为是json串<br> 所以特别注意：'"abc"'是json字符串，目标类型是String时，转换结果为'abc'而不是'"abc"'<br>
     *
     * @param value
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(Object value, Class<T> clazz) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof String) {
                if (!String.class.equals(clazz) && ((String) value).isEmpty()) {
                    return null;
                }
                if (String.class.equals(clazz) && ((String) value).isEmpty()) {
                    return (T) value;
                }
                value = MAPPER.readTree((String) value);
            }
            return MAPPER.convertValue(value, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    public static TypeFactory getTypeFactory() {
        return MAPPER.getTypeFactory();
    }

    public static MapType constructMapType(Class<?> keyClass, Class<?> valueClass) {
        return getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    }


    public static <T> T readUpdate(T valueToUpdate, JsonNode src) {
        ObjectReader reader = MAPPER.readerForUpdating(valueToUpdate);
        try {
            return reader.readValue(src);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int asInt(JsonNode node, int defaultValue) {
        return node == null ? defaultValue : node.asInt(defaultValue);
    }

    public static String asText(JsonNode node, String defaultValue) {
        return node == null ? defaultValue : node.asText(defaultValue);
    }
}
