package com.study.iot.mqtt.cluster.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.cluster.annotation.ActorBean;
import lombok.extern.slf4j.Slf4j;

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

    private final ActorRef mediator;

    public Subscriber(ActorSystem actorSystem) {
        this.mediator = DistributedPubSub.get(actorSystem).mediator();
    }

    /**
     * 订阅消息
     *
     * @param topic 主题
     */
    public void subscribe(String topic) {
        // subscribe to the topic named "topic"
        mediator.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> log.info("Got: {}", message))
            .match(DistributedPubSubMediator.SubscribeAck.class,
                message -> log.info("receive subscribe message: {}", message))
            .build();
    }
}
