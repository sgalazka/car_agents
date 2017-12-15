package pl.edu.pw.elka.car_agents.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.model.Car;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.view.OnWindowCloseListener;
import pl.edu.pw.elka.car_agents.view.View;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;
import scala.concurrent.duration.Duration;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RootActor extends AbstractActorWithTimers implements OnWindowCloseListener {

    public final static int EXIT_SYSTEM_CAR_ID = 999999;
    private static Object TICK = "TICK";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(RootActor.class);
    }

    private final BlockingQueue<List<CarCoordinates>> blockingQueueToView = new LinkedBlockingQueue();
    List<CarCoordinates> carCoordinatesList;

    public RootActor() {
        Car car = new Car();
        car.setCoordinates(new Coordinates(200, 220));
        CarCoordinates carCoordinates = new CarCoordinates();
        carCoordinates.setCarId(0);
        carCoordinates.setCoordinates(car.getCoordinates());
        carCoordinates.setDirection(CarDirection.EAST);
        carCoordinatesList = new ArrayList<>();
        carCoordinatesList.add(carCoordinates);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartSystemMsg.class, this::onStartSystem)
                .match(FirstTickMsg.class, this::onFirstTick)
                .match(TickMsg.class, this::onTick)
                .build();
    }

    private void onStartSystem(StartSystemMsg msg) {
        log.debug("onStartSystem");
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


        getTimers().startSingleTimer(TICK, new FirstTickMsg(),
                Duration.create(3, TimeUnit.SECONDS));
    }

    private void onFirstTick(FirstTickMsg msg) {
        getTimers().startPeriodicTimer(TICK, new TickMsg(),
                Duration.create(100, TimeUnit.MILLISECONDS));
    }

    private void onTick(TickMsg msg) {
        log.debug("onTick");
        int x = carCoordinatesList.get(0).getCoordinates().getX();
        carCoordinatesList.get(0).getCoordinates().setX(x + 2);
        blockingQueueToView.add(carCoordinatesList);
    }

    @Override
    public void onWindowClosed() {
        stopViewThread();
        blockingQueueToView.add(carCoordinatesList);
        this.getContext().getSystem().terminate();
    }

    private void stopViewThread() {
        Car car = new Car();
        CarCoordinates carCoordinates = new CarCoordinates();
        carCoordinates.setCarId(EXIT_SYSTEM_CAR_ID);
        carCoordinatesList = new ArrayList<>();
        carCoordinatesList.add(carCoordinates);
    }

    public static class StartSystemMsg {
        public RoadNetwork roadNetwork;
        public int carCount;
    }

    public static class FirstTickMsg {

    }

    public static class TickMsg {

    }

}
