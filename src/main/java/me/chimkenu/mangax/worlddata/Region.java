package me.chimkenu.mangax.worlddata;

import org.bukkit.util.BoundingBox;

public record Region(double x1, double y1, double z1, double x2, double y2, double z2) {
    public BoundingBox toBoundingBox() {
        return new BoundingBox(x1, y1, z1, x2, y2, z2);
    }
}
