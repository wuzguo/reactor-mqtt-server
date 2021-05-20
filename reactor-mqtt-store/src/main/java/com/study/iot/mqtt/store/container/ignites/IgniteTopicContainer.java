package com.study.iot.mqtt.store.container.ignites;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.store.properties.IgniteProperties;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:19
 */

@ConditionalOnBean(value = IgniteProperties.class)
@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.IGNITE)
public class IgniteTopicContainer implements TopicContainer {

    @Resource
    private IgniteCache<String, List<SerializerDisposable>> topicDisposableCache;

    @Override
    public List<SerializerDisposable> getConnections(String topic) {
        return topicDisposableCache.get(topic);
    }

    @Override
    public void add(String topic, SerializerDisposable disposable) {
        List<SerializerDisposable> disposables = Optional.ofNullable(topicDisposableCache.get(topic))
            .orElse(Lists.newArrayList());
        disposables.add(disposable);
        topicDisposableCache.put(topic, disposables);
    }

    @Override
    public void remove(String topic, SerializerDisposable disposable) {
        Optional.ofNullable(topicDisposableCache.get(topic)).ifPresent(disposables -> {
            disposables.remove(disposable);
            topicDisposableCache.put(topic, disposables);
        });
    }
}
