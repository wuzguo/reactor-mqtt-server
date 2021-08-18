package com.study.iot.mqtt.transport.handler.will;

import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@StrategyService(group = StrategyGroup.WILL_SERVER, type = StrategyEnum.EXACTLY_ONCE)
public class ServerWillExactlyHandler implements WillCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttQoS qoS, WillMessage willMessage) {
        int messageId = IdUtils.messageId();
        MqttMessage message = MessageBuilder.buildPub(false, qoS, willMessage.getIsRetain(), messageId,
            willMessage.getTopic(), willMessage.getCopyByteBuf());
        disposableConnection.sendMessageRetry(messageId, message).subscribe();
    }
}
