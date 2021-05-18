package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.akka.annotation.ActorBean;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 8:40
 */

@Slf4j
@ActorBean
public class Publisher extends AbstractActor {

    private final ActorRef mediator;

    public Publisher(ActorSystem actorSystem) {
        this.mediator = DistributedPubSub.get(actorSystem).mediator();
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息体
     */
    public void tell(String topic, Object message) {
        mediator.tell(new DistributedPubSubMediator.Publish(topic, message), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> log.info("publisher receive message: {}", message))
            .build();
    }
}
