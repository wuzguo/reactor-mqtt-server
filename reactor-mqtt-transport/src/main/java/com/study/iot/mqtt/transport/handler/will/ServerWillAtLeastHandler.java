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
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@Slf4j
@StrategyService(group = StrategyGroup.WILL, type = StrategyEnum.AT_LEAST_ONCE)
public class ServerWillAtLeastHandler implements WillCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttQoS qoS, WillMessage willMessage) {
        log.info("will at_least_once message: {}", willMessage);
        MqttMessage message = MessageBuilder.buildPub(false, qoS, willMessage.getRetain(), IdUtils.messageId(),
            willMessage.getTopic(), willMessage.getCopyByteBuf());
        disposableConnection.sendMessage(message).subscribe();
    }
}
