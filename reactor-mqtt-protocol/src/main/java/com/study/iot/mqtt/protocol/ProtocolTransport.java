package com.study.iot.mqtt.protocol;

import com.study.iot.mqtt.cache.connection.DisposableConnection;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public abstract class ProtocolTransport {

    protected Protocol protocol;

    public ProtocolTransport(Protocol protocol) {
        this.protocol = protocol;
    }


    public abstract Mono<? extends DisposableServer> start(ConnectConfiguration config, UnicastProcessor<DisposableConnection> processor);


    public abstract Mono<DisposableConnection> connect(ConnectConfiguration config);
}
