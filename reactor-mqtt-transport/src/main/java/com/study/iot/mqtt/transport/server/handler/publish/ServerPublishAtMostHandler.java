package com.study.iot.mqtt.transport.server.handler.publish;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
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
 * @date 2021/5/7 13:54
 */

@Slf4j
@PublishStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.AT_MOST_ONCE)
public class ServerPublishAtMostHandler implements PublishStrategyCapable {

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        log.info("client publish topic: {}, message: {}", variableHeader.topicName(), new String(bytes,
            CharsetUtil.UTF_8));
    }
}
