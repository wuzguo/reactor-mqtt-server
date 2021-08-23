package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：确认连接</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/23 10:48
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.CONNACK)
public class ServerConAckHandler implements ConnectCapable {

    @Override
    public void handle(DisposableConnection connection, MqttMessage mqttMessage) {
        log.info("connectAck message: {}", mqttMessage);
    }
}
