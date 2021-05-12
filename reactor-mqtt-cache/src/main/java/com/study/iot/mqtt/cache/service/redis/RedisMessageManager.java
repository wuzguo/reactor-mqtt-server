package com.study.iot.mqtt.cache.service.redis;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.service.MessageManager;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.common.message.RetainMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.REDIS)
public class RedisMessageManager implements MessageManager {

    @Autowired
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public void saveRetain(RetainMessage message) {
        redisOpsTemplate.sadd(message.getTopic(), message);
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return redisOpsTemplate.get(topicName, RetainMessage.class);
    }
}
