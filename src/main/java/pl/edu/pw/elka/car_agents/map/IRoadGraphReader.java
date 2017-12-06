package pl.edu.pw.elka.car_agents.map;

/**
 * powinien czytać z pliku konfigurację mapy
 */
public interface IRoadGraphReader {
    RoadNetwork getMap(String fileName);
}
