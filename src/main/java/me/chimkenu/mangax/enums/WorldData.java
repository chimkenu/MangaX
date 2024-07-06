package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.worlddata.*;
import org.bukkit.NamespacedKey;

public enum WorldData {
    NAME (new ComponentDataType()),
    BUILDERS (new ComponentDataType()),
    CHARACTER_SELECTION_LOCATION (new PositionDataType()),
    CHARACTER_SELECTION_REGION (new RegionDataType()),
    SPAWN_LOCATIONS (new PositionArrayDataType());

    public final DataType<?> data;

    WorldData(DataType<?> data) {
        this.data = data;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(MangaX.getPlugin(MangaX.class), "WORLD_" + this);
    }
}
