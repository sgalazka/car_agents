package pl.edu.pw.elka.car_agents;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import pl.edu.pw.elka.car_agents.actor.RootActor;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;

import java.util.concurrent.CompletableFuture;

public class Main {


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("car-agents");
        ActorRef root = system.actorOf(RootActor.props(), "root");
        RootActor.StartSystemMsg startSystemMsg = new RootActor.StartSystemMsg();
        startSystemMsg.roadNetwork = RoadNetwork.getInstance();
        startSystemMsg.carCount = 2;
        CompletableFuture<Object> future = PatternsCS.ask(root, startSystemMsg, 1000 * 20).toCompletableFuture();
    }
}
