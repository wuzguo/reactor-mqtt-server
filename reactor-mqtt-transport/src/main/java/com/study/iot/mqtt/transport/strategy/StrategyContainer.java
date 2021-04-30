package com.study.iot.mqtt.transport.strategy;

import com.google.common.collect.Maps;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:13
 */

@AllArgsConstructor
public class StrategyContainer implements ApplicationContextAware {

    private final ApplicationContext applicationContext;

    private static final Map<String, Map<MqttMessageType, Class<? extends StrategyCapable>>> container = Maps.newConcurrentMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Map<String, Object> annotationBeans = applicationContext.getBeansWithAnnotation(StrategyService.class);
        container.clear();
        annotationBeans.forEach((k, v) -> {

            if (!StrategyCapable.class.isAssignableFrom(v.getClass())) {
                throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface StrategyCapable.", v.getClass()));
            }

            Class<? extends StrategyCapable> strategyClass = (Class<? extends StrategyCapable>) v.getClass();
            StrategyService strategyService = strategyClass.getAnnotation(StrategyService.class);

            String group = strategyService.group();
            Map<MqttMessageType, Class<? extends StrategyCapable>> storage = container.get(group);
            if (storage == null) {
                storage = Maps.newConcurrentMap();
            }

            MqttMessageType value = strategyService.type();
            storage.putIfAbsent(value, strategyClass);
            container.put(group, storage);
        });
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param group {@link Group}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T getStrategy(String group, MqttMessageType value) {
        Map<MqttMessageType, Class<? extends StrategyCapable>> storage = container.get(group);
        if (storage == null) {
            throw new BeanDefinitionValidationException(String
                    .format("StrategyService group '%s' not found in value container", group));

        }

        Class<? extends StrategyCapable> strategy = storage.get(value);
        if (strategy == null) {
            throw new BeanDefinitionValidationException(String
                    .format("StrategyService value '%s' not found in value group '{}'", value, group));

        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link Group}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T findStrategy(String group, MqttMessageType value) {
        Map<MqttMessageType, Class<? extends StrategyCapable>> storage = container.get(group);
        if (storage == null) {
            return null;
        }

        Class<? extends StrategyCapable> strategy = storage.get(value);
        if (strategy == null) {
            return null;
        }

        return (T) applicationContext.getBean(strategy);
    }

}
