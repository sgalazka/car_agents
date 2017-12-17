package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;

public class Signpost {

    private CarDirection direction;
    private Junction junction;

    public CarDirection getDirection() {
        return direction;
    }

    public void setDirection(CarDirection direction) {
        this.direction = direction;
    }

    public Junction getJunction() {
        return junction;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }
}
