package com.study.iot.mqtt.protocol;


import java.util.function.Consumer;

public interface ConnectConfiguration {

    String getIp();

    void setIp(String ip);

    int getPort();

    void setPort(int port);

    String getProtocol();

    boolean isSsl();

    boolean isLog();

    int getHeart();

    Consumer<Throwable> getThrowableConsumer();

    void checkConfig();

    boolean isKeepAlive();

    boolean isNoDelay();

    int getSendBufSize();

    int getRevBufSize();

    int getBacklog();
}
