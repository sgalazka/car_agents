package pl.edu.pw.elka.car_agents.model;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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

    @Override
    public String toString() {
        return "Junction{" +
                "id=" + id +
                ", roads=" + Arrays.toString(roads) +
                ", trafficLights=" + Arrays.toString(trafficLights) +
                ", centerCoordinates=" + centerCoordinates.toString() +
                ", isInOut=" + isInOut +
                '}';
    }

}
