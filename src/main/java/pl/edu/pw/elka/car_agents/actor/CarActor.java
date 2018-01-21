package pl.edu.pw.elka.car_agents.actor;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSelection;
import akka.actor.Props;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.Signpost;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.util.JunctionUtils;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

public class CarActor extends AbstractActorWithTimers {

    private AbstractActor.Receive waitingForJunction;
    private AbstractActor.Receive drive;
    private AbstractActor.Receive stopedByCarInFrontOfMe;

    private final static Object TICK = "TICK";
    private int id;
    private Junction entrance;
    private Junction exit;
    private float x;
    private float y;
    private CarDirection currentDirection;
    private Road currentRoad;
    private int currentLane; /*numerowane od 1*/
    private int speed;
    private int initSpeed;
    private float distanceToRoadEnd;
    private Queue<Signpost> signposts;
    private Junction nextJunction;

    private CarActor(int id, Junction entrance, Junction exit, Integer speed, Queue<Signpost> signposts) throws IllegalArgumentException {
        System.out.println(getSelf().path());
        System.out.println(getSelf().path().address().toString());
        this.waitingForJunction = receiveBuilder()
            .match(JunctionActor.JunctionClear.class, this::onJunctionClear)
            .match(JunctionActor.DriveThroughJunction.class, this::onDriveThroughJuntion)
            .match(TickMsg.class, this::onTick)
            .build();
        this.drive = receiveBuilder()
            .match(StartMsg.class, this::onStartMsg)
            .match(TickMsg.class, this::onTick)
            .match(GetCoordinatesRequest.class, this::onGetCoordinatesRequest)
            .match(RootActor.GetCoordinatesResponse.class, this::onGetCoordinatesResponse)
            .build();

        this.stopedByCarInFrontOfMe = receiveBuilder()
            .match(TickMsg.class, this::onTickWhenStopped)
            .build();

        this.id = id;
        this.entrance = entrance;
        this.exit = exit;
        this.currentRoad = JunctionUtils.getRoadForInOutJunction(entrance);
        this.currentDirection = getStartDirection(entrance.getCenterCoordinates());
        this.x = getStartX(this.entrance);
        this.y = getStartY(this.entrance);
        this.currentLane = currentRoad.getOneDirectionNumberOfLanes();
        this.speed = speed;
        this.initSpeed = speed;
        // FIXME: 20.01.18 to nie powinno być tak
        this.nextJunction = Arrays.stream(JunctionUtils.getJunctionsForRoad(this.currentRoad))
            .filter(junction -> junction != entrance)
            .findFirst()
            .orElseThrow(RuntimeException::new);
        distanceToRoadEnd = getDistanceToEndOfRoad() - Configuration.CAR_HEIGHT / 2;
        this.signposts = signposts;
    }

    private void onTickWhenStopped(TickMsg tickMsg) {

    }

    private void onDriveThroughJuntion(JunctionActor.DriveThroughJunction driveThroughJunction) {
        this.signposts.remove();
        this.currentRoad = driveThroughJunction.road;
        Junction junction1 = Arrays.stream(JunctionUtils.getJunctionsForRoad(this.currentRoad))
            .filter(junction -> junction != this.nextJunction)
            .findFirst().get();
        this.currentDirection = getStartDirection(this.nextJunction.getCenterCoordinates());
        this.x = getStartX(nextJunction);
        this.y = getStartY(nextJunction);
        this.nextJunction = junction1;
        this.distanceToRoadEnd = getDistanceToEndOfRoad() - Configuration.CAR_HEIGHT / 2;
        getContext().become(drive);
        getContext().getSystem().scheduler().scheduleOnce(Duration.create(1, TimeUnit.SECONDS),
            sender(), new JunctionActor.ReleaseJunction(), context().system().dispatcher(), null);
        this.speed = initSpeed;
    }

    private void onJunctionClear(JunctionActor.JunctionClear junctionClear) {
        if (junctionClear.getJuntionId().equals(nextJunction.getId())) {
            sender().tell(new JunctionActor.RequestJunctionDriveMsg(signposts.element().getDirection()), getSelf());
        }
    }

    public static Props props(int id, Junction entrance, Junction exit, Integer speed, Queue<Signpost> signpost) {
        return Props.create(CarActor.class, () -> new CarActor(id, entrance, exit, speed, signpost));
    }

    @Override
    public Receive createReceive() {
        return drive;
    }

