package com.study.iot.mqtt.store.memory;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.store.manager.TopicManager;
import com.study.iot.mqtt.store.memory.path.CacheTopicManager;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import java.util.List;

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
    public List<SerializerDisposable> getConnections(String topic) {
        return topicManager.getConnections(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void add(String topic, SerializerDisposable disposable) {
        topicManager.addConnection(topic, disposable);
    }

    @Override
    public void remove(String topic, SerializerDisposable disposable) {
        topicManager.deleteConnection(topic, disposable);
    }
}
