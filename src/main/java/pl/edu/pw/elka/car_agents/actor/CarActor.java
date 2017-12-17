package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.Props;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.util.JunctionUtils;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CarActor extends AbstractActorWithTimers {

    private final static Object TICK = "TICK";
    private int id;
    private Junction entrance;
    private Junction exit;
    private float x;
    private float y;
    private CarDirection currentDirection;
    private Road currentRoad;
    private int speed = Configuration.CAR_SPEED;

    private CarActor(int id, Junction entrance, Junction exit) throws IllegalArgumentException {

        this.id = id;
        this.entrance = entrance;
        this.exit = exit;
        this.x = entrance.getCenterCoordinates().getX();
        this.y = entrance.getCenterCoordinates().getY();
        this.currentRoad = getCurrentRoad(entrance);
        this.currentDirection = getStartDirection(entrance.getCenterCoordinates());
//        ActorSelection selection = getContext().actorSelection("akka://car-agents/user/root/car");
//        selection.tell(new Identify(id), getSelf());
    }

    public static Props props(int id, Junction entrance, Junction exit) {
        return Props.create(CarActor.class, () -> new CarActor(id, entrance, exit));
    }

    private Road getCurrentRoad(Junction entrance) {
        for (Road road : entrance.getRoads()) {
            if (road != null && road.getOneDirectionNumberOfLanes() > 0)
                return road;
        }
        throw new IllegalArgumentException("No road in entry junction");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartMsg.class, this::onStartMsg)
                .match(TickMsg.class, this::onTick)
                .match(GetCoordinatesRequest.class, this::onGetCoordinatesRequest)
                .build();
    }

    private void onStartMsg(StartMsg msg) {
        getTimers().startPeriodicTimer(TICK, new TickMsg(),
                Duration.create(Configuration.MOVE_CAR_MILIS, TimeUnit.MILLISECONDS));
    }

    private void onTick(TickMsg msg) {
        move();
    }

    private void onGetCoordinatesRequest(GetCoordinatesRequest request) {
        RootActor.GetCoordinatesResponce responce = new RootActor.GetCoordinatesResponce();
        CarCoordinates carCoordinates = new CarCoordinates();
        carCoordinates.setCarId(this.id);
        carCoordinates.setDirection(this.currentDirection);
        carCoordinates.setCoordinates(new Coordinates((int) x, (int) y));
        responce.coordinates = carCoordinates;
        getSender().tell(responce, getSelf());
    }

    private CarDirection getStartDirection(Coordinates entryCoordinates) throws IllegalArgumentException {
        Coordinates finalCoordinates = null;
        Junction[] junctions = JunctionUtils.getJunctionsForRoad(currentRoad);

        if (entryCoordinates.equals(junctions[0].getCenterCoordinates())) {
            finalCoordinates = JunctionUtils.getBorderForRoadOnJunction(junctions[1], currentRoad);
        } else if (entryCoordinates.equals(junctions[1].getCenterCoordinates())) {
            finalCoordinates = JunctionUtils.getBorderForRoadOnJunction(junctions[0], currentRoad);
        }
        if (finalCoordinates == null)
            throw new IllegalArgumentException("Wrong entry coordinates");

        System.out.println("junctions[0]: " + junctions[0].toString());
        System.out.println("junctions[1]: " + junctions[1].toString());
        System.out.println("entryCoordinates: " + entryCoordinates.toString());
        System.out.println("finalCoordinates: " + finalCoordinates.toString());

        int startX = entryCoordinates.getX();
        int startY = entryCoordinates.getY();
        int endX = finalCoordinates.getX();
        int endY = finalCoordinates.getY();
        if (startX < endX && startY == endY) {
            return CarDirection.EAST;
        } else if (startX > endX && startY == endY) {
            return CarDirection.WEST;
        } else if (startY < endY && startX == endX) {
            return CarDirection.NORTH;
        } else if (startY > endY && startX == endX) {
            return CarDirection.SOUTH;
        } else {
            throw new IllegalArgumentException("Wrong start and end coordinates");
        }
    }

    private void move() {
        float distance = speed * Configuration.MOVE_CAR_MILIS / 1000;
//        System.out.println("distance: " + distance);
        if (CarDirection.NORTH == currentDirection) {
            y += distance;
        } else if (CarDirection.EAST == currentDirection) {
            x += distance;
        } else if (CarDirection.SOUTH == currentDirection) {
            y -= distance;
        } else if (CarDirection.WEST == currentDirection) {
            x -= distance;
        }
//        System.out.println("x: " + x + ", y: " + y);
    }

    public static class StartMsg {
    }

    public static class TickMsg {

    }

    public static class GetCoordinatesRequest {

    }
}
