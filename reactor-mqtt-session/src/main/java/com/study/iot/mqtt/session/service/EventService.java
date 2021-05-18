package com.study.iot.mqtt.session.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Publisher;
import com.study.iot.mqtt.akka.event.BaseEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
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
public class EventService {

    @Autowired
    private ActorSystem actorSystem;

    /**
     * 发布事件
     *
     * @param event {@link BaseEvent}
     */
    public void pubEvent(BaseEvent event) {
        ActorRef publisher = actorSystem.actorOf(SpringProps.create(actorSystem, Publisher.class), "publisher");
        publisher.tell(event, ActorRef.noSender());
    }
}
