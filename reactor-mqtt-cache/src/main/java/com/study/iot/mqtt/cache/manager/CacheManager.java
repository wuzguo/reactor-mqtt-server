package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.common.enums.CacheStrategy;

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
     * @return {@link MessageHandler}
     */
    MessageHandler message();

    /**
     * 获取topic
     *
     * @return {@link TopicManager}
     */
    TopicManager topic();

    /**
     * 初始化
     *
     * @param strategy {@link CacheStrategy}
     */
    void init(CacheStrategy strategy);
}
