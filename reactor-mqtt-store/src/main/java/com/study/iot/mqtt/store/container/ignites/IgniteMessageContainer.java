package com.study.iot.mqtt.store.container.ignites;

import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.properties.IgniteProperties;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
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
public class IgniteMessageContainer implements StorageContainer<RetainMessage> {

    @Resource
    private IgniteCache<String, RetainMessage> messageCache;

    @Override
    public void add(String key, RetainMessage value) {
        messageCache.put(key, value);
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public RetainMessage get(String key) {
        return messageCache.get(key);
    }

    @Override
    public List<RetainMessage> list(String key) {
        return null;
    }

    @Override
    public List<RetainMessage> getAll() {
        return null;
    }

    @Override
    public RetainMessage getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
