package com.study.iot.mqtt.transport.strategy;

import com.google.common.collect.Maps;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:13
 */

@AllArgsConstructor
public class WillStrategyContainer implements ApplicationContextAware {

    private static final Map<String, Map<MqttQoS, Class<? extends WillCapable>>> container = Maps.newConcurrentMap();

    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Optional.of(applicationContext.getBeansWithAnnotation(WillStrategyService.class))
            .ifPresent(annotationBeans -> annotationBeans.forEach((k, v) -> {
                if (!WillCapable.class.isAssignableFrom(v.getClass())) {
                    throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface WillCapable.", v.getClass()));
                }

                Class<? extends WillCapable> strategyClass = (Class<? extends WillCapable>) v.getClass();
                WillStrategyService WillStrategyService = strategyClass.getAnnotation(WillStrategyService.class);

                String group = WillStrategyService.group();
                Map<MqttQoS, Class<? extends WillCapable>> storage = container.get(group);
                if (storage == null) {
                    storage = Maps.newConcurrentMap();
                }

                MqttQoS value = WillStrategyService.type();
                storage.putIfAbsent(value, strategyClass);
                container.put(group, storage);
            }));
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param group {@link String}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link WillCapable} 结果
     */
    public <T extends WillCapable> T getStrategy(String group, MqttQoS value) {
        Map<MqttQoS, Class<? extends WillCapable>> storage = container.get(group);
        if (storage == null) {
            throw new BeanDefinitionValidationException(String
                .format("WillStrategyService group '%s' not found in value container", group));

        }

        Class<? extends WillCapable> strategy = storage.get(value);
        if (strategy == null) {
            throw new BeanDefinitionValidationException(String
                .format("WillStrategyService value '%s' not found in value group '%s'", value, group));

        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link String}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link WillCapable} 结果
     */
    public <T extends WillCapable> T findStrategy(String group, MqttQoS value) {
        Map<MqttQoS, Class<? extends WillCapable>> storage = container.get(group);
        if (storage == null) {
            return null;
        }

        Class<? extends WillCapable> strategy = storage.get(value);
        if (strategy == null) {
            return null;
        }

        return (T) applicationContext.getBean(strategy);
    }
}
