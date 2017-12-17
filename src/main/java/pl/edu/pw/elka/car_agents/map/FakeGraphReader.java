package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;

public class FakeGraphReader implements IRoadGraphReader {

    private static int junctionCount = 0;

    public FakeGraphReader(String fileName) {

    }

    @Override
    public RoadNetwork getMap(String fileName) {
        Junction[] junctions = new Junction[4];
        Road[] roads = new Road[4];
        Road[] roads1 = new Road[4];
        Road[] roads2 = new Road[4];
        Road[] roads3 = new Road[4];
        roads[Junction.RIGHT] = new Road(2);
        roads[Junction.UP] = new Road(2);
        roads[Junction.LEFT] = new Road(2);
        roads[Junction.DOWN] = new Road(2);
        roads1[Junction.RIGHT] = new Road(2);
        roads1[Junction.UP] = new Road(2);
        roads1[Junction.LEFT] = roads[Junction.RIGHT];
        roads1[Junction.DOWN] = new Road(2);
        roads2[Junction.RIGHT] = roads[Junction.LEFT];
        roads2[Junction.UP] = null;
        roads2[Junction.LEFT] = null;
        roads2[Junction.DOWN] = null;
        roads3[Junction.RIGHT] = null;
        roads3[Junction.UP] = roads2[Junction.DOWN];
        roads3[Junction.LEFT] = null;
        roads3[Junction.DOWN] = null;
        Junction junction = new Junction(junctionCount++, roads, new Boolean[4], new Coordinates(200, 200), false);
        Junction junction1 = new Junction(junctionCount++, roads1, new Boolean[4], new Coordinates(800, 200), false);
        Junction junction2 = new Junction(junctionCount++, roads2, new Boolean[4], new Coordinates(0, 200), true);
        Junction junction3 = new Junction(junctionCount++, roads3, new Boolean[4], new Coordinates(800, 1000), true);
        junctions[0] = junction;
        junctions[1] = junction1;
        junctions[2] = junction2;
        junctions[3] = junction3;

        return new RoadNetwork(junctions, roads);
    }
}
