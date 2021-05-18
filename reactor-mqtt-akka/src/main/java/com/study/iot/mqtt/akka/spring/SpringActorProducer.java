package com.study.iot.mqtt.akka.spring;

import akka.actor.AbstractActor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;

    private final Class<? extends AbstractActor> actorBeanClass;

    private final Object[] parameters;

    public SpringActorProducer(ApplicationContext applicationContext, Class<? extends AbstractActor> actorBeanClass, Object[] parameters) {
        this.applicationContext = applicationContext;
        this.actorBeanClass = actorBeanClass;
        this.parameters = parameters;
    }

    public SpringActorProducer(ApplicationContext applicationContext, Class<? extends AbstractActor> actorBeanClass) {
        this.applicationContext = applicationContext;
        this.actorBeanClass = actorBeanClass;
        this.parameters = null;
    }

    @Override
    public AbstractActor produce() {
        return applicationContext.getBean(actorBeanClass, parameters);
    }

    @Override
    public Class<? extends AbstractActor> actorClass() {
        return actorBeanClass;
    }
}
