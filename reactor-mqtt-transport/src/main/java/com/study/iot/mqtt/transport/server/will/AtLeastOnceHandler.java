package com.study.iot.mqtt.transport.server.will;

import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.transport.constant.Group;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import com.study.iot.mqtt.transport.strategy.WillStrategyService;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@WillStrategyService(group = Group.WILL, type = MqttQoS.AT_LEAST_ONCE)
public class AtLeastOnceHandler implements WillCapable {

    @Override
    public void handler(MqttQoS qoS, TransportConnection connection, WillMessage willMessage) {
        connection.sendMessage(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf())
                .subscribe();
    }
}
