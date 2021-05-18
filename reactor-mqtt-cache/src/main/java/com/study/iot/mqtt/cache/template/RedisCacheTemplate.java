package com.study.iot.mqtt.cache.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.utils.CollectionUtil;
import com.study.iot.mqtt.common.utils.JsonUtil;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import com.study.iot.mqtt.common.utils.StringUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <B>说明：</B><BR>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2019/11/6 11:45
 */

@Component
public class RedisCacheTemplate {

    @Autowired
    private CacheOpsTemplate cacheOpsTemplate;


    /**
     * 获取单个对象的缓存
     *
     * @param cacheKey 缓存的Key
     * @param clazz    {@link Class}
     * @param <T>      对象
     * @return {@link T} 返回值
     */
    public <T> T find(String cacheKey, Class<T> clazz) {
        return cacheOpsTemplate.get(CacheConstant.ROOT, cacheKey, clazz);
    }

    /**
     * 获取单个对象的缓存
     *
     * @param cacheKey 缓存的Key
     * @param clazz    {@link Class}
     * @param loader   回调
     * @param <T>      对象
     * @return {@link T} 返回值
     */
    public <T> T find(String cacheKey, Class<T> clazz, Supplier<T> loader) {
        return cacheOpsTemplate.get(CacheConstant.ROOT, cacheKey, clazz, loader);
    }

    /**
     * 获取列表对象的缓存
     *
     * @param cacheKey 缓存的Key
     * @param clazz    {@link Class}
     * @param <T>      对象
     * @return {@link T} 返回值
     */
    public <T> List<T> list(String cacheKey, Class<T> clazz) {
        return cacheOpsTemplate.getList(CacheConstant.ROOT, cacheKey, clazz);
    }

    /**
     * 获取列表对象的缓存
     *
     * @param cacheKey 缓存的Key
     * @param clazz    {@link Class}
     * @param loader   回调
     * @param <T>      对象
     * @return {@link T} 返回值
     */
    public <T> List<T> list(String cacheKey, Class<T> clazz, Supplier<List<T>> loader) {
        // 获取Key
        String fullKey = cacheOpsTemplate.getFullKey(CacheConstant.ROOT, cacheKey);
        List<T> value = cacheOpsTemplate.getList(fullKey, clazz);
        if (CollectionUtil.isEmpty(value)) {
            value = loader.get();
            if (CollectionUtil.isNotEmpty(value)) {
                cacheOpsTemplate.setnx(fullKey, JsonUtil.toString(value));
                // 30 分钟失效
                cacheOpsTemplate.expire(fullKey, 60L * 30);
            }
        }
        return value;
    }

    /**
     * 删除所有Key的缓存
     *
     * @param cacheKeys 缓存的键
     */
    public void remove(String... cacheKeys) {
        cacheOpsTemplate.del0(CacheConstant.ROOT, cacheKeys);
    }

    /**
     * 删除所有Key的缓存
     *
     * @param cacheKeys 缓存键集合
     */
    public void remove(Collection<String> cacheKeys) {
        cacheOpsTemplate.del0(CacheConstant.ROOT, cacheKeys);
    }

    /**
     * 删除Hash中的健值对
     *
     * @param key   缓存键
     * @param field 列值
     */
    public void hdel(String key, String field) {
        cacheOpsTemplate.hdel(CacheConstant.ROOT.concat(":").concat(key), field);
    }

    /**
     * 删除Hash中的健值对
     *
     * @param key    缓存键
     * @param fields 列值
     */
    public void hdel(String key, Set<String> fields) {
        cacheOpsTemplate.hdel0(CacheConstant.ROOT.concat(":").concat(key),
            fields.stream().toArray(Object[]::new));
    }

    /**
     * 将哈希表 key 中的字段 field 的值设为 value 。
     *
     * @param key   KEY
     * @param field 列
     * @param obj   对象
     */
    public void hset(String key, String field, Object obj) {
        cacheOpsTemplate.hset(CacheConstant.ROOT.concat(":").concat(key), field, obj);
    }

    /**
     * 获取存储在哈希表中指定字段的值,并转换成指定的Bean。
     *
     * @param key   KEY
     * @param field 列
     * @param clazz 对象
     * @param <T>   CLASS
     * @return {@link T}
     */
    public <T> T hget(String key, String field, Class<T> clazz) {
        return cacheOpsTemplate.hget(CacheConstant.ROOT.concat(":").concat(key), field, clazz);
    }

    /**
     * 获取所有给定字段的值,将item转成指定的Bean
     *
     * @param key    KEY
     * @param fields 列组
     * @param clazz  对象
     * @param <T>    CLASS
     * @return {@link T}
     */
    public <T> Map<String, T> hmget(String key, Class<T> clazz, String... fields) {
        return cacheOpsTemplate.hmget(CacheConstant.ROOT.concat(":").concat(key), clazz, fields);
    }


    /**
     * 将 key 中储存的数字值增一。(原子操作，适合分布式计数器业务) 如果value不存在，则设置为0,在自增 如果value不是数字类型，会报错
     */
    public long hIncrBy(String key, String field, long delta) {
        return cacheOpsTemplate.opsForHash().increment(CacheConstant.ROOT.concat(":").concat(key), field, delta);
    }

    /**
     * 递增
     *
     * @param key   key
     * @param field 字段
     */
    public long hIncr(String key, String field) {
        return hIncrBy(key, field, 1L);
    }

