package com.study.iot.mqtt.transport.server.handler;


import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ServerConfig;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ServerMessageRouter {


    private final ServerConfig config;

    private final DirectHandlerAdaptor directHandlerAdaptor;

    public ServerMessageRouter(ServerConfig config) {
        this.config=config;
        this.directHandlerAdaptor= DirectHandlerFactory::new;
    }

    public void handler(MqttMessage message, TransportConnection connection) {
        if(message.decoderResult().isSuccess()){
            log.info("accept message channel {} info {}",connection.getConnection(),message);
            DirectHandler handler=directHandlerAdaptor.handler(message.fixedHeader().messageType()).loadHandler();
             handler.handler(message,connection,config);
        }
        else {
            log.error("accept message  error{}",message.decoderResult().toString());
        }
    }

}
