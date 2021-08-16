package com.study.iot.mqtt.akka.spring;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

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
