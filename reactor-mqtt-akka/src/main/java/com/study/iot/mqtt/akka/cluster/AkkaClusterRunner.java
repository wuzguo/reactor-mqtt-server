package com.study.iot.mqtt.akka.cluster;

import akka.actor.ActorSystem;
import akka.routing.RoundRobinPool;
import com.study.iot.mqtt.akka.spring.SpringProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 16:20
 */

@Order(2)
@Component
public class AkkaClusterRunner implements ApplicationRunner {

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create an actor that handles cluster domain events
        actorSystem.actorOf(SpringProps.create(actorSystem, ClusterListener.class)
            .withDispatcher("cluster-dispatcher")
            .withRouter(new RoundRobinPool(10)), "clusterListener");
    }
}
