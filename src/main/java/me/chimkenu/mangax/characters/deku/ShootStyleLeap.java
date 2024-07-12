package me.chimkenu.mangax.characters.deku;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ShootStyleLeap extends Move implements Listener {
    private final String tag = "DEKU_SHOOT_STYLE_LEAP";

    public ShootStyleLeap() {
        super(null, null, 20, 15 * 20, Material.LEATHER_HELMET, Component.text("Shoot Style Leap").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            // Silly jump effects
            Location loc = entity.getLocation();
            loc.setY(loc.getY() + 0.1);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 5, 10, (world, location, index) -> world.spawnParticle(Particle.SMOKE, location, 2, 0, 0.5, 0, 0), 0);
            }

            // Launch entity
            entity.setVelocity(entity.getVelocity().add(new Vector(0, 2, 0)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead()) {
                        cancel();
                        return;
                    }
                    Location loc = entity.getLocation();
                    loc.setPitch(0);
                    entity.setVelocity(entity.getVelocity().add(loc.getDirection().multiply(3)));

                    // Give entity tag after launch
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.addScoreboardTag(tag);
                        }
                    }.runTaskLater(plugin, 1);
                }
            }.runTaskLater(plugin, 1);
        };

        this.followUp = (plugin, entity) -> {
            if (entity.getScoreboardTags().contains(tag)) {
                entity.setVelocity(entity.getVelocity().add(new Vector(0, -2, 0)));
            }
        };
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack item = super.getItem();
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(0x106761));
        meta.displayName(getName());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.LONG, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.FORWARD, MoveInfo.Type.AREA, MoveInfo.Difficulty.TRICKY, 15, 12, 10, false);
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @EventHandler
    public void onLand(EntityMoveEvent e) {
        onLand(e.getEntity());
    }

    @EventHandler
    public void onLand(PlayerMoveEvent e) {
        onLand(e.getPlayer());
    }

    private void onLand(LivingEntity entity) {
        if (entity.getScoreboardTags().contains(tag) && !entity.getLocation().subtract(0, 0.01, 0).getBlock().isEmpty()) {
            entity.removeScoreboardTag(tag);

            // Silly ground pound effect
            Location loc = entity.getLocation();
            loc.setPitch(0);
            loc.add(0, 0.2, 0);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(MangaX.getPlugin(MangaX.class), loc.getWorld(), loc.toVector(), loc.getDirection(), 6, 5, (world, location, index) -> world.spawnParticle(Particle.CRIT, location, 10, 0.25, 0.1, 0.25, 0.15), 0);
            }

            // Ground pound damage
            entity.damage(1, entity);
            for (LivingEntity l : entity.getLocation().getNearbyLivingEntities(5)) {
                if (!l.getType().equals(EntityType.ARMOR_STAND) && l != entity) {
                    MoveTargetEvent event = new MoveTargetEvent(Moves.DEKU_DELAWARE_SMASH, entity, l, 8, new Vector());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        continue;
                    }
                    l.setVelocity(l.getVelocity().add(event.getKnockback()));
                    l.damage(event.getDamage(), entity);
                }
            }
        }
    }
}
