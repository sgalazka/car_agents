package pl.edu.pw.elka.car_agents;

public abstract class Configuration {

    public final static long REFRESH_VIEW_MILIS = 20;
    public final static long ADD_NEXT_CAR_MILIS = 2000;
    public final static long MOVE_CAR_MILIS = 20;
    public final static int CAR_SPEED = 90;
    public final static int HEIGHT = 1000;
    public final static int WIDTH = 1700;
    public final static int LANE_WIDTH = 20;
    public static int CAR_HEIGHT = (int) (LANE_WIDTH * 0.7 * 2.5);
    public static int CAR_WIDTH = (int) (LANE_WIDTH * 0.6);
    public final static String ROADNETWORK_FILENAME = "";
}
