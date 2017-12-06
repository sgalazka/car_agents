package pl.edu.pw.elka.car_agents.model;

public class Junction {

    public final static int RIGHT = 0;
    public final static int UP = 1;
    public final static int LEFT = 2;
    public final static int DOWN = 3;

    private Road[] roads;
    private Boolean[] trafficLights;

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
}
