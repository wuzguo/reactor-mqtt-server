package com.study.iot.mqtt.transport.server.handler.connect;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.Attribute;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.netty.Connection;

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
    public void handle(MqttMessage message, DisposableConnection disposableConnection) {
        log.info("server DisConnect message: {}, connection: {}", message, disposableConnection);
        // 获取连接
        Connection connection = disposableConnection.getConnection();
        // 删除设备标识
        Optional.ofNullable(connection.channel().attr(AttributeKeys.identity)).map(Attribute::get)
            .ifPresent(identity -> {
                cacheManager.channel().remove(identity);
                connection.channel().attr(AttributeKeys.identity).set(null);
            });
        // 删除连接
        connection.channel().attr(AttributeKeys.disposableConnection).set(null);

        // 删除topic订阅
        Optional.ofNullable(disposableConnection.getTopics())
            .ifPresent(topics -> topics.forEach(topic -> cacheManager.topic().remove(topic, connection)));

        disposableConnection.dispose();
    }
}
