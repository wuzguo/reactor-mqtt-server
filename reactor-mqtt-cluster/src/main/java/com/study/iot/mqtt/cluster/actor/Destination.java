package com.study.iot.mqtt.cluster.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.study.iot.mqtt.cluster.annotation.ActorBean;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 8:53
 */

@ActorBean
public class Destination extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public Destination(ActorSystem actorSystem) {
        ActorRef mediator = DistributedPubSub.get(actorSystem).mediator();
        // register to the path
        mediator.tell(new DistributedPubSubMediator.Put(getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, msg -> log.info("Got: {}", msg)).build();
    }
}
