package com.study.iot.mqtt.protocol;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ConnectConfiguration;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public abstract class ProtocolTransport {

    protected Protocol protocol;

    public ProtocolTransport(Protocol protocol) {
        this.protocol = protocol;
    }


    public abstract Mono<? extends DisposableServer> start(ServerConfiguration configuration,
        UnicastProcessor<DisposableConnection> processor);


    public abstract Mono<DisposableConnection> connect(ClientConfiguration configuration);
}
