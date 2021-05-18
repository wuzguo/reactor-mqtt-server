package com.study.iot.mqtt.client.handler.connect;


import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.PUBREL)
public class ClientPubRelHandler implements StrategyCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("client PubRel message: {}, connection: {}", message, disposableConnection);

        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        int messageId = variableHeader.messageId();
        // cancel replay rec
        disposableConnection.cancelDisposable(messageId);
        MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubComp(messageId);
        //  send comp
        disposableConnection.sendMessage(mqttPubRecMessage).subscribe();
        // 移除消息
        disposableConnection.getAndRemoveQos2Message(messageId).ifPresent(
            msg -> log.info("client publish topic: {}, message: {}", msg.getTopic(), new String(msg.getCopyByteBuf(),
                CharsetUtil.UTF_8)));
    }
}
