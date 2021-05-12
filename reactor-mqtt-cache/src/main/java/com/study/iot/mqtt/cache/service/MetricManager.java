package com.study.iot.mqtt.cache.service;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:23
 */

public interface MetricManager {

    /**
     * 添加标识
     *
     * @param key   标识
     * @param count 数量
     */
    void add(String key, LongAdder count);

    /**
     * 移除标识
     *
     * @param key 标识
     */
    void remove(String key);

    /**
     * 获取并移除
     *
     * @param key 标识
     * @return {@link LongAdder}
     */
    LongAdder getAndRemove(String key);

    /**
     * 检查
     *
     * @param key 设备ID
     * @return {@link Boolean}
     */
    Boolean containsKey(String key);

    /**
     * 获取所有统计数据
     *
     * @return {@link Map<String,LongAdder>}
     */
    Map<String, LongAdder> loadAll();
}
