package com.study.iot.mqtt.cache.service.ignite;

import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

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
    private IgniteCache<String, DisposableConnection> disposableCache;

    @Override
    public void add(String identity, DisposableConnection connection) {
        disposableCache.put(identity, connection);
    }

    @Override
    public void removeChannel(String identity) {
        disposableCache.remove(identity);
    }

    @Override
    public DisposableConnection getAndRemove(String identity) {
        return disposableCache.getAndRemove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return disposableCache.containsKey(identity);
    }

    @Override
    public Collection<DisposableConnection> getConnections() {
        return Collections.emptyList();
    }
}
