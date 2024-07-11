package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.worlddata.*;
import org.bukkit.NamespacedKey;

public enum WorldData {
    NAME (new ComponentDataType()),
    BUILDERS (new ComponentDataType()),
    VIEW_MAP_POSITIONS (new PositionArrayDataType()),
    CHARACTER_SELECTION_LOCATION_A (new PositionDataType()),
    CHARACTER_SELECTION_REGION_A(new RegionDataType()),
    CHARACTER_SELECTION_LOCATION_B (new PositionDataType()),
    CHARACTER_SELECTION_REGION_B (new RegionDataType()),
    SPAWN_POSITIONS (new PositionArrayDataType());

    public final DataType<?> data;

    WorldData(DataType<?> data) {
        this.data = data;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(MangaX.getPlugin(MangaX.class), "WORLD_" + this);
    }
}
