package com.study.iot.mqtt.cache.service.ignite;

import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
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
    public Collection<Disposable> getConnections() {
        return Collections.emptyList();
    }
}
