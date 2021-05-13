package com.study.iot.mqtt.cluster.listener;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import com.study.iot.mqtt.cluster.annotation.ActorBean;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 15:51
 */

@ActorBean
@Slf4j
public class ClusterListener extends AbstractActor {

    private final Cluster cluster;

    private ClusterListener(ActorSystem actorSystem) {
        this.cluster = Cluster.get(actorSystem);
    }

    static Props props(ActorSystem actorSystem) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(ClusterListener.class, () -> new ClusterListener(actorSystem));
    }

    @Override
    public void preStart() throws Exception, Exception {
        cluster.subscribe(self(), MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception, Exception {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(MemberUp.class, mUp -> {
                log.info("Member is Up: {}", mUp.member());
            })
            .match(UnreachableMember.class, mUnreachable -> {
                log.info("Member detected as unreachable: {}", mUnreachable.member());
            })
            .match(MemberRemoved.class, mRemoved -> {
                log.info("Member is Removed: {}", mRemoved.member());
            })
            .match(MemberEvent.class, message -> {
                // ignore
            }).build();
    }
}
