package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.Props;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.map.Signpost;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.util.JunctionUtils;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

import java.util.LinkedList;
import java.util.Queue;
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
    private int currentLane; /*numerowane od 1*/
    private int speed = Configuration.CAR_SPEED;
    private float distanceToRoadEnd;
    private Queue<Signpost> signposts;

    private CarActor(int id, Junction entrance, Junction exit) throws IllegalArgumentException {

        this.id = id;
        this.entrance = entrance;
        this.exit = exit;
        this.x = getStartX();
        this.y = getStartY();
        this.currentRoad = JunctionUtils.getRoadForInOutJunction(entrance);
        this.currentDirection = getStartDirection(entrance.getCenterCoordinates());
        this.currentLane = currentRoad.getOneDirectionNumberOfLanes();
        distanceToRoadEnd = getDistanceToEndOfRoad() - Configuration.CAR_HEIGHT / 2;
        signposts = new LinkedList<Signpost>(RoadNetwork.getInstance().getPath(
                JunctionUtils.getRoadForInOutJunction(entrance),
                JunctionUtils.getRoadForInOutJunction(exit)));
    }

    public static Props props(int id, Junction entrance, Junction exit) {
        return Props.create(CarActor.class, () -> new CarActor(id, entrance, exit));
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
        if (!isEndOfRoad()) {
            move();
        } else {
            requestJunctionDrive();
            getTimers().cancel(TICK);
        }
    }

    private void requestJunctionDrive() {
        // TODO: 2018-01-10 wiadomośc do konkretnego skrzyżowania o identyfikatorze "juntcionId"
//        ActorSelection actorSelection = getContext().actorSelection("../root/junction"+junctionId);
//        actorSelection.tell(new CarActor.GetCoordinatesRequest(), getSelf());
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

        finalCoordinates = getRoadFinalCoordinates(entryCoordinates, junctions);

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

    private Coordinates getRoadFinalCoordinates(Coordinates entryCoordinates, Junction[] junctions) {
        Coordinates finalCoordinates = null;
        if (entryCoordinates.equals(junctions[0].getCenterCoordinates())) {
            finalCoordinates = JunctionUtils.getBorderForRoadOnJunction(junctions[1], currentRoad);
        } else if (entryCoordinates.equals(junctions[1].getCenterCoordinates())) {
            finalCoordinates = JunctionUtils.getBorderForRoadOnJunction(junctions[0], currentRoad);
        }
        if (finalCoordinates == null)
            throw new IllegalArgumentException("Wrong entry coordinates");
        return finalCoordinates;
    }

    private float getDistanceToEndOfRoad() {
        Junction[] junctions = JunctionUtils.getJunctionsForRoad(currentRoad);
        Coordinates[] endsOfRoadCoordinates = new Coordinates[2];
        if (junctions.length < 2)
            throw new IllegalArgumentException("Road has no end");
        endsOfRoadCoordinates[0] = JunctionUtils.getBorderForRoadOnJunction(junctions[0], currentRoad);
        endsOfRoadCoordinates[1] = JunctionUtils.getBorderForRoadOnJunction(junctions[1], currentRoad);
        for (Coordinates endsOfRoadCoordinate : endsOfRoadCoordinates) {
            System.out.println("endsOfRoadCoordinate " + endsOfRoadCoordinate.toString());
        }
        if (endsOfRoadCoordinates[0].getX() == endsOfRoadCoordinates[1].getX()) {
            return Math.abs(endsOfRoadCoordinates[0].getY() - endsOfRoadCoordinates[1].getY());
        } else if (endsOfRoadCoordinates[0].getY() == endsOfRoadCoordinates[1].getY()) {
            return Math.abs(endsOfRoadCoordinates[0].getX() - endsOfRoadCoordinates[1].getX());
        }
        throw new IllegalArgumentException("Junctions has none equal coordinate");
    }

    private void move() {
        float distance = speed * Configuration.MOVE_CAR_MILIS / 1000;
        distanceToRoadEnd -= distance;
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

    private float getStartX() {
        Coordinates centerCoordinates = entrance.getCenterCoordinates();
        for (int i = 0; i < entrance.getRoads().length; i++) {
            int oneDirectionNumberOfLanes = entrance.getRoads()[i].getOneDirectionNumberOfLanes();
            if (oneDirectionNumberOfLanes > 0) {
                if (i == Junction.UP) {
                    return centerCoordinates.getX() + Configuration.LANE_WIDTH * oneDirectionNumberOfLanes - Configuration.CAR_WIDTH / 2;
                } else if (i == Junction.DOWN) {
                    return centerCoordinates.getX() - Configuration.LANE_WIDTH * oneDirectionNumberOfLanes + Configuration.CAR_WIDTH / 2;
                } else {
                    return centerCoordinates.getX();
                }
            }
        }
        throw new IllegalArgumentException("Junction with no lanes");
    }

    private float getStartY() {
        Coordinates centerCoordinates = entrance.getCenterCoordinates();
        for (int i = 0; i < entrance.getRoads().length; i++) {
            int oneDirectionNumberOfLanes = entrance.getRoads()[i].getOneDirectionNumberOfLanes();
            if (oneDirectionNumberOfLanes > 0) {
                if (i == Junction.RIGHT) {
                    return centerCoordinates.getY() + Configuration.LANE_WIDTH * oneDirectionNumberOfLanes - Configuration.CAR_WIDTH / 2;
                } else if (i == Junction.LEFT) {
                    return centerCoordinates.getY() - Configuration.LANE_WIDTH * oneDirectionNumberOfLanes + Configuration.CAR_WIDTH / 2;
                } else {
                    return centerCoordinates.getY();
                }
            }
        }
        throw new IllegalArgumentException("Junction with no lanes");
    }

    private boolean isEndOfRoad() {
        return distanceToRoadEnd <= 5;
    }

    public static class StartMsg {
    }

    public static class TickMsg {

    }

    public static class GetCoordinatesRequest {

    }
}
