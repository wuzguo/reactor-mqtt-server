package com.study.iot.mqtt.cluster.spring;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class SpringProps {

    public static Props create(ActorSystem actorSystem, String actorBeanName, Object... args) {
        return SpringExtension.instance().get(actorSystem).create(actorBeanName, args);
    }

    public static Props create(ActorSystem actorSystem, Class<? extends AbstractActor> actorBeanClass, Object... args) {
        return SpringExtension.instance().get(actorSystem).create(actorBeanClass, args);
    }

    public static Props create(ActorSystem actorSystem, String actorBeanName,
        Class<? extends AbstractActor> actorBeanClass) {
        return SpringExtension.instance().get(actorSystem).create(actorBeanName, actorBeanClass);
    }
}
