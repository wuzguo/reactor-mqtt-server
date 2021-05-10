package com.study.iot.mqtt.transport.server.will;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import com.study.iot.mqtt.transport.strategy.WillStrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@WillStrategyService(group = StrategyGroup.WILL_SERVER, type = MqttQoS.AT_LEAST_ONCE)
public class ServerAtLeastHandler implements WillCapable {

    @Override
    public void handler(MqttQoS qoS, DisposableConnection connection, WillMessage willMessage) {
        MqttMessage message = MessageBuilder
            .buildPub(false, qoS, willMessage.getRetain(), connection.idGen(), willMessage.getTopicName(),
                willMessage.getMessage());
        connection.sendMessage(message).subscribe();
    }
}
