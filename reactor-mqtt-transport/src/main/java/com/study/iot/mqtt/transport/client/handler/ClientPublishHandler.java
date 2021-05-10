package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("client Publish message: {}, connection: {}", message, connection);
    }
}
