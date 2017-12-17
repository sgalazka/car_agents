package pl.edu.pw.elka.car_agents.model;

public class Road {

    private int oneDirectionNumberOfLanes;

    public Road(int oneDirectionNumberOfLanes) {
        this.oneDirectionNumberOfLanes = oneDirectionNumberOfLanes;
    }

    public int getOneDirectionNumberOfLanes() {
        return oneDirectionNumberOfLanes;
    }

    public void setOneDirectionNumberOfLanes(int oneDirectionNumberOfLanes) {
        this.oneDirectionNumberOfLanes = oneDirectionNumberOfLanes;
    }
}
