package com.study.iot.mqtt.protocol;


import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.common.message.WillMessage;
import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;
import reactor.core.Disposable;

@UtilityClass
public class AttributeKeys {

    public AttributeKey<ClientSession> clientConnection = AttributeKey.valueOf("client_connection");

    public AttributeKey<ServerSession> serverConnection = AttributeKey.valueOf("server_connection");

    public AttributeKey<Disposable> closeConnection = AttributeKey.valueOf("close_connection");

    public AttributeKey<DisposableConnection> disposableConnection = AttributeKey.valueOf("disposable_connection");

    public AttributeKey<String> identity = AttributeKey.valueOf("identity");

    public AttributeKey<Integer> keepalive = AttributeKey.valueOf("keep_alive");

    public AttributeKey<WillMessage> willMessage = AttributeKey.valueOf("will_message");

}
