package com.study.iot.mqtt.session.runner;

import com.study.iot.mqtt.akka.topic.AkkaTopic;
import com.study.iot.mqtt.session.manager.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:50
 */

@Order(3)
@Component
public class SessionManagerRunner implements ApplicationRunner {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sessionManager.subscribe(AkkaTopic.SUB_EVENT);
    }
}
