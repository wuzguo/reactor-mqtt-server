package com.study.iot.mqtt.transport.server.handler;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.Attribute;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:20
 */

@Slf4j
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.DISCONNECT)
public class ServerDisConnectHandler implements StrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server DisConnect message: {}, connection: {}", message, connection);
        // 删除设备标识
        Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.identity))
            .map(Attribute::get)
            .ifPresent(cacheManager.channel()::removeChannel);
        // 删除topic订阅
        Optional.ofNullable(connection.getTopics())
            .ifPresent(topics -> topics.forEach(topic -> cacheManager.topic().deleteConnection(topic, connection)));
        connection.dispose();
    }
}
