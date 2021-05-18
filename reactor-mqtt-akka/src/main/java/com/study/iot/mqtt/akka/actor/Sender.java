package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 8:55
 */

public class Sender extends AbstractActor {

    private final ActorRef mediator;

    // activate the extension
    public Sender(ActorSystem actorSystem) {
        this.mediator = DistributedPubSub.get(actorSystem).mediator();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(
            String.class,
            in -> {
                String out = in.toUpperCase();
                mediator.tell(
                    new DistributedPubSubMediator
                        .Send("/user/destination", out, true), getSelf());
            })
            .build();
    }
}
