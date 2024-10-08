package me.chimkenu.mangax.characters.phoenix;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.time.Duration;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Objection extends Move {
    public Objection() {
        super(null, null, 10, 100, Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Component.text("Objection!").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {

            Location loc = entity.getLocation();
            loc.setPitch(0);
            loc = getRelativeLocation(loc, 0, 0, 4, 0, 0);

            for (int i = 0; i < 45; i++) {
                loc.setYaw(i * 8);
                loc.getWorld().spawnParticle(Particle.WHITE_SMOKE, getRelativeLocation(loc, 0, 0, 2, 0, 0), 5, 0.1, 0.1, 0.1, 0);
            }

            Location origin = loc;
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (LivingEntity e : origin.getNearbyLivingEntities(2)) {
                        if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                            MoveTargetEvent event = new MoveTargetEvent(Moves.PHOENIX_OBJECTION, entity, e, 0, new Vector());
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                continue;
                            }

                            Location loc = e.getEyeLocation();
                            loc.add(0, 0.2, 0);
                            loc.setYaw(e.getYaw());
                            loc.setPitch(0);
                            TextDisplay objection = entity.getWorld().spawn(loc, TextDisplay.class);
                            objection.text(Component.text("Objection!").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));
                            objection.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                            objection.setShadowed(false);
                            objection.setInterpolationDelay(0);
                            objection.setInterpolationDuration(4);
                            objection.setBillboard(Display.Billboard.CENTER);

                            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 100, false, false, true));
                            e.showTitle(Title.title(Component.text("Objection!").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD), Component.text(""), Title.Times.times(Duration.ofMillis(50 * 5), Duration.ofMillis(50 * 75), Duration.ofMillis(50 * 20))));

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    objection.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(3, 3, 3), new AxisAngle4f()));
                                }
                            }.runTaskLater(plugin, 2);

                            new BukkitRunnable() {
                                int t = 90;
                                @Override
                                public void run() {
                                    if (t <= 0 || e.isDead()) {
                                        cancel();
                                        return;
                                    }
                                    e.getWorld().spawnParticle(Particle.WHITE_SMOKE, e.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.1);
                                    t--;
                                }
                            }.runTaskTimer(plugin, 0, 1);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    objection.remove();
                                }
                            }.runTaskLater(plugin, 80);
                        }
                    }
                }
            }.runTaskLater(plugin, getFollowUpTime());
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.CLOSE, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.CONTROL, MoveInfo.Difficulty.TRICKY, 3, getFollowUpTime(), 100, false);
    }
}
