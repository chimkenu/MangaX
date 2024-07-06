package me.chimkenu.mangax.worlddata;

import org.jetbrains.annotations.NotNull;

public class PositionArrayDataType implements DataType<Position[]> {
    @Override
    public Position[] toData(@NotNull String string) {
        String[] split = string.split("/");
        Position[] positions = new Position[split.length];
        PositionDataType positionDataType = new PositionDataType();
        for (int i = 0; i < split.length; i++) {
            positions[i] = positionDataType.toData(split[i]);
        }
        return positions;
    }

    @Override
    public String toString(Position @NotNull [] data) {
        if (data instanceof Position[] positions) {
            StringBuilder result = new StringBuilder();
            PositionDataType positionDataType = new PositionDataType();
            for (Position position : positions) {
                result.append(positionDataType.toString(position));
            }
            return result.toString();
        }
        throw new IllegalArgumentException();
    }
}
