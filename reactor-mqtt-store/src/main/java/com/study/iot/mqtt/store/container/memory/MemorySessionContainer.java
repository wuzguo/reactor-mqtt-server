package com.study.iot.mqtt.store.container.memory;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 9:15
 */

@CacheStrategyService(group = CacheGroup.SESSION, type = CacheEnum.MEMORY)
public class MemorySessionContainer  implements StorageContainer<Object> {

    @Override
    public void add(String key, Object value) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public List<Object> list(String key) {
        return null;
    }

    @Override
    public List<Object> getAll() {
        return null;
    }

    @Override
    public Object getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
