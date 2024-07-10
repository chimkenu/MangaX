package me.chimkenu.mangax.worlddata;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class ComponentDataType implements DataType<Component> {
    @Override
    public Component toData(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    @Override
    public String toString(@NotNull Component data) {
        if (data instanceof Component component)
            return MiniMessage.miniMessage().serialize(component);
        throw new IllegalArgumentException();
    }
}
