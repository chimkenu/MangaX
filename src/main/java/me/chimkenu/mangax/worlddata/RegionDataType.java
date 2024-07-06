package me.chimkenu.mangax.worlddata;

import org.jetbrains.annotations.NotNull;

public class RegionDataType implements DataType<Region> {
    @Override
    public Region toData(@NotNull String string) {
        String[] strings = string.split(",");
        if (strings.length != 6) {
            throw new IllegalArgumentException();
        }
        double[] values = new double[6];
        for (int i = 0; i < 6; i++) {
            values[i] = Double.parseDouble(strings[i]);
        }

        return new Region(values[0], values[1], values[2], values[3], values[4], values[5]);
    }

    @Override
    public String toString(@NotNull Region data) {
        if (data instanceof Region region)
            return region.x1() + "," + region.y1() + "," + region.z1() + "," + region.x2() + "," + region.y2() + "," + region.z2();
        throw new IllegalArgumentException();
    }
}
