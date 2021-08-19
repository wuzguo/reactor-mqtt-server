package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.session.manager.SessionManager;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.netty.Connection;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.PUBLISH)
public class ServerPublishHandler implements ConnectCapable {

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private SessionManager sessionManager;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_PUBLISH_COUNT)
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("server Publish message: {}, connection: {}", message, disposableConnection);
        MqttFixedHeader fixedHeader = message.fixedHeader();
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        MqttPublishVariableHeader variableHeader = mqttMessage.variableHeader();
        byte[] bytes = copyByteBuf(mqttMessage.payload());
        //保留消息
        if (fixedHeader.isRetain()) {
            RetainMessage retainMessage = RetainMessage.builder().topic(variableHeader.topicName())
                .isRetain(fixedHeader.isRetain()).isDup(fixedHeader.isDup()).qos(fixedHeader.qosLevel().value())
                .copyByteBuf(bytes).build();
            containerManager.take(CacheGroup.MESSAGE).add(variableHeader.topicName(), retainMessage);
        }

        // 持久化消息，这里可以使用发布订阅模式
        Connection connection = disposableConnection.getConnection();
        // 当前发送消息的客户端ID
        String identity = connection.channel().attr(AttributeKeys.identity).get();
        SessionMessage sessionMessage = SessionMessage.builder().row(IdUtils.idGen().toString())
            .identity(identity)
            .sessionId(IdUtils.idGen().toString())
            .messageId(variableHeader.packetId())
            .topic(variableHeader.topicName())
            .retain(fixedHeader.isRetain())
            .messageType(fixedHeader.messageType().value())
            .qos(fixedHeader.qosLevel().value())
            .dup(fixedHeader.isDup())
            .copyByteBuf(bytes).build();
        sessionManager.add(identity, sessionMessage);
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
