package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;

import java.util.ArrayList;
import java.util.List;

public class RoadNetwork {

    private static RoadNetwork instance;

    private IRoadGraphReader graphReader;
    private Junction[] junctions;
    private Road[] roads;

    RoadNetwork(Junction[] junctions, Road[] roads) {
        this.junctions = junctions;
        this.roads = roads;
    }

    public static RoadNetwork getInstance(String fileName) {
        if (instance == null) {
            instance = new FakeGraphReader(fileName).getMap(fileName); // TODO: 2017-12-06 podmienić na prawdziwą implementację
        }
        return instance;
    }

    public Junction[] getJunctions() {
        return junctions;
    }

    public Road[] getRoads() {
        return roads;
    }

    public Signpost[] getPath(Road start, Road End) {
        List<Signpost> fakeData = new ArrayList<>();
        Signpost fakeSignpost = new Signpost();
        fakeSignpost.setDirection(Direction.FORWARD);
        fakeSignpost.setJunction(new Junction());
        fakeData.add(fakeSignpost);
        return (Signpost[]) fakeData.toArray();
    }
}
