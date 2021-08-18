package com.study.iot.mqtt.transport.strategy;

import com.google.common.collect.Maps;
import io.netty.handler.codec.mqtt.MqttMessageType;
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
public class StrategyContainer implements ApplicationContextAware {

    private static final Map<String, Map<StrategyEnum, Class<? extends StrategyCapable>>> CONTAINER = Maps.newConcurrentMap();

    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Optional.of(applicationContext.getBeansWithAnnotation(StrategyService.class))
            .ifPresent(annotationBeans -> annotationBeans.forEach((beanName, instance) -> {
                if (!StrategyCapable.class.isAssignableFrom(instance.getClass())) {
                    throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface StrategyCapable.", instance.getClass()));
                }

                Class<? extends StrategyCapable> strategyClass = (Class<? extends StrategyCapable>) instance.getClass();
                StrategyService strategyService = strategyClass.getAnnotation(StrategyService.class);

                String group = strategyService.group();
                Map<StrategyEnum, Class<? extends StrategyCapable>> storage = CONTAINER.get(group);
                if (storage == null) {
                    storage = Maps.newConcurrentMap();
                }

                StrategyEnum value = strategyService.type();
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
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T getStrategy(String group, StrategyEnum value) {
        Map<StrategyEnum, Class<? extends StrategyCapable>> storage = CONTAINER.get(group);
        if (storage == null) {
            throw new BeanDefinitionValidationException(String
                .format("group '%s' not found in value container", group));

        }

        Class<? extends StrategyCapable> strategy = storage.get(value);
        if (strategy == null) {
            throw new BeanDefinitionValidationException(String
                .format("value '%s' not found in value group '%s'", value, group));

        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link String}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T findStrategy(String group, StrategyEnum value) {
        Map<StrategyEnum, Class<? extends StrategyCapable>> storage = CONTAINER.get(group);
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
