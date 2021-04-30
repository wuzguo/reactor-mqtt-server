package com.study.iot.mqtt.protocal;


import java.util.function.Consumer;

public interface ConnectConfiguration {

    void setIp(String ip);

    void setPort(int port);

    String getIp();

    int getPort();

    String getProtocol();

    boolean isSsl();

    boolean isLog();

    int  getHeart();

    Consumer<Throwable> getThrowableConsumer();

    void checkConfig();

    boolean isKeepAlive();
    
    boolean isNoDelay();
    
    int getSendBufSize();
    
    int getRevBufSize();
    
    int getBacklog();
}
