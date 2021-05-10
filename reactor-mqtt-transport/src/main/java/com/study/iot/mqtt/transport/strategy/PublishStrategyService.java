package com.study.iot.mqtt.transport.strategy;

import io.netty.handler.codec.mqtt.MqttQoS;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

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
public @interface PublishStrategyService {

    @AliasFor(value = "value", annotation = Service.class)
    String value() default "";

    /**
     * 策略
     *
     * @return {@link MqttQoS}
     */
    MqttQoS type();

    /**
     * 策略组
     *
     * @return {@link Group}
     */
    String group();
}
