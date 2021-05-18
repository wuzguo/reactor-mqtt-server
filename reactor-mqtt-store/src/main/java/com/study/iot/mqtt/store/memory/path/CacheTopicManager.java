package com.study.iot.mqtt.store.memory.path;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

public class CacheTopicManager {

    private final TopicMap<String, SerializerDisposable> mapPath = new TopicMap<>();

    private final LoadingCache<String, Optional<List<SerializerDisposable>>> mapDisposableCache =
        // 设置并发级别为8，并发级别是指可以同时写缓存的线程数
        CacheBuilder.newBuilder()
            // 设置缓存容器的初始容量为10
            .concurrencyLevel(8)
            // 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .initialCapacity(10)
            // 是否需要统计缓存情况,该操作消耗一定的性能,生产环境应该去除
            .maximumSize(100)
            // 设置写缓存后n秒钟过期
            .recordStats()
            // 设置读写缓存后n秒钟过期,实际很少用到,类似于expireAfterWrite
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Optional<List<SerializerDisposable>>>() {
                @Override
                public Optional<List<SerializerDisposable>> load(@NotNull String key) throws Exception {
                    String[] methodArray = key.split("/");
                    return Optional.ofNullable(mapPath.getData(methodArray));
                }
            });


    public Optional<List<SerializerDisposable>> getConnections(String topic) {
        try {
            return mapDisposableCache.getUnchecked(topic);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void addConnection(String topic, SerializerDisposable connection) {
        String[] methodArray = topic.split("/");
        mapPath.putData(methodArray, connection);
        mapDisposableCache.invalidate(topic);
    }

    public void deleteConnection(String topic, SerializerDisposable connection) {
        String[] methodArray = topic.split("/");
        mapPath.delete(methodArray, connection);
        mapDisposableCache.invalidate(topic);
    }
}
