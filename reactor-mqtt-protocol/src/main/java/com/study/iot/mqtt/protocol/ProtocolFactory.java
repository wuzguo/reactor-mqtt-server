package com.study.iot.mqtt.protocol;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.mqtt.MqttProtocol;
import com.study.iot.mqtt.protocol.ws.WsProtocol;

import java.util.List;
import java.util.Optional;

public class ProtocolFactory {

    private List<Protocol> protocols = Lists.newArrayList();

    public ProtocolFactory() {
        protocols.add(new MqttProtocol());
        protocols.add(new WsProtocol());
    }

    public void registryProtocl(Protocol protocol) {
        protocols.add(protocol);
    }

    public Optional<Protocol> getProtocol(ProtocolType protocolType) {
        return protocols.stream().filter(protocol -> protocol.support(protocolType)).findAny();
    }
}
