package me.chimkenu.mangax.characters.naruto;

import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class Rasenshuriken extends Move {
    public Rasenshuriken() {
        super(null, null, 0, 10 * 20, Material.NETHER_STAR, Component.text("Rasenshuriken").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        activate = (plugin, entity) -> {
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setGravity(true);

            new BukkitRunnable() {
                int t = 20;
                @Override
                public void run() {
                    if (entity instanceof Player player && !player.isOnline()) {
                        stand.remove();
                        cancel();
                        return;
                    }

                    if (entity.isDead()) {
                        stand.remove();
                        cancel();
                        return;
                    }

                    if (t <= 0) {
                        stand.setVelocity(entity.getLocation().getDirection().multiply(4));
                        new BukkitRunnable() {
                            int t = 10;
                            @Override
                            public void run() {
                                if (t <= 0) {
                                    stand.getWorld().spawnParticle(Particle.DUST, stand.getLocation(), 400, 3, 0.2, 3, 1, new Particle.DustOptions(Color.WHITE, 1.5f));
                                    for (LivingEntity e : stand.getLocation().getNearbyLivingEntities(5)) {
                                        if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                                            MoveTargetEvent event = new MoveTargetEvent(Moves.NARUTO_RASENSHURIKEN, entity, e, 10, new Vector());
                                            Bukkit.getPluginManager().callEvent(event);
                                            if (event.isCancelled()) {
                                                continue;
                                            }
                                            e.damage(event.getDamage(), entity);
                                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
                                        }
                                    }
                                    cancel();
                                    return;
                                }

                                Vector v = stand.getVelocity();
                                stand.teleport(getRelativeLocation(stand.getLocation(), 0, 0, 0, 20, 0));
                                stand.setVelocity(v);
                                createHelicopter(plugin, stand);
                                t--;
                            }

                            @Override
                            public void cancel() {
                                super.cancel();
                                stand.remove();
                            }
                        }.runTaskTimer(plugin, 1, 1);

                        cancel();
                        return;
                    }

                    Location loc = entity.getLocation();
                    loc.add(0, 2, 0);
                    loc.setYaw(stand.getYaw() + 10);
                    stand.teleport(loc);
                    createHelicopter(plugin, stand);

                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TYPICAL, 10, 20, 10, false);
    }

    private void createHelicopter(JavaPlugin plugin, ArmorStand stand) {
        Location loc = stand.getLocation();
        loc.setPitch(0);
        for (int i = 0; i < 4; i++) {
            ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 5, 10, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0.01, new Particle.DustOptions(Color.WHITE, 1.5f)), 0);
            loc.setYaw(loc.getYaw() + 90);
        }
    }
}
