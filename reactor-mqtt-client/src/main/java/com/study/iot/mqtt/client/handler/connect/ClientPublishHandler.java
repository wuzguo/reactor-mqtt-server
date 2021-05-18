package com.study.iot.mqtt.client.handler.connect;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyContainer;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.PUBLISH)
public class ClientPublishHandler implements StrategyCapable {

    @Autowired
    private PublishStrategyContainer strategyContainer;

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("client Publish message: {}, connection: {}", message, disposableConnection);
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        MqttFixedHeader header = message.fixedHeader();
        byte[] bytes = copyByteBuf(mqttMessage.payload());
        // 又来一个策略模式
        Optional.ofNullable(strategyContainer.findStrategy(StrategyGroup.CLIENT_PUBLISH, header.qosLevel()))
            .ifPresent(capable -> ((PublishStrategyCapable) capable).handle(disposableConnection, mqttMessage, bytes));
    }

    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
