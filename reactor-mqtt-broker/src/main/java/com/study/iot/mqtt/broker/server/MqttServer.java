package com.study.iot.mqtt.broker.server;

import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocal.handler.MemoryMessageHandler;
import com.study.iot.mqtt.transport.server.TransportServer;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TransportServer.create("localhost", 1884)
                .auth((s, p) -> true)
                .heart(100000)
                .protocol(ProtocolType.MQTT)
                .ssl(false)
                .auth((username, password) -> true)
                .log(true)
                .messageHandler(new MemoryMessageHandler())
                .exception(throwable -> System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + throwable))
                .start()
                .block();
    }
}
