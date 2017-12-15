package pl.edu.pw.elka.car_agents.model;

import java.util.List;

public class Road {

    private List<Car[]> carsOnLanes;
    private Lane[] lanes;
    private Coordinates startCoordinates;
    private Coordinates endCoordinates;

    public Road(List<Car[]> carsOnLanes, Lane[] lanes, Coordinates startCoordinates, Coordinates endCoordinates) {
        this.carsOnLanes = carsOnLanes;
        this.lanes = lanes;
        this.startCoordinates = startCoordinates;
        this.endCoordinates = endCoordinates;
    }

    public List<Car[]> getCarsOnLanes() {
        return carsOnLanes;
    }

    public void setCarsOnLanes(List<Car[]> carsOnLanes) {
        this.carsOnLanes = carsOnLanes;
    }

    public Lane[] getLanes() {
        return lanes;
    }

    public void setLanes(Lane[] lanes) {
        this.lanes = lanes;
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
