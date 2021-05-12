package com.study.iot.mqtt.transport.server.handler.will;

import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import com.study.iot.mqtt.transport.strategy.WillStrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.common.message.WillMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@WillStrategyService(group = StrategyGroup.WILL_SERVER, type = MqttQoS.EXACTLY_ONCE)
public class ServerWillExactlyHandler implements WillCapable {

    @Override
    public void handle(MqttQoS qoS, DisposableConnection connection, WillMessage willMessage) {
        int messageId = connection.messageId();
        MqttMessage message = MessageBuilder.buildPub(false, qoS, willMessage.getIsRetain(), messageId,
            willMessage.getTopic(), willMessage.getCopyByteBuf());
        connection.sendMessageRetry(messageId, message).subscribe();
    }
}
