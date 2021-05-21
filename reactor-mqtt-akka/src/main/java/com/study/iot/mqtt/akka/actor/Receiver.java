package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.akka.annotation.ActorBean;
import com.study.iot.mqtt.akka.event.SenderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 9:07
 */

@Slf4j
@ActorBean
public class Receiver extends AbstractActor {

    private final ApplicationEventPublisher eventPublisher;

    public Receiver(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        // register to the path
        mediator.tell(new DistributedPubSubMediator.Put(getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, msg -> log.info("Got: {}", msg))
            .match(SenderEvent.class, this::processEvent).build();
    }

    private void processEvent(SenderEvent event) {
        log.info("receive event message: {}", event);
        eventPublisher.publishEvent(event);
    }
}
