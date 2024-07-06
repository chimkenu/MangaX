package me.chimkenu.mangax.worlddata;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ComponentDataType implements DataType<Component> {
    @Override
    public Component toData(@NotNull String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    @Override
    public String toString(@NotNull Component data) {
        if (data instanceof Component component)
            return LegacyComponentSerializer.legacyAmpersand().serialize(component);
        throw new IllegalArgumentException();
    }
}
