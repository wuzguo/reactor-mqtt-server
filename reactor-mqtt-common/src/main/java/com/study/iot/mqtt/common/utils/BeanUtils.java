package com.study.iot.mqtt.common.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.beans.AbstractBeanCopier;
import com.study.iot.mqtt.common.beans.BeanCopyConverter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.lang.Nullable;

/**
 * <B>说明：实体工具类</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 8:47
 */

@UtilityClass
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 实例化对象
     *
     * @param clazz 类
     * @param <T>   泛型标记
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        return (T) instantiateClass(clazz);
    }

    /**
     * 实例化对象
     *
     * @param clazzStr 类名
     * @param <T>      泛型标记
     * @return 对象
     */
    public static <T> T newInstance(String clazzStr) {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        try {
            Class<?> clazz = ClassUtils.forName(clazzStr, classLoader);
            return newInstance(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Bean的属性, 支持 propertyName 多级 ：test.user.name
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @return 属性值
     */
    @Nullable
    public static Object getProperty(@Nullable Object bean, String propertyName) {
        if (bean == null) {
            return null;
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        return beanWrapper.getPropertyValue(propertyName);
    }

    /**
     * 设置Bean属性, 支持 propertyName 多级 ：test.user.name
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @param value        属性值
     */
    public static void setProperty(Object bean, String propertyName, Object value) {
        Objects.requireNonNull(bean, "bean Could not null");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        beanWrapper.setPropertyValue(propertyName, value);
    }

    /**
     * 深复制
     * <p>
     * 支持 map bean
     *
     * @param source 源对象
     * @param <T>    泛型标记
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T clone(@Nullable T source) {
        return source == null ? null : (T) BeanUtils.copy(source, source.getClass());
    }

    /**
     * copy 对象属性，默认不使用Convert
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param source 源对象
     * @param clazz  类名
     * @param <T>    泛型标记
     * @return T
     */
    @Nullable
    public static <T> T copy(@Nullable Object source, Class<T> clazz) {
        return source == null ? null : BeanUtils.copy(source, source.getClass(), clazz);
    }

    /**
     * copy 对象属性，默认不使用Convert
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param source      源对象
     * @param sourceClazz 源类型
     * @param targetClazz 转换成的类型
     * @param <T>         泛型标记
     * @return T
     */
    @Nullable
    public static <T> T copy(@Nullable Object source, Class<?> sourceClazz, Class<T> targetClazz) {
        if (source == null) {
            return null;
        }
        AbstractBeanCopier copier = AbstractBeanCopier.create(sourceClazz, targetClazz, false);
        T instance = newInstance(targetClazz);
        copier.copy(source, instance, null);

        return instance;
    }

    /**
     * copy 列表对象，默认不使用Convert
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param sources  源列表
     * @param targetClazz 转换成的类型
     * @param <T>         泛型标记
     * @return T
     */
    public static <T> List<T> copy(@Nullable Collection<?> sources, Class<T> targetClazz) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> outers = Lists.newArrayList();
        Class<?> sourceClazz = null;

        for (Object source : sources) {
            if (source == null) {
                continue;
            }
            if (sourceClazz == null) {
                sourceClazz = source.getClass();
            }
            T bean = BeanUtils.copy(source, sourceClazz, targetClazz);
            outers.add(bean);
        }
        return outers;
    }

    /**
     * 拷贝对象
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param source     源对象
     * @param targetBean 需要赋值的对象
     */
    public static void copy(@Nullable Object source, @Nullable Object targetBean) {
        if (source == null || targetBean == null) {
            return;
        }
        AbstractBeanCopier copier = AbstractBeanCopier
            .create(source.getClass(), targetBean.getClass(), false);

        copier.copy(source, targetBean, null);
    }

    /**
     * 拷贝对象，source 属性做 null 判断，Map 不支持，map 会做 instanceof 判断，不会
     *
     * <p>
     * 支持 bean copy
     * </p>
     *
     * @param source     源对象
     * @param targetBean 需要赋值的对象
     */
    public static void copyNonNull(@Nullable Object source, @Nullable Object targetBean) {
        if (source == null || targetBean == null) {
            return;
        }
        AbstractBeanCopier copier = AbstractBeanCopier
            .create(source.getClass(), targetBean.getClass(), false, true);

        copier.copy(source, targetBean, null);
    }

    /**
     * 拷贝对象并对不同类型属性进行转换
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param source      源对象
     * @param targetClazz 转换成的类
     * @param <T>         泛型标记
     * @return T
     */
    @Nullable
    public static <T> T copyWithConvert(@Nullable Object source, Class<T> targetClazz) {
        return source == null ? null : BeanUtils.copyWithConvert(source, source.getClass(), targetClazz);
    }

    /**
     * 拷贝对象并对不同类型属性进行转换
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param source      源对象
     * @param sourceClazz 源类
     * @param targetClazz 转换成的类
     * @param <T>         泛型标记
     * @return T
     */
    @Nullable
    public static <T> T copyWithConvert(@Nullable Object source, Class<?> sourceClazz, Class<T> targetClazz) {
        if (source == null) {
            return null;
        }
        AbstractBeanCopier copier = AbstractBeanCopier.create(sourceClazz, targetClazz, true);
        T instance = newInstance(targetClazz);
        copier.copy(source, instance, new BeanCopyConverter(sourceClazz, targetClazz));
        return instance;
    }

    /**
     * 拷贝列表并对不同类型属性进行转换
     *
     * <p>
     * 支持 map bean copy
     * </p>
     *
     * @param sources     源对象列表
     * @param targetClazz 转换成的类
     * @param <T>         泛型标记
     * @return List
     */
    public static <T> List<T> copyWithConvert(@Nullable Collection<?> sources, Class<T> targetClazz) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> outers = Lists.newArrayList();
        Class<?> sourceClazz = null;

        for (Object source : sources) {
            if (source == null) {
                continue;
            }
            if (sourceClazz == null) {
                sourceClazz = source.getClass();
            }
            T bean = BeanUtils.copyWithConvert(source, sourceClazz, targetClazz);
            outers.add(bean);
        }
        return outers;
    }

    /**
     * Copy the property values of the given source bean into the target class.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
     * bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     *
     * @param source      the source bean
     * @param targetClazz the target bean class
     * @param <T>         泛型标记
     * @return T
     * @throws BeansException if the copying failed
     */
    @Nullable
    public static <T> T copyProperties(@Nullable Object source, Class<T> targetClazz) throws BeansException {
        if (source == null) {
            return null;
        }
        T instance = newInstance(targetClazz);
        BeanUtils.copyProperties(source, instance);
        return instance;
    }

    /**
     * Copy the property values of the given source bean into the target class.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
     * bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     *
     * @param sources     the source list bean
     * @param targetClazz the target bean class
     * @param <T>         泛型标记
     * @return List
     * @throws BeansException if the copying failed
     */
    public static <T> List<T> copyProperties(@Nullable Collection<?> sources, Class<T> targetClazz)
        throws BeansException {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> outers = Lists.newArrayList();
        for (Object source : sources) {
            if (source == null) {
                continue;
            }
            T bean = BeanUtils.copyProperties(source, targetClazz);
            outers.add(bean);
        }
        return outers;
    }

    /**
     * 将对象装成map形式
     *
     * @param bean 源对象
     * @return {Map}
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(@Nullable Object bean) {
        if (bean == null) {
            return Maps.newHashMap();
        }
        return BeanMap.create(bean);
    }

    /**
     * 将map 转为 bean
     *
     * @param mapBeans  map
     * @param valueType 对象类型
     * @param <T>       泛型标记
     * @return {T}
     */
    public static <T> T toBean(Map<String, Object> mapBeans, Class<T> valueType) {
        Objects.requireNonNull(mapBeans, "beanMap Could not null");
        T instance = newInstance(valueType);
        if (mapBeans.isEmpty()) {
            return instance;
        }
        BeanUtils.copy(mapBeans, instance);
        return instance;
    }
}
