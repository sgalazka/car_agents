package pl.edu.pw.elka.car_agents.view.model;

import pl.edu.pw.elka.car_agents.model.Coordinates;

public class CarCoordinates {

    private Coordinates coordinates;
    private CarDirection direction;
    private int CarId;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getCarId() {
        return CarId;
    }

    public void setCarId(int carId) {
        CarId = carId;
    }

    public CarDirection getDirection() {
        return direction;
    }

    public void setDirection(CarDirection direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "CarCoordinates{" +
                "coordinates=" + coordinates.toString() +
                ", direction=" + direction +
                ", CarId=" + CarId +
                '}';
    }
}
