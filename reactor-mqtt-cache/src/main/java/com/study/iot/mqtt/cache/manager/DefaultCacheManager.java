package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.constant.CacheGroup;
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

    private CacheStrategyContainer container;

    public DefaultCacheManager(CacheStrategyContainer container) {
        this.container = container;
    }

    @Override
    public void init(CacheStrategy strategy) {
        this.channelManager = container.getStrategy(CacheGroup.CHANNEL, strategy);
        this.topicManager = container.getStrategy(CacheGroup.TOPIC, strategy);
        this.messageHandler = container.getStrategy(CacheGroup.MESSAGE, strategy);
    }

    private ChannelManager channelManager;

    private MessageHandler messageHandler;

    private TopicManager topicManager;

    @Override
    public ChannelManager channel() {
        return channelManager;
    }

    @Override
    public MessageHandler message() {
        return messageHandler;
    }

    @Override
    public TopicManager topic() {
        return topicManager;
    }
}
