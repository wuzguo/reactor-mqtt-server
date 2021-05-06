package com.study.iot.mqtt.transport.strategy;

import io.netty.handler.codec.mqtt.MqttMessageType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:14
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface StrategyService {

    @AliasFor(value = "value", annotation = Service.class)
    String value() default "";

    /**
     * 策略
     *
     * @return {@link MqttMessageType}
     */
    MqttMessageType type();

    /**
     * 策略组
     * @return {@link MqttMessageType}
     */
    String group();
}
