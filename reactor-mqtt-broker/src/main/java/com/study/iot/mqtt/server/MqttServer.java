package com.study.iot.mqtt.server;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.enums.CacheStrategy;
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

    private CacheManager cacheManager;

    private ServerMessageRouter messageRouter;

    public MqttServer(CacheManager cacheManager, ServerMessageRouter messageRouter) {
        this.cacheManager = cacheManager;
        this.messageRouter = messageRouter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TransportServer.create("localhost", 1884)
                .heart(100000)
                .protocol(ProtocolType.MQTT)
                .ssl(false)
                .auth((key, secret) -> true)
                .cache(CacheStrategy.MEMORY)
                .log(true)
                .exception(e -> log.error("exception occurred when starting mqtt server：{}", e.getMessage()))
                .start(cacheManager, messageRouter)
                .block();
    }
}
