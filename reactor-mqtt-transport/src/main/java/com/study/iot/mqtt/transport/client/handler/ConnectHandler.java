package com.study.iot.mqtt.transport.client.handler;


import com.study.iot.mqtt.protocal.AttributeKeys;
import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectHandler implements DirectHandler {


    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration config) {
            MqttConnAckMessage mqttConnAckMessage = (MqttConnAckMessage) message;
            MqttConnAckVariableHeader variableHeader=mqttConnAckMessage.variableHeader();
            switch (message.fixedHeader().messageType()){
                case CONNACK:
                    switch (variableHeader.connectReturnCode()){
                        case CONNECTION_ACCEPTED:
                            connection.getConnection().channel().attr(AttributeKeys.closeConnection).get().dispose();//取消重发
                            break;
                        case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                            log.error("login error", new RuntimeException("用户名密码错误"));
                            break;
                        case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                            log.error("login error", new RuntimeException("clientId  不允许链接"));
                            break;
                        case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                            log.error("login error",  new RuntimeException("服务不可用"));
                            break;
                        case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                            log.error("login error",  new RuntimeException("mqtt 版本不可用"));
                            break;
                        case CONNECTION_REFUSED_NOT_AUTHORIZED:
                            log.error("login error", new RuntimeException("未授权登录"));
                            break;
                    }
                    break;
            }
    }
}
