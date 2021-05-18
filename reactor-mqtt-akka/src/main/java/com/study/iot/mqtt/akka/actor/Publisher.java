package com.study.iot.mqtt.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.study.iot.mqtt.akka.annotation.ActorBean;
import com.study.iot.mqtt.akka.event.BaseEvent;
import com.study.iot.mqtt.akka.topic.AkkaTopic;
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

    public Publisher() {
        this.mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
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
            .match(BaseEvent.class, this::processEvent)
            .build();
    }

    private void processEvent(BaseEvent event) {
        log.info("receive event message: {}", event);
        mediator.tell(new DistributedPubSubMediator.Publish(AkkaTopic.SUB_EVENT, event), getSelf());
    }
}
