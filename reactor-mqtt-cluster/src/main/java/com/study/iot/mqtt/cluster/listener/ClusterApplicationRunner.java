package com.study.iot.mqtt.cluster.listener;

import akka.actor.ActorSystem;
import akka.routing.RoundRobinPool;
import com.study.iot.mqtt.cluster.spring.SpringProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 16:20
 */
@Component
public class ClusterApplicationRunner implements ApplicationRunner {

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create an actor that handles cluster domain events
        actorSystem.actorOf(SpringProps.create(actorSystem, ClusterListener.class)
            .withDispatcher("processor-dispatcher")
            .withRouter(new RoundRobinPool(10)), "clusterListener");
    }
}
