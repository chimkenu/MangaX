package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TheWorld extends Move {
    public static final String timeResistanceKey = "JOTARO_TIME_RESISTANCE";

    public TheWorld() {
        super(null, null, 130, 30 * 20, Material.CLOCK, Component.text("ZA WARUDO").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            if (entity instanceof Player player)
                player.setCooldown(getMaterial(), getCooldown());
            entity.getWorld().spawnParticle(Particle.DUST, entity.getEyeLocation(), 1500, 1, 1, 1, 0, new Particle.DustOptions(Color.YELLOW, 0.8f));

            final int CHARGE_TIME = 30;
            final int RADIUS = 10;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead()) {
                        cancel();
                        return;
                    }

                    if (entity instanceof Player player)
                        player.setCooldown(getMaterial(), getCooldown() + getFollowUpTime() - CHARGE_TIME);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, false));

                    Location loc = entity.getLocation();
                    AreaEffectCloud areaEffectCloud = entity.getWorld().spawn(loc, AreaEffectCloud.class);
                    areaEffectCloud.setColor(Color.BLACK);
                    areaEffectCloud.setDuration(getFollowUpTime() - CHARGE_TIME);
                    areaEffectCloud.setRadius(RADIUS);
                    areaEffectCloud.addScoreboardTag(entity.getUniqueId() + ".cloud");
                    areaEffectCloud.addScoreboardTag(timeResistanceKey);

                    new BukkitRunnable() {
                        final HashMap<Entity, Integer> entities = new HashMap<>();
                        @Override
                        public void run() {
                            if (entity instanceof Player player && !player.isOnline()) {
                                areaEffectCloud.remove();
                                cancel();
                                return;
                            }

                            if (areaEffectCloud.isDead() || entity.isDead()) {
                                areaEffectCloud.remove();
                                cancel();
                                return;
                            }

                            for (Entity e : loc.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                                if (e == entity) {
                                    continue;
                                }

                                if (isTheWorldable(e, entities) && e.getLocation().distanceSquared(areaEffectCloud.getLocation()) < RADIUS * RADIUS) {
                                    theWorld(plugin, areaEffectCloud, entity, e, areaEffectCloud.getDuration() + 20);
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }.runTaskLater(plugin, CHARGE_TIME);
        };
    }

    private void theWorld(JavaPlugin plugin, AreaEffectCloud cloud, LivingEntity source, Entity target, int duration) {
        if (target instanceof LivingEntity livingEntity) {
            new BukkitRunnable() {
                int t = duration;
                final Location loc = livingEntity.getLocation();
                final double health = livingEntity.getHealth();
                final double maxLoss = health - 6;

                @Override
                public void run() {
                    if (livingEntity.isDead() || (source instanceof Player player && !player.isOnline())) {
                        cancel();
                        return;
                    }

                    // Eject based on damage
                    if (t <= 0 || cloud.isDead() || source.isDead()) {
                        double diff = health - livingEntity.getHealth();
                        if (diff > 0) {
                            Vector direction = livingEntity.getLocation().toVector().subtract(source.getLocation().toVector());
                            direction = direction.normalize();
                            Vector v = livingEntity.getVelocity().add(direction.multiply(diff * 0.5)).add(new Vector(0, 0.5, 0));

                            MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_ZA_WARUDO, source, livingEntity, 0, v);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return;
                            }

                            livingEntity.setVelocity(livingEntity.getVelocity().add(event.getKnockback()));
                        }
                        cancel();
                        return;
                    }

                    MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_ZA_WARUDO, source, livingEntity, 0, new Vector());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    if (health - livingEntity.getHealth() >= maxLoss) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 5, false, false, false));
                    }

                    if (!livingEntity.getType().equals(EntityType.ARMOR_STAND)) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 2, 0, false, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 4, false, false, false));
                    }
                    livingEntity.teleport(loc);

                    t--;
                }

                @Override
                public void cancel() {
                    super.cancel();
                    livingEntity.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            }.runTaskTimer(plugin, 0, 1);

        } else {
            new BukkitRunnable() {
                int t = duration;
                final Location loc = target.getLocation();
                final Vector velocity = target.getVelocity();
                @Override
                public void run() {
                    if (target.isDead() || (source instanceof Player player && !player.isOnline())) {
                        cancel();
                        return;
                    }

                    if (t <= 0 || cloud.isDead() || source.isDead()) {
                        target.setVelocity(velocity);
                        cancel();
                        return;
                    }

                    target.teleport(loc);

                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    private boolean isTheWorldable(Entity e, HashMap<Entity, Integer> entities) {
        if (!entities.containsKey(e)) {
            if (e instanceof Player player) {
                // look for time resistance
                if (player.getInventory().contains(Material.CLOCK)) {
                    entities.put(e, 51);
                }
            }
            entities.putIfAbsent(e, 1);
        }
        entities.put(e, entities.get(e) - 1);
        return entities.get(e) == 0 && !e.getScoreboardTags().contains(timeResistanceKey);
    }

    @Override
    public String[] getLore() {
        return new String[] {
            "<#c4c4c4>Uses Star Platinum: The World to stop</#c4c4c4>",
            "<#c4c4c4>the flow of time for a brief moment,</#c4c4c4>",
            "<#c4c4c4>affecting all players within a certain</#c4c4c4>",
            "<#c4c4c4>distance of the user upon activation.</#c4c4c4>",
            "<reset>",
            "<i:false><#ffffff>Type:</#ffffff> <u>Special</u>",
            "<i:false><#ffffff>Range:</#ffffff> <#e8eb46>Mid</#e8eb46>",
            "<i:false><#ffffff>Duration:</#ffffff> <#e6dd6c><b>5</b> seconds</#e6dd6c>",
            "<i:false><#ffffff>Cooldown:</#ffffff> <#e6dd6c><b>30</b> seconds</#e6dd6c>",
            "<reset>",
            "<i:false><#f6ff52><b>[<key:key.attack>]</b></#f6ff52> <#ffffff>to use ability</#ffffff>"
        };
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.MID, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.CONTROL, MoveInfo.Difficulty.TYPICAL, 6, 20, 100, false);
    }
}
