package pl.edu.pw.elka.car_agents.view;

import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;

import java.util.List;

public abstract class RoadNetworkView {

    protected OnWindowCloseListener onWindowCloseListener;
    protected RoadNetwork roadNetwork;

    public RoadNetworkView(OnWindowCloseListener onWindowCloseListener, RoadNetwork roadNetwork) {
        this.onWindowCloseListener = onWindowCloseListener;
        this.roadNetwork = roadNetwork;
    }

    public abstract void drawCars(List<CarCoordinates> carCoordinates);
}
