package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.service.MessageManager;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */


public interface CacheManager {

    /**
     * 获取通道管理器
     *
     * @return {@link ChannelManager}
     */
    ChannelManager channel();

    /**
     * 获取消息管理
     *
     * @return {@link MessageManager}
     */
    MessageManager message();

    /**
     * 获取topic
     *
     * @return {@link TopicManager}
     */
    TopicManager topic();

    /**
     * 获取 Metric
     *
     * @return {@link MetricManager}
     */
    MetricManager metric();

    /**
     * 初始化
     *
     * @param strategy {@link CacheStrategy}
     */
    void strategy(CacheStrategy strategy);
}
