package com.study.iot.mqtt.transport.listener;

import com.study.iot.mqtt.akka.event.SenderEvent;
import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.hbase.HbaseTemplate;
import com.study.iot.mqtt.store.mapper.SessionMessageRowMapper;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyContainer;
import io.netty.handler.codec.mqtt.MqttQoS;
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
 * @date 2021/5/21 9:32
 */

@Slf4j
@Component
public class SenderEventListener {

    @Autowired
    private PublishStrategyContainer strategyContainer;

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @EventListener
    public void listen(SenderEvent event) {
        log.info("receive sender event info: {}", event);
        // 查询连接信息
        DisposableConnection disposableConnection = (DisposableConnection) containerManager.take(CacheGroup.CHANNEL)
            .get(event.getIdentity());
        // 获取消息体
        SessionMessage sessionMessage = hbaseTemplate.get("reactor-mqtt-message", event.getRow(),
            new SessionMessageRowMapper());
        // 又来一个策略模式
        Optional.ofNullable(
            strategyContainer.findStrategy(StrategyGroup.SERVER_PUBLISH, MqttQoS.valueOf(sessionMessage.getQos())))
            .ifPresent(capable -> ((PublishStrategyCapable) capable).handle(disposableConnection, sessionMessage));
    }
}
