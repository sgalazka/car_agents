package pl.edu.pw.elka.car_agents.view.model;

public enum CarDirection {
    NORTH(0), EAST(1), SOUTH(2), WEST(3);

    int value;

    CarDirection(int value) {
        this.value = value;
    }

    public static CarDirection fromValue(int value) {
        if (value >= 0 && value <= 3)
            return CarDirection.values()[value];
        return null;
    }
}
