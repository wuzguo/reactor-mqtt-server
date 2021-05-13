package com.study.iot.mqtt.cluster.listener;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.study.iot.mqtt.cluster.annotation.ActorBean;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 15:51
 */

@ActorBean
public class ClusterListener extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() throws Exception, Exception {
        Cluster.get(getContext().system()).subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
            MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception, Exception {
        Cluster.get(getContext().system()).unsubscribe(getSelf());
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
            })
            .build();
    }
}
