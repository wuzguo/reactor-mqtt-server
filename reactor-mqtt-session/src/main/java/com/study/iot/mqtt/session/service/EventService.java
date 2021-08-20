package com.study.iot.mqtt.session.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Publisher;
import com.study.iot.mqtt.akka.event.BaseEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 13:40
 */

@Component
public class EventService implements InitializingBean {

    @Autowired
    private ActorSystem actorSystem;

    private ActorRef publisher;

    /**
     * 发布事件
     *
     * @param event {@link BaseEvent}
     */
    public void tellEvent(BaseEvent event) {
        publisher.tell(event, ActorRef.noSender());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        publisher = actorSystem.actorOf(SpringProps.create(actorSystem, Publisher.class), "event-publisher");
    }
}
