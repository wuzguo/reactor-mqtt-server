package com.study.iot.mqtt.store.mapper;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.strategy.CacheStrategyContainer;
import com.study.iot.mqtt.store.strategy.CacheStrategy;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:55
 */


public class DefaultStoreMapper implements StoreMapper {

    private final CacheStrategyContainer container;

    private CacheStrategy strategy;

    public DefaultStoreMapper(CacheStrategyContainer container) {
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
    public MessageMapper message() {
        return container.getStrategy(CacheGroup.MESSAGE, strategy);
    }

    @Override
    public TopicMapper topic() {
        return container.getStrategy(CacheGroup.TOPIC, strategy);
    }

    @Override
    public MetricMapper metric() {
        return container.getStrategy(CacheGroup.METRIC, strategy);
    }

    @Override
    public SessionMapper session() {
        return container.getStrategy(CacheGroup.SESSION, strategy);
    }
}
