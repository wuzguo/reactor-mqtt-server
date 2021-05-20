package com.study.iot.mqtt.client.handler.connect;

import com.study.iot.mqtt.client.strategy.StrategyCapable;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.client.strategy.StrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
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
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.PINGRESP)
public class ClientPingRespHandler implements StrategyCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("client PingResp message: {}, connection: {}", message, disposableConnection);
    }
}
