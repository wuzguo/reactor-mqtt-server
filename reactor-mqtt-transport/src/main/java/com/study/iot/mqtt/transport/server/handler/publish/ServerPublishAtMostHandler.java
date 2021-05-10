package com.study.iot.mqtt.transport.server.handler.publish;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@PublishStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.AT_MOST_ONCE)
public class ServerPublishAtMostHandler implements PublishStrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        // 过滤掉本身 已经关闭的dispose
        cacheManager.topic().getConnections(variableHeader.topicName())
            .stream().filter(disposable -> !connection.equals(disposable) && !disposable.isDispose())
            .forEach(disposable -> {
                MqttMessage mqttMessage = MessageBuilder.buildPub(false, header.qosLevel(), header.isRetain(),
                    connection.messageId(), variableHeader.topicName(), bytes);
                disposable.sendMessage(mqttMessage).subscribe();
            });
    }
}
