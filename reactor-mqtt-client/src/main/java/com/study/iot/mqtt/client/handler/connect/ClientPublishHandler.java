package com.study.iot.mqtt.client.handler.connect;

import com.study.iot.mqtt.client.strategy.ConnectCapable;
import com.study.iot.mqtt.client.strategy.PublishCapable;
import com.study.iot.mqtt.client.strategy.PublishStrategyContainer;
import com.study.iot.mqtt.client.strategy.StrategyCapable;
import com.study.iot.mqtt.client.strategy.StrategyEnum;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.client.strategy.StrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
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
@StrategyService(group = StrategyGroup.CLIENT, type = StrategyEnum.PUBLISH)
public class ClientPublishHandler implements ConnectCapable {

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
            .ifPresent(capable -> ((PublishCapable) capable).handle(disposableConnection, mqttMessage, bytes));
    }

    /**
     * 转换
     *
     * @param byteBuf {@link ByteBuf}
     * @return {@link byte}
     */
    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
