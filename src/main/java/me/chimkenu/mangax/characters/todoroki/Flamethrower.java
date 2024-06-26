package me.chimkenu.mangax.characters.todoroki;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Flamethrower extends Move {
    public Flamethrower() {
        super(null, null, 0, 10 * 20, Material.BLAZE_POWDER, Component.text("Flamethrower").color(TextColor.color(0xff8903)).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            entity.getWorld().playSound(entity, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 0);
            new BukkitRunnable() {
                int t = 40;

                @Override
                public void run() {
                    EntityEquipment equipment = entity.getEquipment();
                    Moves move = null;
                    if (equipment != null) {
                        move = Moves.getMoveFromItem(equipment.getItemInMainHand());
                    }

                    if (t <= 0 || entity.isDead() || move == null || !move.equals(Moves.TODOROKI_FLAMETHROWER) || (entity instanceof Player player && !player.isOnline())) {
                        cancel();
                        return;
                    }

                    if (t % 7 == 0) {
                        entity.getWorld().playSound(entity, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1, 0);
                    }

                    Location loc = getRelativeLocation(entity.getEyeLocation(), -0.3, -0.15, 0.6, 0, 0);
                    for (int i = 0; i < 5; i++) {
                        Location origin = getRelativeLocation(loc.clone(), 0, 0, 0, random() * 40, random() * 40);
                        ParticleEffects.create(plugin, entity.getWorld(), origin.toVector(), origin.getDirection(), 10, 10, new ParticleEffects.Effect() {
                            @Override
                            public void playParticle(World world, Location location, int index) {
                                world.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0.01);
                            }

                            @Override
                            public void intersect(LivingEntity e) {
                                if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                                    MoveTargetEvent event = new MoveTargetEvent(Moves.TODOROKI_FLAMETHROWER, entity, e, 0.5, new Vector());
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (event.isCancelled()) {
                                        return;
                                    }

                                    if (e.getFireTicks() < 1)
                                        event.getTarget().damage(0.5, entity);
                                    event.getTarget().setFireTicks(40);
                                }
                            }


                        }, 0);
                    }

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
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TRIVIAL, 5, 1, 40, true);
    }

    private float random() {
        return (float) (Math.random() - 0.5);
    }
}
