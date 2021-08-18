package com.study.iot.mqtt.protocol.ws;

import com.study.iot.mqtt.protocol.config.ServerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 11:04
 */

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WsServerProperties extends ServerProperties {

}
