package com.study.iot.mqtt.session.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Receiver;
import com.study.iot.mqtt.akka.actor.Sender;
import com.study.iot.mqtt.akka.actor.Subscriber;
import com.study.iot.mqtt.akka.event.SenderEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
import com.study.iot.mqtt.common.domain.BaseMessage;
import com.study.iot.mqtt.common.domain.ConnectSession;
import com.study.iot.mqtt.common.utils.IdUtil;
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
        return ConnectSession.builder().instanceId("localhost:1885").build();
    }

    @Override
    public void add(String identity, BaseMessage message) {
        // 持久化
        this.hbaseTemplate.saveOrUpdate("reactor-mqtt-message", message);
        // 发送定向消息
        ActorRef sender = actorSystem.actorOf(SpringProps.create(actorSystem, Sender.class), "sender");
        SenderEvent event = new SenderEvent(this, IdUtil.idGen());
        event.setPath(this.get(identity).getInstanceId());
        sender.tell(event, ActorRef.noSender());
    }

    @Override
    public void doReady(String topic) {
        // 发布订阅模式的订阅者
        actorSystem.actorOf(SpringProps.create(actorSystem, Subscriber.class, topic, eventPublisher), "subscriber");
        // 点对点模式的接收者
        actorSystem.actorOf(SpringProps.create(actorSystem, Receiver.class, eventPublisher), "receiver");
    }
}
