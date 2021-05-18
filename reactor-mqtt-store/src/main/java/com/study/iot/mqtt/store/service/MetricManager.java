package com.study.iot.mqtt.store.service;

import com.study.iot.mqtt.store.strategy.CacheCapable;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:23
 */

public interface MetricManager extends CacheCapable {

    /**
     * 新增1
     *
     * @param key 标识
     */
    Mono<Void> increase(String key);

    /**
     * 新增1
     *
     * @param key   标识
     * @param count {@link Integer}
     */
    Mono<Void> increase(String key, Long count);

    /**
     * 减少
     *
     * @param key 标识
     */
    Mono<Void> decrease(String key);

    /**
     * 减少
     *
     * @param key   标识
     * @param count {@link LongAdder}
     */
    Mono<Void> decrease(String key, Long count);

    /**
     * 移除标识
     *
     * @param key 标识
     */
    Mono<Void> remove(String key);

    /**
     * 检查
     *
     * @param key 设备ID
     * @return {@link Boolean}
     */
    Mono<Boolean> containsKey(String key);

    /**
     * 获取所有统计数据
     *
     * @return {@link Map<String,LongAdder>}
     */
    Mono<Map<String, LongAdder>> loadAll();
}
