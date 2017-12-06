package pl.edu.pw.elka.car_agents.map;

public enum Direction {
    FORWARD("FORWARD"),
    RIGHT("RIGHT"),
    LEFT("FORWARD");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
