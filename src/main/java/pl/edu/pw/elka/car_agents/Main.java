package pl.edu.pw.elka.car_agents;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import pl.edu.pw.elka.car_agents.actor.RootActor;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;

import java.util.concurrent.CompletableFuture;

public class Main {


    public final static int LANE_WIDTH = 20;

    public static void main(String[] args) {
//        final RoadNetworkView view;
//        Thread thread = null;
        ActorSystem system = ActorSystem.create("car-agents");
        ActorRef root = system.actorOf(RootActor.props(), "root");
        RootActor.StartSystemMsg startSystemMsg = new RootActor.StartSystemMsg();
        startSystemMsg.roadNetwork = RoadNetwork.getInstance("");
        CompletableFuture<Object> future = PatternsCS.ask(root, startSystemMsg, 1000 * 20).toCompletableFuture();
//        view = new SwingRoadNetworkView(new OnWindowCloseListener() {
//            @Override
//            public void onWindowClosed() {
//                system.terminate();
//            }
//        }, RoadNetwork.getInstance(""));
//        Car car = new Car();
//        car.setCoordinates(new Coordinates(500, 500));
//        CarCoordinates carCoordinates = new CarCoordinates();
//        carCoordinates.setCarId(0);
//        carCoordinates.setCoordinates(car.getCoordinates());
//        carCoordinates.setDirection(CarDirection.EAST);
//        List<CarCoordinates> carCoordinatesList = new ArrayList<>();
//        carCoordinatesList.add(carCoordinates);
//        view.drawCars(carCoordinatesList);
    }
}
