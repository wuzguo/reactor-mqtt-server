package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：订阅确认</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.SUBACK)
public class ServerSubscribeAckHandler implements ConnectCapable {

    @Override
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("subAck message: {}, connection: {}", mqttMessage, disposable);
    }
}
