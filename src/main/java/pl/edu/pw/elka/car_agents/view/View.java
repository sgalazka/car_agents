package pl.edu.pw.elka.car_agents.view;

import pl.edu.pw.elka.car_agents.actor.RootActor;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class View implements Runnable {

    private BlockingQueue<List<CarCoordinates>> blockingQueue;
    private RoadNetworkView roadNetworkView;

    public View(BlockingQueue<List<CarCoordinates>> blockingQueue, OnWindowCloseListener onWindowCloseListener, RoadNetwork roadNetwork) {

        this.blockingQueue = blockingQueue;
        this.roadNetworkView = new SwingRoadNetworkView(onWindowCloseListener, roadNetwork);
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<CarCoordinates> carCoordinatesList = blockingQueue.take();
                if (carCoordinatesList != null && carCoordinatesList.size() > 0 && carCoordinatesList.get(0).getCarId() == RootActor.EXIT_SYSTEM_CAR_ID)
                    break;
                roadNetworkView.drawCars(carCoordinatesList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
