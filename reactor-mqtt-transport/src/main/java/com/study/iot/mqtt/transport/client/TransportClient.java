package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ClientConfiguration.Options;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import reactor.core.publisher.Mono;


public class TransportClient {

    private ClientConfiguration configuration;

    private final TransportClientFactory transportFactory;

    private final ClientConfiguration.Options options;

    private TransportClient() {
        transportFactory = new TransportClientFactory();
        options = new Options();
    }

    public TransportClient create(ClientConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }


    public Mono<ClientSession> connect(ClientMessageRouter messageRouter) {
        configuration.setOptions(options);
        return transportFactory.connect(configuration, messageRouter);
    }
}
