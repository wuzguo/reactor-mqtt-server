package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.TransportConnection;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.connection.ClientConnection;
import reactor.core.publisher.Mono;


public class TransportClientFactory {

    private ProtocolFactory protocolFactory;

    private ClientConfiguration clientConfiguration;


    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }


    public Mono<ClientSession> connect(ClientConfiguration config) {
        this.clientConfiguration = config;
        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
                .get().getTransport().connect(config)).map(this::wrapper).doOnError(config.getThrowableConsumer());
    }


    private ClientSession wrapper(TransportConnection connection) {
        return new ClientConnection(connection, clientConfiguration);
    }

}