    private void onGetCoordinatesResponse(RootActor.GetCoordinatesResponse getCoordinatesResponse) {
        CarCoordinates coordinates = getCoordinatesResponse.getCoordinates();
        // FIXME: 21.01.18 dodać kierunek
        if (this.currentRoad == getCoordinatesResponse.getRoad()) {
            if (coordinates.getCoordinates().getX() > x && coordinates.getCoordinates().getX() - x <= 100
                && coordinates.getCoordinates().getY() == (int) y && speed >= getCoordinatesResponse.getSpeed()) {
                speed = getCoordinatesResponse.getSpeed();
            } else if (coordinates.getCoordinates().getY() - y <= 100 && coordinates.getCoordinates().getX() == (int) x
                && speed >= getCoordinatesResponse.getSpeed()) {
                speed = getCoordinatesResponse.getSpeed();
            }
            if (speed == 0) {
                getContext().become(stopedByCarInFrontOfMe);
            }
        }
    }
    private void onStartMsg(StartMsg msg) {
        getTimers().startPeriodicTimer(TICK, new TickMsg(),
                Duration.create(Configuration.MOVE_CAR_MILIS, TimeUnit.MILLISECONDS));
    }

    private void onTick(TickMsg msg) {
        System.out.println(this.y);
        if (!isEndOfRoad()) {
            move();
        } else {
            // FIXME: 20.01.18
            speed = 0;
            getContext().become(waitingForJunction);
//            getTimers().cancel(TICK);
        }

        // FIXME: 20.01.18 only to carAgents
        ActorSelection actorSelection = getContext().actorSelection("../*");
        actorSelection.tell(createGetCoordinatesResponse(), getSelf());
//        mediator.tell(new DistributedPubSubMediator.Publish("coordinates", createGetCoordinatesResponse()), getSelf());
    }

    private void onGetCoordinatesRequest(GetCoordinatesRequest request) {
        RootActor.GetCoordinatesResponse response = createGetCoordinatesResponse();
        getSender().tell(response, getSelf());
    }

    private RootActor.GetCoordinatesResponse createGetCoordinatesResponse() {
        RootActor.GetCoordinatesResponse response = new RootActor.GetCoordinatesResponse();
        CarCoordinates carCoordinates = new CarCoordinates();
        carCoordinates.setCarId(this.id);
        carCoordinates.setDirection(this.currentDirection);
        carCoordinates.setCoordinates(new Coordinates((int) x, (int) y));
        response.setCoordinates(carCoordinates);
        response.setSpeed(speed);
        response.setRoad(currentRoad);
        return response;
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

    // FIXME: 20.01.18 to nie powinno tak się nazywać, działa tylko w przypadku gdy jesteśmy na początku drogi
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
        if (CarDirection.NORTH == currentDirection) {
            y += distance;
        } else if (CarDirection.EAST == currentDirection) {
            x += distance;
        } else if (CarDirection.SOUTH == currentDirection) {
            y -= distance;
        } else if (CarDirection.WEST == currentDirection) {
            x -= distance;
        }
    }

    private float getStartX(Junction junction) {
        Coordinates centerCoordinates = junction.getCenterCoordinates();

        if (this.currentDirection.equals(CarDirection.SOUTH)) {
            return centerCoordinates.getX() + Configuration.LANE_WIDTH * currentRoad.getOneDirectionNumberOfLanes()
                - Configuration.CAR_WIDTH / 2;
        } else if (this.currentDirection.equals(CarDirection.NORTH)) {
            return centerCoordinates.getX() - Configuration.LANE_WIDTH * currentRoad.getOneDirectionNumberOfLanes()
                + Configuration.CAR_WIDTH / 2;
        }
        return centerCoordinates.getX();
    }

    private float getStartY(Junction junction) {
        Coordinates centerCoordinates = junction.getCenterCoordinates();

        if (this.currentDirection.equals(CarDirection.EAST)) {
            return centerCoordinates.getY() + Configuration.LANE_WIDTH * currentRoad.getOneDirectionNumberOfLanes()
                - Configuration.CAR_WIDTH / 2;
        } else if (this.currentDirection.equals(CarDirection.WEST)) {
            return centerCoordinates.getY() - Configuration.LANE_WIDTH * currentRoad.getOneDirectionNumberOfLanes()
                + Configuration.CAR_WIDTH / 2;
        }
        return centerCoordinates.getY();
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
