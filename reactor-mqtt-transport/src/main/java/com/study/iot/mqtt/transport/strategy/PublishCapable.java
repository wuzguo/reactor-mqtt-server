package com.study.iot.mqtt.transport.strategy;

import com.study.iot.mqtt.common.message.SessionMessage;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 11:04
 */

public interface PublishCapable extends StrategyCapable {

    /**
     * 处理消息
     *
     * @param message    {@link MqttPublishMessage}
     * @param connection {@link DisposableConnection}
     */
    void handle(DisposableConnection connection, SessionMessage message);
}
