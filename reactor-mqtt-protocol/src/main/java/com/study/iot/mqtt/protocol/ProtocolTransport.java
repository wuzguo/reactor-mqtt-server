package com.study.iot.mqtt.protocol;

import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:16
 */

public abstract class ProtocolTransport {

    protected Protocol protocol;

    public ProtocolTransport(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * 启动
     *
     * @param properties {@link ClientProperties}
     * @param processor  {@link UnicastProcessor}
     * @return {@link DisposableServer}
     */
    public abstract Mono<? extends DisposableServer> start(ServerProperties properties,
        UnicastProcessor<DisposableConnection> processor);

    /**
     * 连接信息
     *
     * @param properties {@link ClientProperties}
     * @return {@link DisposableConnection}
     */
    public abstract Mono<DisposableConnection> connect(ClientProperties properties);
}
