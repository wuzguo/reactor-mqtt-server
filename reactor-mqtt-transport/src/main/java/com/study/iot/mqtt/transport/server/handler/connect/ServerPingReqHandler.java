package com.study.iot.mqtt.transport.server.handler.connect;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:20
 */

@Slf4j
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.PINGREQ)
public class ServerPingReqHandler implements StrategyCapable {

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server PingResp message: {}, connection: {}", message, connection);
        MqttMessage mqttMessage = MessageBuilder.buildPing(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE,
            false, 0);
        connection.sendMessage(mqttMessage);
    }
}