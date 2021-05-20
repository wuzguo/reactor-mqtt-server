package com.study.iot.mqtt.client.handler.publish;

import com.study.iot.mqtt.client.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.client.strategy.PublishStrategyService;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@Slf4j
@PublishStrategyService(group = StrategyGroup.CLIENT_PUBLISH, type = MqttQoS.AT_LEAST_ONCE)
public class ClientPublishAtLeastHandler implements PublishStrategyCapable {

    @Override
    public void handle(DisposableConnection connection, MqttPublishMessage message,  byte[] bytes) {
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        MqttFixedHeader header = message.fixedHeader();
        log.info("client publish topic: {}, message: {}", variableHeader.topicName(), new String(bytes,
            CharsetUtil.UTF_8));
        // back
        MqttPubAckMessage mqttMessage = MessageBuilder.buildPubAck(header.isDup(), header.qosLevel(),
            header.isRetain(), variableHeader.packetId());
        connection.sendMessage(mqttMessage).subscribe();
    }
}
