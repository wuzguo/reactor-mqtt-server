package com.study.iot.mqtt.store.mapper;

import com.study.iot.mqtt.store.strategy.CacheStrategy;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */


public interface StoreMapper {

    /**
     * 获取通道管理器
     *
     * @return {@link ChannelManager}
     */
    ChannelManager channel();

    /**
     * 获取消息管理
     *
     * @return {@link MessageMapper}
     */
    MessageMapper message();

    /**
     * 获取topic
     *
     * @return {@link TopicMapper}
     */
    TopicMapper topic();

    /**
     * 获取 Metric
     *
     * @return {@link MetricMapper}
     */
    MetricMapper metric();

    /**
     * 获取Session
     * @return {@link SessionMapper}
     */
    SessionMapper session();

    /**
     * 初始化
     *
     * @param strategy {@link CacheStrategy}
     */
    void strategy(CacheStrategy strategy);
}
