package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("server DisConnect message: {}, connection: {}", message, disposableConnection);
        disposableConnection.dispose();
    }
}
