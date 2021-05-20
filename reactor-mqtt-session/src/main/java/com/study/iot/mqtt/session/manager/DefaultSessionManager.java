package com.study.iot.mqtt.session.manager;

import akka.actor.ActorSystem;
import com.google.common.collect.Lists;
import com.study.iot.mqtt.akka.actor.Subscriber;
import com.study.iot.mqtt.akka.spring.SpringProps;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import com.study.iot.mqtt.session.domain.BaseMessage;
import com.study.iot.mqtt.session.domain.ConnectSession;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.hbase.HbaseTemplate;
import com.study.iot.mqtt.store.container.ContainerManager;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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
    public ConnectSession create(String instanceId, String clientIdentity, Boolean isCleanSession) {
        ConnectSession session = ConnectSession.builder().instanceId(instanceId).clientIdentity(clientIdentity)
            .topics(Collections.emptyList()).build();
        // 如果是持久化 Session 需要放入Redis保存
        containerManager.take(CacheGroup.SESSION).add(clientIdentity, session);

        return session;
    }

    @Override
    public void add(String identity, BaseMessage message) {
        // 持久化
        List<Mutation> saveOrUpdates = Lists.newArrayList();
        // rowKey
        Put put = new Put(Bytes.toBytes(message.getId()));
        // 列族，列名，值
        ReflectionUtils.doWithFields(message.getClass(), field -> {
            field.setAccessible(true);
            if (!ObjectUtil.isNull(field.get(message))) {
                put.addColumn(Bytes.toBytes("message"), Bytes.toBytes(field.getName()),
                    Bytes.toBytes(String.valueOf(field.get(message))));
            }
        });
        saveOrUpdates.add(put);
        this.hbaseTemplate.saveOrUpdates("reactor-mqtt-message", saveOrUpdates);
    }

    @Override
    public void subscribe(String topic) {
        actorSystem.actorOf(SpringProps.create(actorSystem, Subscriber.class, topic, eventPublisher), "subscriber");
    }
}
