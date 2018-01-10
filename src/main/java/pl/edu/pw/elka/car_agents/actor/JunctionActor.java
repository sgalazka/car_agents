package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.Props;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;

public class JunctionActor extends AbstractActorWithTimers {

    private Junction model;
    private int id;

    private JunctionActor(Junction model, int id) {
        this.model = model;
        this.id = id;
    }

    public static Props props(int id, Junction model) {
        return Props.create(JunctionActor.class, () -> new JunctionActor(model, id));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestJunctionDriveMsg.class, this::onRequestJunctionDrive)
                .build();
    }

    private void onRequestJunctionDrive(RequestJunctionDriveMsg msg) {
        // TODO: 2017-12-26 Zrobić poprawną odpowiedź zależną od świateł

    }

    public static class RequestJunctionDriveMsg {
        public CarDirection direction;
    }
}
