package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import java.util.List;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

public interface ServerSession extends Disposable {

    /**
     * 获取连接数
     *
     * @return {@link Disposable}
     */
    Mono<List<Disposable>> getConnections();

    /**
     * 关闭连接
     *
     * @param identity 标识
     * @return {@link Void}
     */
    Mono<Void> closeConnect(String identity);

    /**
     * 在关闭连接的时候处理的内容
     *
     * @param disposableConnection {@link DisposableConnection}
     */
    void setDisposeDeal(DisposableConnection disposableConnection);
}
