package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.protocol.ConnectConfiguration;
import com.study.iot.mqtt.transport.constant.Group;
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
@StrategyService(group = Group.CLIENT, type = MqttMessageType.PUBACK)
public class ClientPubAckHandler implements StrategyCapable {

    @Override
    public void handler(MqttMessage message, TransportConnection connection) {

    }
}
