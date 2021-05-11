package com.study.iot.mqtt.cache.service.ignite;

import com.study.iot.mqtt.cache.config.IgniteAutoConfig;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MessageHandler;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.message.RetainMessage;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@ConditionalOnBean(value = IgniteAutoConfig.class)
@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.IGNITE)
public class IgniteMessageHandler implements MessageHandler {

    @Resource
    private IgniteCache<String, RetainMessage> messageCache;

    @Override
    public void saveRetain(RetainMessage message) {
        messageCache.put(message.getTopic(), message);
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return messageCache.get(topicName);
    }
}
