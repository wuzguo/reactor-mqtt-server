package com.study.iot.mqtt.cache.ignite;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import org.apache.ignite.IgniteCache;

import javax.annotation.Resource;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.IGNITE)
public class IgniteChannelManager implements ChannelManager {

    @Resource
    private IgniteCache<String, DisposableConnection> connectionCache;

    @Override
    public List<DisposableConnection> getConnections() {
        return null;
    }

    @Override
    public void addConnections(DisposableConnection connection) {

    }

    @Override
    public void removeConnection(DisposableConnection connection) {

    }

    @Override
    public void add(String identity, DisposableConnection connection) {
        connectionCache.put(identity, connection);
    }

    @Override
    public void removeChannel(String identity) {
        connectionCache.remove(identity);
    }

    @Override
    public DisposableConnection getAndRemove(String identity) {
        return connectionCache.getAndRemove(identity);
    }

    @Override
    public Boolean check(String identity) {
        return connectionCache.containsKey(identity);
    }
}
