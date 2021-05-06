package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
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
 * @date 2021/5/6 11:25
 */

@Slf4j
@StrategyService(group = Group.CLIENT,  type = MqttMessageType.SUBACK)
public class SubAckHandler implements StrategyCapable {
    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration configuration) {

    }
}
