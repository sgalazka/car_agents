package pl.edu.pw.elka.car_agents.actor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.map.Signpost;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.view.OnWindowCloseListener;
import pl.edu.pw.elka.car_agents.view.View;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

public class RootActor extends AbstractActorWithTimers implements OnWindowCloseListener {

    public final static int EXIT_SYSTEM_CAR_ID = -1;
    private static Object REFRESHH_VIEW_TICK = "REFRESHH_VIEW_TICK";
    private static Object ADD_NEW_CAR_TICK = "ADD_NEW_CAR_TICK";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static int currentCarId = 0;
    private RoadNetwork roadNetwork;
    private Map<Integer, CarCoordinates> carCoordinatesMap;
    private int maxCarCount = 0;
    private Junction[] inOutJunctions;
    private Junction[] allJuncions;

    public static Props props() {
        return Props.create(RootActor.class);
    }

    public RootActor() {
        this.carCoordinatesMap = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartSystemMsg.class, this::onStartSystem)
                .match(FirstTickMsg.class, this::onFirstTick)
                .match(RefreshViewMsg.class, this::refreshView)
                .match(GetCoordinatesResponse.class, this::onGetCoordinatesResponce)
                .build();
    }

    private void onStartSystem(StartSystemMsg msg) {
        log.debug("onStartSystem");
        this.roadNetwork = msg.roadNetwork;
        this.maxCarCount = msg.carCount;
        this.inOutJunctions = roadNetwork.getInOutJunctions();
        this.allJuncions = roadNetwork.getJunctions();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(new View(blockingQueueToView, RootActor.this, msg.roadNetwork));
                    thread.start();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        int i = 0;
        for (Junction junction : msg.roadNetwork.getJunctions()) {
            getContext().actorOf(JunctionActor.props(i++, junction));
        }

        getTimers().startSingleTimer(REFRESHH_VIEW_TICK, new FirstTickMsg(),
                Duration.create(3, TimeUnit.SECONDS));
    }

    private void onFirstTick(FirstTickMsg msg) {
        getTimers().startPeriodicTimer(REFRESHH_VIEW_TICK, new RefreshViewMsg(),
                Duration.create(Configuration.REFRESH_VIEW_MILIS, TimeUnit.MILLISECONDS));

        scheduleOnceCreation(
            getContext().actorOf(CarActor.props(0, inOutJunctions[0], inOutJunctions[1], 70,
                new LinkedList<Signpost>(){{
                add(Signpost.builder()
                    .direction(CarDirection.EAST)
                    .junction(allJuncions[0])
                    .build());
                add(Signpost.builder()
                    .direction(CarDirection.NORTH)
                    .junction(allJuncions[1])
                    .build());
            }}),
                "car" + 0), 2);
        scheduleOnceCreation(
            getContext().actorOf(CarActor.props(1, inOutJunctions[2], inOutJunctions[1], 70,
                new LinkedList<Signpost>(){{
                    add(Signpost.builder()
                        .direction(CarDirection.EAST)
                        .junction(allJuncions[0])
                        .build());
                    add(Signpost.builder()
                        .direction(CarDirection.SOUTH)
                        .junction(allJuncions[1])
                        .build());
                }}), "car" + 1), 6);
        scheduleOnceCreation(
            getContext().actorOf(CarActor.props(2, inOutJunctions[3], inOutJunctions[0], 90,
                new LinkedList<Signpost>(){{
                    add(Signpost.builder()
                        .direction(CarDirection.WEST)
                        .junction(allJuncions[0])
                        .build());
                    add(Signpost.builder()
                        .direction(CarDirection.SOUTH)
                        .junction(allJuncions[1])
                        .build());
                }}), "car" + 3), 13);
//        scheduleOnceCreation(
//            getContext().actorOf(CarActor.props(3, inOutJunctions[0], inOutJunctions[1], 130), "car" + 4), 4);
        }

    private void scheduleOnceCreation(ActorRef receiver, int seconds) {
        getContext().getSystem().scheduler()
            .scheduleOnce(Duration.create(seconds, TimeUnit.SECONDS), receiver, new CarActor.StartMsg(),
                context().system().dispatcher(), null);
    }

    private final BlockingQueue<List<CarCoordinates>> blockingQueueToView = new LinkedBlockingQueue();

    private void refreshView(RefreshViewMsg msg) {
        log.debug("refreshView");
        ActorSelection actorSelection = getContext().actorSelection("../root/car*");
        actorSelection.tell(new CarActor.GetCoordinatesRequest(), getSelf());
        List<CarCoordinates> carCoordinatesList = new ArrayList<>(carCoordinatesMap.values());
        blockingQueueToView.add(carCoordinatesList);
    }

    private void onGetCoordinatesResponce(GetCoordinatesResponse responce) {
        updateCoordinates(responce.coordinates);
    }

    private void stopViewThread() {
        CarCoordinates carCoordinates = new CarCoordinates();
        carCoordinates.setCarId(EXIT_SYSTEM_CAR_ID);
        List<CarCoordinates> carCoordinatesList = new ArrayList<>();
        carCoordinatesList.add(carCoordinates);
        blockingQueueToView.add(carCoordinatesList);
    }

    private synchronized void updateCoordinates(CarCoordinates carCoordinates) {
        carCoordinatesMap.put(carCoordinates.getCarId(), carCoordinates);
    }

    public static class StartSystemMsg {
        public RoadNetwork roadNetwork;
        public int carCount;
    }

    @Override
    public void onWindowClosed() {
        stopViewThread();
        this.getContext().getSystem().terminate();
    }

    public static class FirstTickMsg {

    }

    public static class RefreshViewMsg {

    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class AddNewCarMsg {
        Integer speed;
        Junction begin;
        Junction finish;
    }

    @Setter
    @Getter
    public static class GetCoordinatesResponse {
        private CarCoordinates coordinates;
        private Integer speed;
        private Road road;
    }
}
