package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.connection.ClientConnection;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import reactor.core.publisher.Mono;


public class TransportClientFactory {

    private ProtocolFactory protocolFactory;

    private ClientConfiguration clientConfiguration;


    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }


    public Mono<ClientSession> connect(ClientConfiguration config, ClientMessageRouter messageRouter) {
        this.clientConfiguration = config;
        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
                .get().getTransport().connect(config))
                .map(connection -> this.wrapper(connection, messageRouter))
                .doOnError(config.getThrowableConsumer());
    }


    private ClientSession wrapper(TransportConnection connection, ClientMessageRouter messageRouter) {
        return new ClientConnection(connection, clientConfiguration, messageRouter);
    }

}
