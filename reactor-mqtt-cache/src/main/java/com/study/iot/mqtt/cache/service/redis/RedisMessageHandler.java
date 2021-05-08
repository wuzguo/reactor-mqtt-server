package com.study.iot.mqtt.cache.service.redis;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MessageHandler;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.enums.CacheStrategy;
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
public class RedisMessageHandler implements MessageHandler {

    @Autowired
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf) {
        redisOpsTemplate.sadd(topicName, new RetainMessage(dup, retain, qos, topicName, copyByteBuf));
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return redisOpsTemplate.get(topicName, RetainMessage.class);
    }
}
