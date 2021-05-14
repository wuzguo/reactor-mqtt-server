package com.study.iot.mqtt.cache.service.ignite;

import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import reactor.core.Disposable;

import javax.annotation.Resource;
import javax.cache.Cache;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@ConditionalOnBean(value = IgniteProperties.class)
@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.IGNITE)
public class IgniteChannelManager implements ChannelManager {

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
    public Disposable getAndRemove(String identity) {
        return disposableCache.getAndRemove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return disposableCache.containsKey(identity);
    }

    @Override
    public List<Disposable> getConnections() {
        return disposableCache.query(new ScanQuery<String, Disposable>())
                .getAll()
                .stream()
                .map(Cache.Entry::getValue)
                .collect(Collectors.toList());
    }
}
