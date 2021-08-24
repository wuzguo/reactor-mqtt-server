package com.study.iot.mqtt.store.container.memory;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.store.container.path.CacheTopicManager;
import com.study.iot.mqtt.store.strategy.StrategyService;
import com.study.iot.mqtt.common.enums.CacheEnum;
import java.util.List;
import reactor.core.Disposable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@StrategyService(group = CacheGroup.TOPIC, type = CacheEnum.MEMORY)
public class MemoryTopicContainer implements TopicContainer {

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
