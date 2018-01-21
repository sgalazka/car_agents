package pl.edu.pw.elka.car_agents.model;

import lombok.Getter;

public class Road {

    @Getter
    private int oneDirectionNumberOfLanes;

    public Road(int oneDirectionNumberOfLanes) {
        this.oneDirectionNumberOfLanes = oneDirectionNumberOfLanes;
    }

}
