package com.study.iot.mqtt.cache.service.memory;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.service.path.CacheTopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import java.util.List;
import reactor.core.Disposable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.MEMORY)
public class MemoryTopicManager implements TopicManager {

    private final CacheTopicManager topicManager = new CacheTopicManager();

    @Override
    public List<Disposable> getConnections(String topic) {
        return topicManager.getConnections(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void add(String topic, Disposable disposable) {
        topicManager.addConnection(topic, disposable);
    }

    @Override
    public void remove(String topic, Disposable disposable) {
        topicManager.deleteConnection(topic, disposable);
    }
}
