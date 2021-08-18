package com.study.iot.mqtt.protocol.proto.ws;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.Protocol;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import com.study.iot.mqtt.protocol.codec.MqttWebSocketCodec;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

public class WsProtocol implements Protocol {

    @Override
    public Boolean support(ProtocolType protocolType) {
        return protocolType == ProtocolType.WS;
    }

    @Override
    public ProtocolTransport getTransport() {
        return new WsTransport(this);
    }

    @Override
    public List<ChannelHandler> getHandlers() {
        return Lists.newArrayList(
            // 将请求和应答消息编码或解码为HTTP消息
            new HttpServerCodec(),
            // 将HTTP消息的多个部分合成一条完整的HTTP消息
            new HttpObjectAggregator(65536),
            // 将HTTP消息进行压缩编码
            new HttpContentCompressor(),
            // websocket handler
            new WebSocketServerProtocolHandler("/mqtt", "mqtt, mqttv3.1, mqttv3.1.1", true, 65536),
            new MqttWebSocketCodec(),
            new MqttDecoder(1024 * 1024), MqttEncoder.INSTANCE);
    }
}
