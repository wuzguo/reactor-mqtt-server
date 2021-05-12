package com.study.iot.mqtt.cache.service.ignite;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.service.MessageManager;
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

@ConditionalOnBean(value = IgniteProperties.class)
@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.IGNITE)
public class IgniteMessageManager implements MessageManager {

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