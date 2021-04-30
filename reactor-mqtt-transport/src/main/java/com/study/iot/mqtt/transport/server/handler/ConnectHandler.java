package com.study.iot.mqtt.transport.server.handler;

import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 11:08
 */

public class ConnectHandler  implements DirectHandler {

    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration configuration) {

    }
}
