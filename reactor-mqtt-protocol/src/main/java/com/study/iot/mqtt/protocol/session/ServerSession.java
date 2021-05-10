package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import java.util.List;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface ServerSession extends Disposable {

    /**
     * 获取连接数
     *
     * @return {@link DisposableConnection}
     */
    Mono<List<DisposableConnection>> getConnections();

    /**
     * 关闭连接
     *
     * @param identity 标识
     * @return {@link Void}
     */
    Mono<Void> closeConnect(String identity);
}
