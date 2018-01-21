package pl.edu.pw.elka.car_agents.view.model;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.elka.car_agents.model.Coordinates;

@Getter
@Setter
public class CarCoordinates {

    private Coordinates coordinates;
    private CarDirection direction;
    private int CarId;

    @Override
    public String toString() {
        return "CarCoordinates{" +
                "coordinates=" + coordinates.toString() +
                ", direction=" + direction +
                ", CarId=" + CarId +
                '}';
    }
}
