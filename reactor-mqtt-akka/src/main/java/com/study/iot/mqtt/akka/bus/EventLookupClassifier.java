package com.study.iot.mqtt.akka.bus;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 16:59
 */

@Component
public class EventLookupClassifier extends LookupEventBus<EventBusMessage, ActorRef, String> {

    @Override
    public int mapSize() {
        return 128;
    }

    @Override
    public int compareSubscribers(ActorRef aRef, ActorRef cRef) {
        return aRef.compareTo(cRef);
    }

    @Override
    public String classify(EventBusMessage event) {
        return event.getTopic();
    }

    @Override
    public void publish(EventBusMessage event, ActorRef subscriber) {
        subscriber.tell(event.getEvent(), ActorRef.noSender());
    }
}
