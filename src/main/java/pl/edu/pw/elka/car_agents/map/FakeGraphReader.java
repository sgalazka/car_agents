package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;

import java.util.ArrayList;
import java.util.List;

public class FakeGraphReader implements IRoadGraphReader {

    public FakeGraphReader(String fileName) {

    }

    @Override
    public RoadNetwork getMap(String fileName) {
        List<Junction> junctions = new ArrayList<>();
        List<Road> roads = new ArrayList<>();
        return new RoadNetwork((Junction[]) junctions.toArray(), (Road[]) roads.toArray());
    }
}
