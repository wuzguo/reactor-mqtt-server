package com.study.iot.mqtt.transport.server.handler.publish;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@PublishStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.AT_LEAST_ONCE)
public class ServerPublishAtLeastHandler implements PublishStrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        // back
        MqttPubAckMessage mqttPubAckMessage = MessageBuilder.buildPubAck(header.isDup(), header.qosLevel(),
            header.isRetain(), variableHeader.packetId());
        connection.sendMessage(mqttPubAckMessage).subscribe();

        cacheManager.topic().getConnections(variableHeader.topicName())
            .stream().filter(disposable -> !connection.equals(disposable) && !disposable.isDispose())
            .forEach(disposable -> {
                int id = connection.messageId();
                MqttPublishMessage publishMessage = MessageBuilder.buildPub(false, header.qosLevel(),
                    header.isRetain(), id,
                    variableHeader.topicName(),
                    bytes); // pub
                disposable.sendMessageRetry(id, publishMessage).subscribe();
            });
    }
}
