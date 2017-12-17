package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.view.OnWindowCloseListener;
import pl.edu.pw.elka.car_agents.view.View;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import scala.concurrent.duration.Duration;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
                .match(AddNewCarMsg.class, this::addNewCar)
                .match(GetCoordinatesResponce.class, this::onGetCoordinatesResponce)
                .build();
    }

    private void onStartSystem(StartSystemMsg msg) {
        log.debug("onStartSystem");
        this.roadNetwork = msg.roadNetwork;
        this.maxCarCount = msg.carCount;
        this.inOutJunctions = roadNetwork.getInOutJunctions();
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


        getTimers().startSingleTimer(REFRESHH_VIEW_TICK, new FirstTickMsg(),
                Duration.create(3, TimeUnit.SECONDS));
    }

    private void onFirstTick(FirstTickMsg msg) {
        getTimers().startPeriodicTimer(REFRESHH_VIEW_TICK, new RefreshViewMsg(),
                Duration.create(Configuration.REFRESH_VIEW_MILIS, TimeUnit.MILLISECONDS));
        getTimers().startPeriodicTimer(ADD_NEW_CAR_TICK, new AddNewCarMsg(),
                Duration.create(Configuration.ADD_NEXT_CAR_MILIS, TimeUnit.MILLISECONDS));
    }

    private final BlockingQueue<List<CarCoordinates>> blockingQueueToView = new LinkedBlockingQueue();

    private void refreshView(RefreshViewMsg msg) {
        log.debug("refreshView");
        ActorSelection actorSelection = getContext().actorSelection("../root/car*");
        actorSelection.tell(new CarActor.GetCoordinatesRequest(), getSelf());
        List<CarCoordinates> carCoordinatesList = new ArrayList<>(carCoordinatesMap.values());
        blockingQueueToView.add(carCoordinatesList);
    }

    private void onGetCoordinatesResponce(GetCoordinatesResponce responce) {
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

    private void addNewCar(AddNewCarMsg msg) {
        if (currentCarId <= maxCarCount) {
            int newCarId = currentCarId++;
            ActorRef actorRef = getContext().actorOf(CarActor.props(newCarId, inOutJunctions[0], inOutJunctions[1]), "car" + newCarId);
            actorRef.tell(new CarActor.StartMsg(), getSelf());
        }
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

    public static class AddNewCarMsg {

    }

    public static class GetCoordinatesResponce {
        public CarCoordinates coordinates;
    }
}
