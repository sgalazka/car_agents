package pl.edu.pw.elka.car_agents.actor;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSelection;
import akka.actor.Props;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

public class JunctionActor extends AbstractActorWithTimers {

    private AbstractActor.Receive busy;
    private AbstractActor.Receive empty;

    private Junction model;
    private int id;
    private Road currentRoadAllowed;
    private Iterator<Road> circularRoadIterator;

    private JunctionActor(Junction model, int id) {
        System.out.println(getSelf().path().address().toString());
        this.empty = receiveBuilder()
            .match(RequestJunctionDriveMsg.class, this::onRequestJunctionDrive)
            .match(CarActor.TickMsg.class, this::onTick)
            .build();

        this.busy = receiveBuilder()
            .match(ReleaseJunction.class, this::onReleaseJunction)
            .build();

        this.model = model;
        this.id = id;
        this.circularRoadIterator = Iterables.cycle(model.getRoads()).iterator();
        this.currentRoadAllowed = this.circularRoadIterator.next();
    }

    private void onTick(CarActor.TickMsg tickMsg) {
        ActorSelection actorSelection = getContext().getSystem().actorSelection("akka://car-agents/user/root/car*");
        actorSelection.tell(new JunctionClear(model.getId()), getSelf());


    }

    private void onReleaseJunction(ReleaseJunction releaseJunction) {
        System.out.println("junction released");
        getContext().become(empty);
    }

    public static Props props(int id, Junction model) {
        return Props.create(JunctionActor.class, () -> new JunctionActor(model, id));
    }

    @Override
    public void preStart() throws Exception {
        getTimers().startPeriodicTimer("TICK", new CarActor.TickMsg(),
            Duration.create(Configuration.MOVE_CAR_MILIS, TimeUnit.MILLISECONDS));
    }

    @Override
    public Receive createReceive() {
        return empty;
    }

    private void onRequestJunctionDrive(RequestJunctionDriveMsg msg) {
        System.out.println("I got message in juction onRequestJunctionDrive");
        getContext().become(busy);
        sender().tell(new DriveThroughJunction(model.getRoads()[getJunctionIndex(msg.direction)]), getSelf());
    }

    private int getJunctionIndex(CarDirection direction) {
        if (CarDirection.NORTH.equals(direction)) {
            return Junction.UP;
        } else if (CarDirection.SOUTH.equals(direction)) {
            return Junction.DOWN;
        } else if (CarDirection.WEST.equals(direction)) {
            return Junction.LEFT;
        }
        return Junction.RIGHT;
    }

    public static final NoEntry NO_ENTRY = new NoEntry();
    public static class NoEntry {}

    @AllArgsConstructor
    @Getter
    public static class JunctionClear {
        private Integer juntionId;
    }

    public static final ReleaseJunction RELEASE_JUNCTION = new ReleaseJunction();
    public static class ReleaseJunction {} {}
    @AllArgsConstructor
    public static class DriveThroughJunction {
        public Road road;
    }

    public static class RequestJunctionDriveMsg {
        public CarDirection direction;
        public Road road;

        public RequestJunctionDriveMsg(CarDirection carDirection) {
            this.direction = carDirection;
        }
    }
}
