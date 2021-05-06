package com.study.iot.mqtt.protocol;

import java.util.List;

public interface ChannelManager {

     List<TransportConnection> getConnections();


     void  addConnections(TransportConnection connection);


     void removeConnections(TransportConnection connection);

     void addDeviceId(String deviceId, TransportConnection connection);

     void removeDeviceId(String deviceId);

     TransportConnection getRemoveDeviceId(String deviceId);

     boolean checkDeviceId(String deviceId);
}
