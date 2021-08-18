package com.study.iot.mqtt.client.strategy;

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
 * @date 2021/5/10 10:57
 */

@AllArgsConstructor
public class PublishStrategyContainer  implements ApplicationContextAware {

    private static final Map<String, Map<MqttQoS, Class<? extends PublishCapable>>> CONTAINER = Maps
        .newConcurrentMap();

    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Optional.of(applicationContext.getBeansWithAnnotation(PublishStrategyService.class))
            .ifPresent(annotationBeans -> annotationBeans.forEach((beanName, instance) -> {
                if (!PublishCapable.class.isAssignableFrom(instance.getClass())) {
                    throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface PublishStrategyCapable.", instance.getClass()));
                }

                Class<? extends PublishCapable> strategyClass = (Class<? extends PublishCapable>) instance.getClass();
                PublishStrategyService strategyService = strategyClass.getAnnotation(PublishStrategyService.class);

                String group = strategyService.group();
                Map<MqttQoS, Class<? extends PublishCapable>> storage = CONTAINER.get(group);
                if (storage == null) {
                    storage = Maps.newConcurrentMap();
                }

                MqttQoS value = strategyService.type();
                storage.putIfAbsent(value, strategyClass);
                CONTAINER.put(group, storage);
            }));
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param group {@link String}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link PublishCapable} 结果
     */
    public <T extends PublishCapable> T getStrategy(String group, MqttQoS value) {
        Map<MqttQoS, Class<? extends PublishCapable>> storage = CONTAINER.get(group);
        if (storage == null) {
            throw new BeanDefinitionValidationException(String.format("group '%s' not found in value container", group));
        }

        Class<? extends PublishCapable> strategy = storage.get(value);
        if (strategy == null) {
            throw new BeanDefinitionValidationException(String.format("value '%s' not found in value group '%s'", value, group));
        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link String}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link PublishCapable} 结果
     */
    public <T extends PublishCapable> T findStrategy(String group, MqttQoS value) {
        Map<MqttQoS, Class<? extends PublishCapable>> storage = CONTAINER.get(group);
        if (storage == null) {
            return null;
        }

        Class<? extends PublishCapable> strategy = storage.get(value);
        if (strategy == null) {
            return null;
        }

        return (T) applicationContext.getBean(strategy);
    }
}
