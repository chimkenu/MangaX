package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTriggerEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Infinity extends Move implements Listener {
    public final String tag = "GOJO_INFINITY";
    
    public Infinity() {
        super(null, null, 0, 20 * 20, Material.MUSIC_DISC_5, Component.text("Infinity", NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        
        this.activate = (plugin, entity) -> {
            entity.addScoreboardTag(tag);
            Location loc = entity.getLocation();
            loc.setPitch(0);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 2, 20, (world, location, index) -> {
                    Location diff = entity.getLocation().subtract(loc);
                    location.add(diff.add(0, 0.2, 0));
                    world.spawnParticle(Particle.DUST, getRelativeLocation(location, Math.log(index), 0, 0, 0, 0), 1, 0.05, 0.05, 0.05, 0.1, new Particle.DustOptions(Color.WHITE, 0.8f));
                }, 0);
            }
        };
    }

    @Override
    public @NotNull String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 1, 1, 1, false);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMoveTrigger(MoveTriggerEvent e) {
        if (e.getEntity().getScoreboardTags().contains(tag) && (e.getMove() == Moves.GOJO_COLLAPSING_BLUE || e.getMove() == Moves.GOJO_RED_REVERSAL) && e.isCancelled() && e.getCancelReason() == MoveTriggerEvent.CancelReason.IN_COOLDOWN) {
            e.getEntity().removeScoreboardTag(tag);
            e.setCancelled(false);
            if (e.getEntity() instanceof Player player) {
                player.setCooldown(e.getMove().move.getMaterial(), 0);
            }
        }
    }
}
