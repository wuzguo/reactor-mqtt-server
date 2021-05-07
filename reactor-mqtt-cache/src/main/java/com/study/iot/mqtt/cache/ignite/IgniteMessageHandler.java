package com.study.iot.mqtt.cache.ignite;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.ChannelManager;
import com.study.iot.mqtt.cache.manager.MessageHandler;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.message.RetainMessage;
import org.apache.ignite.IgniteCache;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.IGNITE)
public class IgniteMessageHandler implements MessageHandler {

    @Resource
    private IgniteCache<String,  RetainMessage> messages;

    @Override
    public void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf) {
        messages.put(topicName, new RetainMessage(dup, retain, qos, topicName, copyByteBuf));
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return messages.get(topicName);
    }
}
