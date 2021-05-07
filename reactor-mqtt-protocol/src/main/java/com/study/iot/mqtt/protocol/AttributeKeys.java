package com.study.iot.mqtt.protocol;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.protocol.session.ServerSession;
import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;
import reactor.core.Disposable;

@UtilityClass
public class AttributeKeys {

    public AttributeKey<ClientSession> clientConnectionAttributeKey = AttributeKey.valueOf("client_operation");

    public AttributeKey<ServerSession> serverConnectionAttributeKey = AttributeKey.valueOf("server_operation");

    public AttributeKey<Disposable> closeConnection = AttributeKey.valueOf("close_connection");

    public AttributeKey<DisposableConnection> connectionAttributeKey = AttributeKey.valueOf("transport_connection");

    public AttributeKey<String> device_id = AttributeKey.valueOf("device_id");

    public AttributeKey<Integer> keepalived = AttributeKey.valueOf("keepalived");

    public AttributeKey<WillMessage> WILL_MESSAGE = AttributeKey.valueOf("WILL_MESSAGE");

}
