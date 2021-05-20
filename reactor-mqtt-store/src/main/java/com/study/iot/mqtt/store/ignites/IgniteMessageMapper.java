package com.study.iot.mqtt.store.ignites;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.mapper.MessageMapper;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
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
public class IgniteMessageMapper implements MessageMapper {

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
