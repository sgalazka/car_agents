package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.*;

import java.util.ArrayList;

public class FakeGraphReader implements IRoadGraphReader {

    public FakeGraphReader(String fileName) {

    }

    @Override
    public RoadNetwork getMap(String fileName) {
        Junction[] junctions = new Junction[2];
        Road[] roads = new Road[4];
        Road[] roads1 = new Road[4];
        Lane[] lanes = new Lane[4];
        lanes[0] = new Lane();
        lanes[1] = new Lane();
        roads[Junction.RIGHT] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads[Junction.UP] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads[Junction.LEFT] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads[Junction.DOWN] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads1[Junction.RIGHT] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads1[Junction.UP] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        roads1[Junction.LEFT] = roads[Junction.RIGHT];
        roads1[Junction.DOWN] = new Road(new ArrayList<Car[]>(), lanes, null, null);
        Junction junction = new Junction();
        junction.setCenterCoordinates(new Coordinates(200, 200));
        junction.setRoads(roads);
        Junction junction1 = new Junction();
        junction1.setCenterCoordinates(new Coordinates(800, 200));
        junction1.setRoads(roads1);
        junctions[0] = junction;
        junctions[1] = junction1;

        return new RoadNetwork(junctions, roads);
    }
}
