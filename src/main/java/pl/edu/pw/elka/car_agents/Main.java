package pl.edu.pw.elka.car_agents;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import pl.edu.pw.elka.car_agents.actor.RootActor;

public class Main {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("car-agents");
        ActorRef root = system.actorOf(RootActor.props(), "root");

    }
}
