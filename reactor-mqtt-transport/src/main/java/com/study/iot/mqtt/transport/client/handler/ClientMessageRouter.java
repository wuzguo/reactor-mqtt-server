package com.study.iot.mqtt.transport.client.handler;


import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ClientConfig;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ClientMessageRouter {


    private final ClientConfig config;

    private final DirectHandlerAdaptor directHandlerAdaptor;

    public ClientMessageRouter(ClientConfig config) {
        this.config = config;
        this.directHandlerAdaptor = DirectHandlerFactory::new;
    }

    public void handler(MqttMessage message, TransportConnection connection) {
        if (message.decoderResult().isSuccess()) {
            DirectHandler handler = directHandlerAdaptor.handler(message.fixedHeader().messageType()).loadHandler();
            log.info("accept message  info{}", message);
            handler.handler(message, connection, config);
        } else {
            log.error("accept message  error{}", message.decoderResult().toString());
        }
    }

}
