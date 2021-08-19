package com.study.iot.mqtt.client.handler.publish;

import com.study.iot.mqtt.client.strategy.PublishCapable;
import com.study.iot.mqtt.client.strategy.StrategyEnum;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.client.strategy.StrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@Slf4j
@StrategyService(group = StrategyGroup.PUBLISH, type = StrategyEnum.AT_MOST_ONCE)
public class ClientPublishAtMostHandler implements PublishCapable {

    @Override
    public void handle(DisposableConnection connection, MqttPublishMessage message, byte[] bytes) {
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        log.info("client publish topic: {}, message: {}", variableHeader.topicName(), new String(bytes,
            CharsetUtil.UTF_8));
    }
}
