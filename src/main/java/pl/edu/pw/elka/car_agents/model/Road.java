package pl.edu.pw.elka.car_agents.model;

import java.util.ArrayList;
import java.util.List;

public class Road {
    public List<Lane> getLanes() {
        return lanes;
    }

    private List<Car[]> carsOnLanes;
    private List<Lane> lanes = new ArrayList<>();
    private Coordinates startCoordinates;
    private Coordinates endCoordinates;

    public List<Car[]> getCarsOnLanes() {
        return carsOnLanes;
    }

    public void setCarsOnLanes(List<Car[]> carsOnLanes) {
        this.carsOnLanes = carsOnLanes;
    }


    public Coordinates getStartCoordinates() {
        return startCoordinates;
    }

    public void setStartCoordinates(Coordinates startCoordinates) {
        this.startCoordinates = startCoordinates;
    }

    public Coordinates getEndCoordinates() {
        return endCoordinates;
    }

    public void setEndCoordinates(Coordinates endCoordinates) {
        this.endCoordinates = endCoordinates;
    }
}
