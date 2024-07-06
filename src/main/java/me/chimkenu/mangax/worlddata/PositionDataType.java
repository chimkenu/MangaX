package me.chimkenu.mangax.worlddata;

import org.jetbrains.annotations.NotNull;

public class PositionDataType implements DataType<Position> {
    @Override
    public Position toData(@NotNull String string) {
        String[] strings = string.split(",");
        if (strings.length != 5) {
            throw new IllegalArgumentException();
        }
        double[] values = new double[5];
        for (int i = 0; i < 5; i++) {
            values[i] = Double.parseDouble(strings[i]);
        }

        return new Position(values[0], values[1], values[2], (float) values[3], (float) values[4]);
    }

    @Override
    public String toString(@NotNull Position data) {
        if (data instanceof Position position)
            return position.x() + "," + position.y() + "," + position.z() + "," + position.pitch() + "," + position.yaw();
        throw new IllegalArgumentException();
    }
}
