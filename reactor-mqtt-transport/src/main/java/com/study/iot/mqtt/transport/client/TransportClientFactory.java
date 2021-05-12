package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.connection.ClientConnection;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import reactor.core.publisher.Mono;


public class TransportClientFactory {

    private final ProtocolFactory protocolFactory;

    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }

    public Mono<ClientSession> connect(ClientConfiguration configuration, ClientMessageRouter messageRouter) {
        return Mono.from(protocolFactory.getProtocol(configuration.getProtocol())
            .get().getTransport().connect(configuration))
            .map(connection -> this.wrapper(connection, configuration, messageRouter))
            .doOnError(configuration.getThrowable());
    }

    private ClientSession wrapper(DisposableConnection connection, ClientConfiguration configuration,
        ClientMessageRouter messageRouter) {
        return new ClientConnection(connection, configuration, messageRouter);
    }

}
