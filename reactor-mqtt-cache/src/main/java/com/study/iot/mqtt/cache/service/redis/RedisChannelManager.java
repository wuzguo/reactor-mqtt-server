package com.study.iot.mqtt.cache.service.redis;


import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.REDIS)
public class RedisChannelManager implements ChannelManager {

    @Autowired
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public void add(String identity, DisposableConnection connection) {
        redisOpsTemplate.sadd(identity, connection);
    }

    @Override
    public void remove(String identity) {
        redisOpsTemplate.del(identity);
    }

    @Override
    public DisposableConnection getAndRemove(String identity) {
        DisposableConnection connection = redisOpsTemplate.get(identity, DisposableConnection.class);
        redisOpsTemplate.del(identity);
        return connection;
    }

    @Override
    public Boolean containsKey(String identity) {
        return ObjectUtil.isNull(redisOpsTemplate.get(identity, DisposableConnection.class));
    }

    @Override
    public Collection<DisposableConnection> getConnections() {
        return Collections.emptyList();
    }
}
