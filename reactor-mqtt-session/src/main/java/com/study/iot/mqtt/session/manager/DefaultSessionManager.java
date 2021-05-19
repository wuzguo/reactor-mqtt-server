package com.study.iot.mqtt.session.manager;

import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Subscriber;
import com.study.iot.mqtt.akka.spring.SpringProps;
import com.study.iot.mqtt.session.domain.ConnectSession;
import com.study.iot.mqtt.store.mapper.StoreMapper;
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
    private StoreMapper storeMapper;

    @Override
    public ConnectSession create(String instanceId, String clientIdentity, Boolean isCleanSession) {
        ConnectSession session = ConnectSession.builder().instanceId(instanceId).clientIdentity(clientIdentity)
            .topics(Collections.emptyList()).build();
        // 如果是持久化 Session 需要放入Redis保存
        storeMapper.session().add(clientIdentity, session);

        return session;
    }

    @Override
    public void subscribe(String topic) {
        actorSystem.actorOf(SpringProps.create(actorSystem, Subscriber.class, topic, eventPublisher), "subscriber");
    }
}