    /**
     * 减1
     *
     * @param key   KEY
     * @param field 字段
     * @return {@link Long}
     */
    public long hDecr(String key, String field) {
        return hDecrBy(key, field, 1L);
    }


    /**
     * 减去固定数目
     *
     * @param key   KEY
     * @param field 字段
     * @param delta 数字
     * @return {@link Long}
     */
    public long hDecrBy(String key, String field, long delta) {
        return cacheOpsTemplate.opsForHash().increment(CacheConstant.ROOT.concat(":").concat(key), field, -delta);
    }


    /**
     * 获取所有给定字段的值,将item转成指定的Bean
     *
     * @param key   KEY
     * @param clazz 对象
     * @param <T>   CLASS
     * @return {@link T}
     */
    public <T> Map<String, T> hmget(String key, Class<T> clazz) {
        String cacheKey = CacheConstant.ROOT.concat(":").concat(key);
        Set<String> keys = cacheOpsTemplate.opsForHash().keys(cacheKey);
        return cacheOpsTemplate.hmget(cacheKey, clazz, keys.toArray(new String[keys.size()]));
    }

    /**
     * 通过健值对获取Hash的值
     *
     * @param key    Hash Key
     * @param fields 字段对象
     * @param clazz  Bean Class
     * @param loader 回调函数
     * @param <T>    Bean 对象
     * @return {@link List} Bean 对象
     */
    public <T> Map<String, List<T>> hmlist(Long key, Set<Long> fields, Class<T> clazz,
        Supplier<Map<String, List<T>>> loader, Function<String, List<T>> function) {
        return this.hmlist(CacheConstant.ROOT.concat(":").concat(String.valueOf(key)), clazz, loader,
            function, fields.stream().map(String::valueOf).collect(Collectors.toList()));
    }


    /**
     * 通过健值对获取Hash的值
     *
     * @param key    Hash Key
     * @param fields 字段对象
     * @param clazz  Bean Class
     * @param loader 回调函数
     * @param <T>    Bean 对象
     * @return {@link List} Bean 对象
     */
    public <T> List<T> hmget(Long key, Set<Long> fields, Class<T> clazz,
        Supplier<Map<String, T>> loader, Function<String, T> function) {
        return this.hmget(CacheConstant.ROOT.concat(":").concat(String.valueOf(key)), clazz, loader,
            function, fields.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    /**
     * 通过健值对获取Hash的值
     *
     * @param key    KEY
     * @param clazz  类型
     * @param loader 回调函数
     * @param fields 字段对象
     * @param <T>    Bean 对象
     * @return {@link Map} Bean 对象
     */
    private <T> Map<String, List<T>> hmlist(String key, Class<T> clazz, Supplier<Map<String, List<T>>> loader,
        Function<String, List<T>> function, List<String> fields) {
        List<String> values = cacheOpsTemplate.opsForHash().multiGet(key, fields);
        if (StringUtil.isAnyNotBlank(values)) {
            Map<String, List<T>> result = Maps.newHashMap();
            for (int i = 0; i < fields.size(); ++i) {
                String value = values.get(i);
                if (StringUtil.isNotBlank(value)) {
                    result.put(fields.get(i), JsonUtil.readList(value, clazz));
                } else {
                    List<T> res = function.apply(fields.get(i));
                    result.put(fields.get(i), res);
                    cacheOpsTemplate.hmset(key, result);
                    // 24小时过期
                    cacheOpsTemplate.expire(key, 3600L * 24);
                }
            }
            return result;
        } else {
            Map<String, List<T>> result = loader.get();
            if (CollectionUtil.isNotEmpty(result)) {
                cacheOpsTemplate.hmset(key, result);
                cacheOpsTemplate.expire(key, 3600L * 24);
                return result;
            } else {
                return Collections.emptyMap();
            }
        }
    }


    /**
     * 通过健值对获取Hash的值
     *
     * @param key    KEY
     * @param clazz  类型
     * @param loader 回调函数
     * @param fields 字段对象
     * @param <T>    Bean 对象
     * @return {@link Map} Bean 对象
     */
    private <T> List<T> hmget(String key, Class<T> clazz, Supplier<Map<String, T>> loader,
        Function<String, T> function, List<String> fields) {
        List<String> values = cacheOpsTemplate.opsForHash().multiGet(key, fields);
        if (StringUtil.isAnyNotBlank(values)) {
            List<T> result = Lists.newArrayList();
            for (int i = 0; i < fields.size(); ++i) {
                String value = values.get(i);
                if (StringUtil.isNotBlank(value)) {
                    result.add(JsonUtil.readValue(value, clazz));
                } else {
                    T res = function.apply(fields.get(i));
                    if (ObjectUtil.isNotNull(res)) {
                        result.add(res);
                        cacheOpsTemplate.hset(key, fields.get(i), res);
                        // 24小时过期
                        cacheOpsTemplate.expire(key, 3600L * 24);
                    }
                }
            }
            return result;
        } else {
            Map<String, T> result = loader.get();
            if (CollectionUtil.isNotEmpty(result)) {
                cacheOpsTemplate.hmset(key, result);
                cacheOpsTemplate.expire(key, 3600L * 24);
                return Lists.newArrayList(result.values());
            } else {
                return Collections.emptyList();
            }
        }
    }
}
