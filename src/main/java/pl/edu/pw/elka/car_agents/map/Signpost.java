package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Junction;

public class Signpost {

    private Direction direction;
    private Junction junction;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Junction getJunction() {
        return junction;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }
}
