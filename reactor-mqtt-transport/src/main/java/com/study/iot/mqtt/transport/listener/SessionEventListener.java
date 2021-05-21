package com.study.iot.mqtt.transport.listener;

import com.study.iot.mqtt.akka.event.SessionEvent;
import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.hbase.HbaseTemplate;
import com.study.iot.mqtt.store.mapper.SessionMessageRowMapper;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyContainer;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:32
 */

@Slf4j
@Component
public class SessionEventListener {

    @Autowired
    private PublishStrategyContainer strategyContainer;

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @EventListener
    public void listen(SessionEvent event) {
        log.info("receive subscribe event info: {}", event);
        // 查询连接信息
        Optional.ofNullable(containerManager.take(CacheGroup.ID_TOPIC).list(event.getTopic()))
            .orElse(Collections.emptyList())
            .forEach(identity -> {
                DisposableConnection disposableConnection = (DisposableConnection) containerManager
                    .take(CacheGroup.CHANNEL).get(String.valueOf(identity));
                // 如果有连接就执行下面的逻辑
                if (!ObjectUtil.isNull(disposableConnection)) {
                    // 获取消息体
                    SessionMessage sessionMessage = hbaseTemplate.get("reactor-mqtt-message",
                        event.getRow(), new SessionMessageRowMapper());
                    // 又来一个策略模式
                    Optional.ofNullable(strategyContainer.findStrategy(StrategyGroup.SERVER_PUBLISH,
                        MqttQoS.valueOf(sessionMessage.getQos())))
                        .ifPresent(
                            capable -> ((PublishStrategyCapable) capable).handle(disposableConnection, sessionMessage));
                }
            });
    }
}
