package pl.edu.pw.elka.car_agents.util;

import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.model.Road;

public class JunctionUtils {

    public static Junction[] getJunctionsForRoad(Road road) {
        Junction[] junctions = new Junction[2];
        int index = 0;
        for (Junction junction : RoadNetwork.getInstance().getJunctions()) {
            for (Road r : junction.getRoads()) {
                if (r == road) {
                    junctions[index] = junction;
                    index++;
                }
            }
            if (index >= 2)
                break;
        }
        if (junctions.length < 2)
            throw new IllegalArgumentException("Road points to " + junctions.length + " junctions");
        return junctions;
    }

    public static Coordinates getBorderForRoadOnJunction(Junction junction, Road road) {
        for (int i = 0; i < junction.getRoads().length; i++) {
            if (junction.getRoads()[i] == road) {
                return JunctionUtils.getBorderCoordinates(junction, i);
            }
        }
        throw new IllegalArgumentException("Road does not belong to junction");
    }

    public static Coordinates getBorderCoordinates(Junction junction, int direction) {
        if (direction == Junction.UP) {
            Coordinates coordinates = junction.getCenterCoordinates();
            int yFromCenterToBorder = junction.getRoads()[(direction + 1) % 3].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
            int y = coordinates.getY() - yFromCenterToBorder;
            return new Coordinates(coordinates.getX(), y);
        } else if (direction == Junction.DOWN) {
            Coordinates coordinates = junction.getCenterCoordinates();
            int yFromCenterToBorder = junction.getRoads()[(direction + 1) % 3].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
            int y = coordinates.getY() - yFromCenterToBorder;
            return new Coordinates(coordinates.getX(), y);
        } else if (direction == Junction.RIGHT) {
            Coordinates coordinates = junction.getCenterCoordinates();
            int xFromCenterToBorder = junction.getRoads()[(direction + 1) % 3].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
            int x = coordinates.getX() + xFromCenterToBorder;
            return new Coordinates(x, coordinates.getY());
        } else if (direction == Junction.LEFT) {
            Coordinates coordinates = junction.getCenterCoordinates();
            int xFromCenterToBorder = junction.getRoads()[(direction + 1) % 3].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
            int x = coordinates.getX() - xFromCenterToBorder;
            return new Coordinates(x, coordinates.getY());
        }
        throw new IllegalArgumentException("Direction is wrong: " + direction);
    }

    public static Road getRoadForInOutJunction(Junction inOutJunction) {
        for (Road road : inOutJunction.getRoads()) {
            // FIXME: 20.01.18 bierze pierwszą drogę w skrzyżowaniu, mało oczywiste
            if (road != null && road.getOneDirectionNumberOfLanes() > 0)
                return road;
        }
        throw new IllegalArgumentException("No road in entry junction");
    }
}
