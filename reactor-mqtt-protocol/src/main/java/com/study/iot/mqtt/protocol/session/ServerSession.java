package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import java.util.List;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface ServerSession extends Disposable {


    Mono<List<DisposableConnection>> getConnections();


    Mono<Void> closeConnect(String clientId);
}
