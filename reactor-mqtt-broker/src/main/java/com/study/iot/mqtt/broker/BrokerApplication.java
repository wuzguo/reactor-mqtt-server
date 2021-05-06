package com.study.iot.mqtt.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 9:54
 */

@SpringBootApplication(scanBasePackages = {
        "com.study.iot.mqtt.protocol",
        "com.study.iot.mqtt.transport",
        "com.study.iot.mqtt.cache",
        "com.study.iot.mqtt.auth",
        "com.study.iot.mqtt.broker"
})
public class BrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrokerApplication.class, args);
    }
}
