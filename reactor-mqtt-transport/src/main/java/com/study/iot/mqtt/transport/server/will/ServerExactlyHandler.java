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
 * @date 2021/5/7 13:54
 */

@WillStrategyService(group = Group.WILL_SERVER, type = MqttQoS.EXACTLY_ONCE)
public class ServerExactlyHandler implements WillCapable {

    @Override
    public void handler(MqttQoS qoS, TransportConnection connection, WillMessage willMessage) {
        connection.sendMessageRetry(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf())
                .subscribe();
    }
}
