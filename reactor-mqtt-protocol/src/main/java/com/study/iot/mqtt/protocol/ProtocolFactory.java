package com.study.iot.mqtt.protocol;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.proto.coap.CoapProtocol;
import com.study.iot.mqtt.protocol.proto.mqtt.MqttProtocol;
import com.study.iot.mqtt.protocol.proto.ws.WsProtocol;
import com.study.iot.mqtt.protocol.proto.xmpp.XmppProtocol;
import java.util.List;
import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:16
 */

public class ProtocolFactory {

    private final List<Protocol> protocols = Lists.newArrayList();

    public ProtocolFactory() {
        protocols.add(new MqttProtocol());
        protocols.add(new WsProtocol());
        protocols.add(new CoapProtocol());
        protocols.add(new XmppProtocol());
    }

    public void registry(Protocol protocol) {
        protocols.add(protocol);
    }

    public Optional<Protocol> getProtocol(ProtocolType protocolType) {
        return protocols.stream().filter(protocol -> protocol.support(protocolType)).findAny();
    }
}
