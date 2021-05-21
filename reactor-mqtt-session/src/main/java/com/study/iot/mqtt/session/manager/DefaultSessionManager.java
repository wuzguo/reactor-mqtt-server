package com.study.iot.mqtt.session.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Publisher;
import com.study.iot.mqtt.akka.actor.Receiver;
import com.study.iot.mqtt.akka.actor.Subscriber;
import com.study.iot.mqtt.akka.event.SubscribeEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
import com.study.iot.mqtt.akka.topic.AkkaTopic;
import com.study.iot.mqtt.common.domain.ConnectSession;
import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.session.config.InstanceUtil;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.hbase.HbaseTemplate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 13:46
 */

@Component
public class DefaultSessionManager implements SessionManager {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private InstanceUtil instanceUtil;

    @Override
    public void add(String instanceId, String identity, Boolean isCleanSession) {
        ConnectSession session = ConnectSession.builder().instanceId(instanceId).identity(identity)
            .topics(Collections.emptyList()).build();
        // 如果是持久化 Session 需要放入Redis保存
        containerManager.take(CacheGroup.SESSION).add(identity, session);

    }

    @Override
    public ConnectSession get(String identity) {
        // 先写死
        return ConnectSession.builder().instanceId("localhost:1884").build();
    }

    @Override
    public void add(String identity, SessionMessage message) {
        // 持久化
        hbaseTemplate.saveOrUpdate("reactor-mqtt-message", message);
        // 发布订阅消息
        ActorRef publisher = actorSystem.actorOf(SpringProps.create(actorSystem, Publisher.class), "publisher");
        SubscribeEvent event = new SubscribeEvent(this, IdUtil.idGen());
        event.setIdentity(identity);
        event.setInstanceId(instanceUtil.getInstanceId());
        event.setRow(message.getRow());
        event.setTopic(AkkaTopic.SUB_EVENT);
        publisher.tell(event, ActorRef.noSender());
    }

    @Override
    public void doReady(String topic) {
        // 发布订阅模式的订阅者
        actorSystem.actorOf(SpringProps.create(actorSystem, Subscriber.class, topic, eventPublisher),
            "subscriber");
        // 点对点模式的接收者，接收者名称为实例ID
        actorSystem.actorOf(SpringProps.create(actorSystem, Receiver.class, eventPublisher),
            instanceUtil.getInstanceId());
    }
}
