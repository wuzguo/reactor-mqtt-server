package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.connection.ClientConnection;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import reactor.core.publisher.Mono;


public class TransportClientFactory {

    private final ProtocolFactory protocolFactory;

    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }

    public Mono<ClientSession> connect(ClientConfiguration config, ClientMessageRouter messageRouter) {
        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
            .get().getTransport().connect(config))
            .map(connection -> this.wrapper(connection, config, messageRouter))
            .doOnError(config.getThrowableConsumer());
    }

    private ClientSession wrapper(DisposableConnection connection, ClientConfiguration config,
        ClientMessageRouter messageRouter) {
        return new ClientConnection(connection, config, messageRouter);
    }

}
