package com.study.iot.mqtt.akka.config;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import com.study.iot.mqtt.akka.spring.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 14:29
 */

@Configuration
@EnableConfigurationProperties(AkkaProperties.class)
public class AkkaAutoConfiguration {

    @Autowired
    private AkkaProperties akkaProperties;

    @Bean(destroyMethod = "terminate")
    @ConditionalOnMissingBean
    public ActorSystem actorSystem(ApplicationContext applicationContext) {
        ActorSystem actorSystem = ActorSystem.create(akkaProperties.getSystemName(), akkaProperties.getConfig());
        SpringExtension.instance().get(actorSystem).setApplicationContext(applicationContext);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Cluster cluster = Cluster.get(actorSystem);
                cluster.leave(cluster.selfAddress());
            })
        );
        return actorSystem;
    }
}
