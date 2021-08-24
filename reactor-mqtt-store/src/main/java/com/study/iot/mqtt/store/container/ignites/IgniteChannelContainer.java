package com.study.iot.mqtt.store.container.ignites;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.properties.IgniteProperties;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.StrategyService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.cache.Cache;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import reactor.core.Disposable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@ConditionalOnBean(value = IgniteProperties.class)
@StrategyService(group = CacheGroup.CHANNEL, type = CacheEnum.IGNITE)
public class IgniteChannelContainer implements StorageContainer<Disposable> {

    @Resource
    private IgniteCache<String, Disposable> disposableCache;

    @Override
    public void add(String identity, Disposable disposable) {
        disposableCache.put(identity, disposable);
    }

    @Override
    public void remove(String identity) {
        disposableCache.remove(identity);
    }

    @Override
    public Disposable get(String key) {
        return null;
    }

    @Override
    public List<Disposable> list(String key) {
        return null;
    }

    @Override
    public List<Disposable> getAll() {
        return disposableCache.query(new ScanQuery<String, Disposable>())
            .getAll()
            .stream()
            .map(Cache.Entry::getValue)
            .collect(Collectors.toList());
    }

    @Override
    public Disposable getAndRemove(String identity) {
        return disposableCache.getAndRemove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return disposableCache.containsKey(identity);
    }
}
