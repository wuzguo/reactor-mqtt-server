package com.study.iot.mqtt.protocol.proto.coap;

import com.study.iot.mqtt.protocol.Protocol;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 10:42
 */

@Slf4j
public class CoapTransport extends ProtocolTransport {

    public CoapTransport(Protocol protocol) {
        super(protocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ServerProperties properties,
        UnicastProcessor<DisposableConnection> processor) {
        return null;
    }

    @Override
    public Mono<DisposableConnection> connect(ClientProperties properties) {
        return null;
    }
}
