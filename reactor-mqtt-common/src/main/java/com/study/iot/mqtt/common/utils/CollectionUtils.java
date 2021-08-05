package com.study.iot.mqtt.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

@UtilityClass
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Return {@code true} if the supplied Collection is not {@code null} or empty. Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is not empty
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    /**
     * Return {@code true} if the supplied Map is not {@code null} or empty. Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is not empty
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !CollectionUtils.isEmpty(map);
    }

    /**
     * Check whether the given Array contains the given element.
     *
     * @param array   the Array to check
     * @param element the element to look for
     * @param <T>     The generic tag
     * @return {@code true} if found, {@code false} else
     */
    public static <T> boolean contains(@Nullable T[] array, final T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtils.nullSafeEquals(x, element));
    }

    /**
     * Concatenates 2 arrays
     *
     * @param one   数组1
     * @param other 数组2
     * @return 新数组
     */
    public static String[] concat(String[] one, String[] other) {
        return concat(one, other, String.class);
    }

    /**
     * Concatenates 2 arrays
     *
     * @param one   数组1
     * @param other 数组2
     * @param clazz 数组类
     * @return 新数组
     */
    public static <T> T[] concat(T[] one, T[] other, Class<T> clazz) {
        T[] target = (T[]) Array.newInstance(clazz, one.length + other.length);
        System.arraycopy(one, 0, target, 0, one.length);
        System.arraycopy(other, 0, target, one.length, other.length);
        return target;
    }

    /**
     * 不可变 Set
     *
     * @param es  对象
     * @param <E> 泛型
     * @return 集合
     */
    @SafeVarargs
    public static <E> Set<E> ofImmutableSet(E... es) {
        Objects.requireNonNull(es, "args es is null.");
        return Arrays.stream(es).collect(Collectors.toSet());
    }

    /**
     * 不可变 List
     *
     * @param es  对象
     * @param <E> 泛型
     * @return 集合
     */
    @SafeVarargs
    public static <E> List<E> ofImmutableList(E... es) {
        Objects.requireNonNull(es, "args es is null.");
        return Arrays.stream(es).collect(Collectors.toList());
    }

    public static <T> List<List<T>> split(Collection<T> values, int size) {
        if (isEmpty(values)) {
            return new ArrayList<>(0);
        }

        List<List<T>> result = new ArrayList<>(values.size() / size + 1);
        List<T> tmp = new ArrayList<>(size);
        for (T value : values) {
            tmp.add(value);
            if (tmp.size() >= size) {
                result.add(tmp);
                tmp = new ArrayList<>(size);
            }
        }
        if (!tmp.isEmpty()) {
            result.add(tmp);
        }
        return result;
    }

    public static <T> List<Set<T>> splitToSet(Collection<T> values, int size) {
        if (isEmpty(values)) {
            return new ArrayList<>(0);
        }

        List<Set<T>> result = new ArrayList<>(values.size() / size + 1);
        Set<T> tmp = new HashSet<>(size);
        for (T value : new LinkedHashSet<>(values)) {
            tmp.add(value);
            if (tmp.size() >= size) {
                result.add(tmp);
                tmp = new HashSet<>(size);
            }
        }
        if (!tmp.isEmpty()) {
            result.add(tmp);
        }
        return result;
    }

    /**
     * 返回长度
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> int size(Collection<T> collection) {
        if (collection == null) {
            return -1;
        }
        return collection.size();
    }

    /**
     * 返回长度
     *
     * @param arrays
     * @param <T>
     * @return
     */
    public static <T> int size(T[] arrays) {
        if (arrays == null) {
            return -1;
        }
        return arrays.length;
    }

    /**
     * 注意集合不能为空
     */
    public static <T> T[] toArray(Collection<T> collection) {
        Class<?> elClass = null;
        for (T el : collection) {
            if (el != null) {
                elClass = el.getClass();
                break;
            }
        }
        if (elClass == null) {
            throw new IllegalArgumentException("collection=" + collection);
        }
        return toArray(collection, elClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<?> elClass) {
        return collection.toArray((T[]) Array.newInstance(elClass, collection.size()));
    }


    public static String[] toStringArrayDistinct(Collection<?> elements) {
        return toStrings(elements, new HashSet<>());
    }

    /**
     * @return 集合中的元素转String
     */
    public static <T extends Collection<String>> String[] toStrings(Collection<?> elements, T collect) {
        for (Object element : elements) {
            collect.add(element == null ? null : element.toString());
        }
        return collect.toArray(new String[0]);
    }
}
