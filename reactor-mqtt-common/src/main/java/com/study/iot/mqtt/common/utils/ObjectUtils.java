package com.study.iot.mqtt.common.utils;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

/**
 * <B>说明：描述</B>
 *
 * @author L.cm
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

@UtilityClass
public class ObjectUtils extends org.springframework.util.ObjectUtils {

    /**
     * 判断对象为null
     *
     * @param object 数组
     * @return 数组是否为空
     */
    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    /**
     * 判断对象不为null
     *
     * @param object 数组
     * @return 数组是否为空
     */
    public static boolean isNotNull(@Nullable Object object) {
        return object != null;
    }

    /**
     * 是否为 true
     *
     * @param bool boolean
     * @return boolean
     */
    public static boolean isTrue(boolean bool) {
        return bool;
    }

    /**
     * 是否为 true
     *
     * @param bool Boolean
     * @return boolean
     */
    public static boolean isTrue(@Nullable Boolean bool) {
        return Optional.ofNullable(bool).orElse(Boolean.FALSE);
    }

    /**
     * 是否为 false
     *
     * @param bool Boolean
     * @return boolean
     */
    public static boolean isFalse(boolean bool) {
        return !ObjectUtils.isTrue(bool);
    }

    /**
     * 是否为 false
     *
     * @param bool Boolean
     * @return boolean
     */
    public static boolean isFalse(@Nullable Boolean bool) {
        return !ObjectUtils.isTrue(bool);
    }

    /**
     * 判断数组不为空
     *
     * @param array 数组
     * @return 数组是否为空
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !ObjectUtils.isEmpty(array);
    }

    /**
     * 判断对象不为空
     *
     * @param obj 对象
     * @return 数组是否为空
     */
    public static boolean isNotEmpty(@Nullable Object obj) {
        return !ObjectUtils.isEmpty(obj);
    }

    /**
     * 如果为空，给默认值
     *
     * @param object       对象
     * @param defaultValue 默认值
     * @return 对象
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * 首个不为空的对象
     *
     * @param values 对象列表
     * @param <T>    类型
     * @return 返回的对象
     */
    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values != null) {
            Object[] var1 = values;
            int var2 = values.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                T val = (T) var1[var3];
                if (val != null) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * 存在不为空的对象
     *
     * @param values 对象列表
     * @return 是否存在
     */
    public static boolean anyNotNull(Object... values) {
        return firstNonNull(values) != null;
    }

    /**
     * 所有对象都不为空
     *
     * @param values 对象列表
     * @return 是否不为空
     */
    public static boolean allNotNull(Object... values) {
        if (values == null) {
            return false;
        } else {
            Object[] var1 = values;
            int var2 = values.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                Object val = var1[var3];
                if (val == null) {
                    return false;
                }
            }

            return true;
        }
    }
}
