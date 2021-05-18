package com.study.iot.mqtt.store.manager;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.service.ChannelManager;
import com.study.iot.mqtt.store.service.MessageManager;
import com.study.iot.mqtt.store.service.MetricManager;
import com.study.iot.mqtt.store.service.TopicManager;
import com.study.iot.mqtt.store.strategy.CacheStrategyContainer;
import com.study.iot.mqtt.store.strategy.CacheStrategy;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:55
 */


public class DefaultCacheManager implements CacheManager {

    private final CacheStrategyContainer container;

    private CacheStrategy strategy;

    public DefaultCacheManager(CacheStrategyContainer container) {
        this.container = container;
    }

    @Override
    public void strategy(CacheStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public ChannelManager channel() {
        return container.getStrategy(CacheGroup.CHANNEL, strategy);
    }

    @Override
    public MessageManager message() {
        return container.getStrategy(CacheGroup.MESSAGE, strategy);
    }

    @Override
    public TopicManager topic() {
        return container.getStrategy(CacheGroup.TOPIC, strategy);
    }

    @Override
    public MetricManager metric() {
        return container.getStrategy(CacheGroup.METRIC, strategy);
    }
}
