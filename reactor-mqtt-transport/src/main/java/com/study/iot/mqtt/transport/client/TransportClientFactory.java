package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocal.ProtocolFactory;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ClientConfig;
import com.study.iot.mqtt.protocal.session.ClientSession;
import com.study.iot.mqtt.transport.client.connection.ClientConnection;
import reactor.core.publisher.Mono;


public class TransportClientFactory {

    private ProtocolFactory protocolFactory;

    private ClientConfig clientConfig;


    public TransportClientFactory() {
        protocolFactory = new ProtocolFactory();
    }


    public Mono<ClientSession> connect(ClientConfig config) {
        this.clientConfig = config;
        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
                .get().getTransport().connect(config)).map(this::wrapper).doOnError(config.getThrowableConsumer());
    }


    private ClientSession wrapper(TransportConnection connection) {
        return new ClientConnection(connection, clientConfig);
    }

}
