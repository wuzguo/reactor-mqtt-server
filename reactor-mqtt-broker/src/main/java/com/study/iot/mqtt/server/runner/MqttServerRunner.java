package com.study.iot.mqtt.server.runner;

import com.google.common.collect.Sets;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.server.config.MqttProperties;
import com.study.iot.mqtt.transport.TransportServer;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 10:11
 */

@Slf4j
@Component
public class MqttServerRunner implements ApplicationRunner {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ServerMessageRouter messageRouter;

    @Autowired
    private MqttProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 配置文件
        ServerProperties serverProperties = ServerProperties.builder()
            .host(properties.getHost()).port(properties.getPort())
            .protocols(Sets.newHashSet(ProtocolType.MQTT))
            .strategy(properties.getStrategy())
            .sendBufSize(32 * 1024)
            .revBufSize(32 * 1024)
            .backlog(properties.getBacklog())
            .keepAlive(properties.getKeepAlive())
            .noDelay(true)
            .isSsl(properties.getEnableSsl())
            .isLog(properties.getEnableLog())
            .throwable(e -> log.error("starting mqtt server exception：{}", e.getMessage()))
            .build();
        // 启动服务
        ServerSession serverSession = new TransportServer().create(serverProperties)
            .start(cacheManager, messageRouter).block();
        Optional.ofNullable(serverSession)
            .ifPresent(session -> session.getConnections().subscribe(disposables -> disposables.stream()
                .map(disposable -> (DisposableConnection) disposable)
                .forEach(DisposableConnection::destroy)));
    }
}
