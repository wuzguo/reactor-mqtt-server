package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.akka.annotation.ActorBean;
import com.study.iot.mqtt.akka.event.SenderEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 9:09
 */

@Slf4j
@ActorBean
public class Sender extends AbstractActor {

    private final ActorRef mediator;

    public Sender() {
        this.mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SenderEvent.class, event -> {
                String path = String.format("/user/%s", event.getPath());
                log.info("sender receive message: {}, {}", path, event);
                mediator.tell(new DistributedPubSubMediator.Send(path, event, true), getSelf());
            }).build();
    }
}