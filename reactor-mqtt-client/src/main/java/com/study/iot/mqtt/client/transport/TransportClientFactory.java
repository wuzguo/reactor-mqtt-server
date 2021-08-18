package com.study.iot.mqtt.client.transport;


import com.study.iot.mqtt.client.connection.ClientConnection;
import com.study.iot.mqtt.client.router.ClientMessageRouter;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ClientSession;
import reactor.core.publisher.Mono;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:00
 */

public class TransportClientFactory {

    private final ProtocolFactory protocolFactory;

    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }

    public Mono<ClientSession> connect(ClientProperties properties, ClientMessageRouter messageRouter) {
        return Mono.from(protocolFactory.getProtocol(properties.getProtocol())
            .get().getTransport().connect(properties))
            .map(connection -> this.wrapper(connection, properties, messageRouter))
            .doOnError(properties.getThrowable());
    }

    private ClientSession wrapper(DisposableConnection connection, ClientProperties properties,
        ClientMessageRouter messageRouter) {
        return new ClientConnection(connection, properties, messageRouter);
    }
}
