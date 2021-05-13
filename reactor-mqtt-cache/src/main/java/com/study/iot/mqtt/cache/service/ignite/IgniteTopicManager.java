package com.study.iot.mqtt.cache.service.ignite;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import org.apache.ignite.IgniteCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import reactor.core.Disposable;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:19
 */

@ConditionalOnBean(value = IgniteProperties.class)
@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.IGNITE)
public class IgniteTopicManager implements TopicManager {

    @Resource
    private IgniteCache<String, List<Disposable>> topicDisposableCache;

    @Override
    public List<Disposable> getConnections(String topic) {
        return topicDisposableCache.get(topic);
    }

    @Override
    public void add(String topic, Disposable disposable) {
        List<Disposable> disposables = Optional.ofNullable(topicDisposableCache.get(topic))
                .orElse(Lists.newArrayList());
        disposables.add(disposable);
        topicDisposableCache.put(topic, disposables);
    }

    @Override
    public void remove(String topic, Disposable disposable) {
        Optional.ofNullable(topicDisposableCache.get(topic)).ifPresent(disposables -> {
            disposables.remove(disposable);
            topicDisposableCache.put(topic, disposables);
        });
    }
}
