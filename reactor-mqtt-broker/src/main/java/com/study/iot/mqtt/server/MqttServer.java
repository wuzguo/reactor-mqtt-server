package com.study.iot.mqtt.server;

import com.google.common.collect.Sets;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.server.TransportServer;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 10:11
 */

@Slf4j
public class MqttServer implements ApplicationRunner {

    private final CacheManager cacheManager;

    private final ServerMessageRouter messageRouter;

    public MqttServer(CacheManager cacheManager, ServerMessageRouter messageRouter) {
        this.cacheManager = cacheManager;
        this.messageRouter = messageRouter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 配置文件
        ServerConfiguration configuration = ServerConfiguration.builder()
            .host("localhost")
            .port(1883)
            .heart(100000)
            .protocols(Sets.newHashSet(ProtocolType.MQTT, ProtocolType.WEB_SOCKET))
            .isSsl(false)
            .strategy(CacheStrategy.MEMORY)
            .isLog(true)
            .throwable(e -> log.error("exception occurred when starting mqtt server：{}", e.getMessage()))
            .build();
        ServerSession session = new TransportServer().create(configuration).start(cacheManager, messageRouter).block();
        session.getConnections().subscribe(disposables -> disposables.forEach(DisposableConnection::destory));
    }
}
