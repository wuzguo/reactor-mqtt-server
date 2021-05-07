package com.study.iot.mqtt.server;

import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.handler.MemoryMessageHandler;
import com.study.iot.mqtt.transport.server.TransportServer;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
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

    public MqttServer(StrategyContainer container) {
        this.container = container;
    }

    private StrategyContainer container;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TransportServer.create("localhost", 1884)
                .heart(100000)
                .protocol(ProtocolType.MQTT)
                .ssl(false)
                .auth((key, secret) -> true)
                .log(true)
                .messageHandler(new MemoryMessageHandler())
                .exception(e -> log.error("启动mqtt server 发生异常：{}", e.getMessage()))
                .start(container)
                .block();
    }
}
