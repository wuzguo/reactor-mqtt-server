package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.session.domain.SessionMessage;
import com.study.iot.mqtt.session.manager.SessionManager;
import com.study.iot.mqtt.store.mapper.StoreMapper;
import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
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
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
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
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.PUBLISH)
public class ServerPublishHandler implements StrategyCapable {

    @Autowired
    private PublishStrategyContainer strategyContainer;

    @Autowired
    private StoreMapper storeMapper;

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
            storeMapper.message().saveRetain(retainMessage);
        }

        // 持久化消息
        SessionMessage sessionMessage = SessionMessage.builder().id(IdUtil.idGen())
            .messageId(variableHeader.packetId())
            .messageType(fixedHeader.messageType().value())
            .qos(fixedHeader.qosLevel().value())
            .copyByteBuf(bytes)
            .retain(fixedHeader.isRetain()).build();
        sessionManager.add("", sessionMessage);

        // 又来一个策略模式
        Optional.ofNullable(strategyContainer.findStrategy(StrategyGroup.SERVER_PUBLISH, fixedHeader.qosLevel()))
            .ifPresent(capable -> ((PublishStrategyCapable) capable).handle(disposableConnection, mqttMessage, bytes));
    }

    /**
     * 转换
     * @param byteBuf {@link ByteBuf}
     * @return {@link byte}
     */
    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
