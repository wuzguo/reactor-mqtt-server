package com.study.iot.mqtt.cache.ignite;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.TransportConnection;
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
    private IgniteCache<String, TransportConnection> connectionCache;

    @Override
    public List<TransportConnection> getConnections() {
        return null;
    }

    @Override
    public void addConnections(TransportConnection connection) {

    }

    @Override
    public void removeConnection(TransportConnection connection) {

    }

    @Override
    public void add(String deviceId, TransportConnection connection) {
        connectionCache.put(deviceId, connection);
    }

    @Override
    public void removeChannel(String deviceId) {
        connectionCache.remove(deviceId);
    }

    @Override
    public TransportConnection getAndRemove(String deviceId) {
        return connectionCache.getAndRemove(deviceId);
    }

    @Override
    public Boolean check(String deviceId) {
        return connectionCache.containsKey(deviceId);
    }
}
