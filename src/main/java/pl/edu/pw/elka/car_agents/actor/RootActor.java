package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;

public class RootActor extends AbstractActor {

    public static Props props() {
        return Props.create(RootActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartSystemMsg.class, this::startSystem)
                .build();
    }

    private void startSystem(StartSystemMsg msg) {

    }

    public static class StartSystemMsg {
        RoadNetwork roadNetwork;
        int carCount;
    }
}
