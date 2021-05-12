package com.study.iot.mqtt.transport.constant;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:45
 */

public interface MetricName {

    /**
     * 总的连接数
     */
    String TOTAL_CONNECTION_COUNT = "totalConnectionCount";

    /**
     * 当前连接数
     */
    String CURRENT_CONNECTION_COUNT = "currentConnectionCount";

    /**
     * 总的发布数量
     */
    String TOTAL_PUBLISH_COUNT = "totalPublishCount";

    /**
     * 总的接收数量
     */
    String TOTAL_RECEIVE_COUNT = "totalReceiveCount";

    /**
     * 总的发布的字节数
     */
    String TOTAL_PUBLISH_BYTES = "totalPublishBytes";

    /**
     * 总的接收的字节数
     */
    String TOTAL_RECEIVE_BYTES = "totalReceiveBytes";
}
