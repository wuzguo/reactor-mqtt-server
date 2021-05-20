package com.study.iot.mqtt.store.ignites;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.mapper.SessionMapper;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.io.Serializable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 9:15
 */

@CacheStrategyService(group = CacheGroup.SESSION, type = CacheStrategy.IGNITE)
public class IgniteSessionMapper implements SessionMapper {

    @Override
    public void add(String identity, Serializable value) {

    }

    @Override
    public void remove(String identity) {

    }

    @Override
    public <T extends Serializable> T get(String identity) {
        return null;
    }
}
