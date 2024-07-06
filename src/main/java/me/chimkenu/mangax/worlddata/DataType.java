package me.chimkenu.mangax.worlddata;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataType<T> {
    T toData(@NotNull String string);
    String toString(@NotNull T data);
    default @Nullable  T retrieveFrom(World world, NamespacedKey key) {
        String string = world.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (string == null)
            return null;
        return toData(string);
    };
}
