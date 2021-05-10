package com.study.iot.mqtt.transport.server.handler.connect;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.PUBREC)
public class ServerPubRecHandler implements StrategyCapable {

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server PubRec message: {}, connection: {}", message, connection);
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        int messageId = variableHeader.messageId();
        connection.cancelDisposable(messageId);
        //  send rel
        connection.sendMessage(MessageBuilder.buildPubRel(messageId)).subscribe();
        // retry
        connection.addDisposable(messageId, Mono.fromRunnable(() ->
            connection.sendMessage(MessageBuilder.buildPubRel(messageId)).subscribe())
            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
    }
}
