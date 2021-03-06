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
        Junction[] junctions = new Junction[6];
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
        roads2[Junction.UP] = new Road(0);
        roads2[Junction.LEFT] = new Road(0);
        roads2[Junction.DOWN] = new Road(0);

        roads3[Junction.RIGHT] = new Road(0);
        roads3[Junction.DOWN] = roads1[Junction.UP];
        roads3[Junction.LEFT] = new Road(0);
        roads3[Junction.UP] = new Road(0);

        Road roads4[] = new Road[4];
        roads4[Junction.RIGHT] = new Road(0);
        roads4[Junction.DOWN] = new Road(0);
        roads4[Junction.LEFT] = new Road(0);
        roads4[Junction.UP] = roads[Junction.DOWN];

        Road roads5[] = new Road[4];
        roads5[Junction.RIGHT] = new Road(0);
        roads5[Junction.DOWN] = new Road(0);
        roads5[Junction.LEFT] = new Road(0);
        roads5[Junction.UP] = roads1[Junction.DOWN];


        Junction junction = new Junction(junctionCount++, roads, new Boolean[4], new Coordinates(400, 200), false);
        Junction junction1 = new Junction(junctionCount++, roads1, new Boolean[4], new Coordinates(800, 200), false);
        Junction junction2 = new Junction(junctionCount++, roads2, new Boolean[4], new Coordinates(0, 200), true);
        Junction junction3 = new Junction(junctionCount++, roads3, new Boolean[4], new Coordinates(800, 400), true);
        Junction junction4 = new Junction(junctionCount++, roads4, new Boolean[4], new Coordinates(400, 0), true);
        Junction junction5 = new Junction(junctionCount++, roads5, new Boolean[4], new Coordinates(800, 0), true);
        junctions[0] = junction;
        junctions[1] = junction1;
        junctions[2] = junction2;
        junctions[3] = junction3;
        junctions[4] = junction4;
        junctions[5] = junction5;

        return new RoadNetwork(junctions, roads);
    }
}
