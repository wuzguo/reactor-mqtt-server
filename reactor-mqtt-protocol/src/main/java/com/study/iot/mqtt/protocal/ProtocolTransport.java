package com.study.iot.mqtt.protocal;

import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public abstract class ProtocolTransport {

    protected Protocol protocol;

    public ProtocolTransport(Protocol protocol){
        this.protocol=protocol;
    }

    public abstract Mono<? extends DisposableServer> start(ConnectConfiguration config, UnicastProcessor<TransportConnection> connections);


    public abstract Mono<TransportConnection>  connect(ConnectConfiguration config);
}
