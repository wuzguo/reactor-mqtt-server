package com.study.iot.mqtt.protocol.mqtt;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.Protocol;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

import java.util.List;


public class MqttProtocol implements Protocol {

    @Override
    public Boolean support(ProtocolType protocolType) {
        return protocolType == ProtocolType.MQTT;
    }

    @Override
    public ProtocolTransport getTransport() {
        return new MqttTransport(this);
    }

    @Override
    public List<ChannelHandler> getHandlers() {
        return Lists.newArrayList(new MqttDecoder(5 * 1024 * 1024), MqttEncoder.INSTANCE);
    }
}
