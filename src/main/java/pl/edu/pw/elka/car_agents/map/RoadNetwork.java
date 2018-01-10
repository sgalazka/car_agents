package pl.edu.pw.elka.car_agents.map;

import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.pw.elka.car_agents.Configuration.ROADNETWORK_FILENAME;

public class RoadNetwork {

    private static RoadNetwork instance;

    private IRoadGraphReader graphReader;
    private Junction[] junctions;
    private Road[] roads;

    RoadNetwork(Junction[] junctions, Road[] roads) {
        this.junctions = junctions;
        this.roads = roads;
    }

    public static synchronized RoadNetwork getInstance() {
        if (instance == null) {
            instance = new FakeGraphReader(ROADNETWORK_FILENAME).getMap(ROADNETWORK_FILENAME); // TODO: 2017-12-06 podmienić na prawdziwą implementację
        }
        return instance;
    }

    public Junction[] getJunctions() {
        return junctions;
    }

    public Road[] getRoads() {
        return roads;
    }

    public List<Signpost> getPath(Road start, Road End) {
        List<Signpost> fakeData = new ArrayList<>();
        Signpost fakeSignpost = new Signpost();
        fakeSignpost.setDirection(CarDirection.NORTH);
        fakeSignpost.setJunction(new Junction());
        fakeData.add(fakeSignpost);
        return fakeData;
    }

    public Junction[] getInOutJunctions() {
        ArrayList<Junction> list = new ArrayList<>();
        for (Junction junction : junctions) {
            if (junction.isInOut())
                list.add(junction);
        }
        Junction[] junctions = new Junction[list.size()];
        junctions = list.toArray(junctions);
        return junctions;
    }
}
