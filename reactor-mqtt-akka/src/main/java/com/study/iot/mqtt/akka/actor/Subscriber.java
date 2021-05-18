package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.akka.annotation.ActorBean;
import com.study.iot.mqtt.akka.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 8:39
 */

@Slf4j
@ActorBean
public class Subscriber extends AbstractActor {

    private final ApplicationEventPublisher eventPublisher;

    public Subscriber(String topic, ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> log.info("receive string message : {}", message))
            .match(BaseEvent.class, this::processEvent)
            .match(DistributedPubSubMediator.SubscribeAck.class,
                message -> log.info("receive subscribe message: {}", message))
            .build();
    }

    private void processEvent(BaseEvent event) {
        log.info("receive event message: {}", event);
        eventPublisher.publishEvent(event);
    }
}
