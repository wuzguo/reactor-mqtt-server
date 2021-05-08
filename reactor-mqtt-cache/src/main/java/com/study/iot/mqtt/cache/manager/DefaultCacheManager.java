package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.service.MessageHandler;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyContainer;
import com.study.iot.mqtt.common.enums.CacheStrategy;

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
    public MessageHandler message() {
        return container.getStrategy(CacheGroup.MESSAGE, strategy);
    }

    @Override
    public TopicManager topic() {
        return container.getStrategy(CacheGroup.TOPIC, strategy);
    }
}
