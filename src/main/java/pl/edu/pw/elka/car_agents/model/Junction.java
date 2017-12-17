package pl.edu.pw.elka.car_agents.model;

public class Junction {

    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;

    private int id;
    private Road[] roads;
    private Boolean[] trafficLights;
    private Coordinates centerCoordinates;
    private boolean isInOut;

    public Junction() {
    }

    public Junction(int id, Road[] roads, Boolean[] trafficLights, Coordinates centerCoordinates, boolean isInOut) {
        this.id = id;
        this.roads = roads;
        this.trafficLights = trafficLights;
        this.centerCoordinates = centerCoordinates;
        this.isInOut = isInOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Road[] getRoads() {
        return roads;
    }

    public void setRoads(Road[] roads) {
        this.roads = roads;
    }

    public Boolean[] getTrafficLights() {
        return trafficLights;
    }

    public void setTrafficLights(Boolean[] trafficLights) {
        this.trafficLights = trafficLights;
    }

    public Coordinates getCenterCoordinates() {
        return centerCoordinates;
    }

    public void setCenterCoordinates(Coordinates centerCoordinates) {
        this.centerCoordinates = centerCoordinates;
    }

    public boolean isInOut() {
        return isInOut;
    }

    public void setInOut(boolean inOut) {
        isInOut = inOut;
    }
}
