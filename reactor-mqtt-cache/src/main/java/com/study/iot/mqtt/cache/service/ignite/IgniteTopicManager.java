package com.study.iot.mqtt.cache.service.ignite;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:19
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.IGNITE)
public class IgniteTopicManager implements TopicManager {

    @Resource
    private IgniteCache<String, List<DisposableConnection>> topicDisposableCache;

    @Override
    public List<DisposableConnection> getConnections(String topic) {
        return topicDisposableCache.get(topic);
    }

    @Override
    public void addConnection(String topic, DisposableConnection connection) {
        List<DisposableConnection> connections = Optional.ofNullable(topicDisposableCache.get(topic))
            .orElse(Lists.newArrayList());
        connections.add(connection);
        topicDisposableCache.put(topic, connections);
    }

    @Override
    public void deleteConnection(String topic, DisposableConnection connection) {
        Optional.ofNullable(topicDisposableCache.get(topic)).ifPresent(connections -> connections.remove(connection));
    }
}
