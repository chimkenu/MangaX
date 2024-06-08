package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Flamethrower extends Move {
    public Flamethrower() {
        super(null, null, 0, 10 * 20, Material.BLAZE_POWDER, Component.text("Flamethrower").color(TextColor.color(0xff8903)).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, player) -> {
            player.getWorld().playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 0);
            new BukkitRunnable() {
                int t = 40;

                @Override
                public void run() {
                    Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
                    if (t <= 0 || player.isDead() || !player.isOnline() || move == null || !move.equals(Moves.TODOROKI_FLAMETHROWER)) {
                        cancel();
                        return;
                    }

                    if (t % 7 == 0) {
                        player.getWorld().playSound(player, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1, 0);
                    }

                    Location loc = getRelativeLocation(player.getEyeLocation(), -0.3, -0.15, 0.6, 0, 0);
                    for (int i = 0; i < 5; i++) {
                        Location origin = getRelativeLocation(loc.clone(), 0, 0, 0, random() * 40, random() * 40);
                        ParticleEffects.create(plugin, player.getWorld(), origin.toVector(), origin.getDirection(), 10, 10, new ParticleEffects.Effect() {
                            @Override
                            public void playParticle(World world, Location location, int index) {
                                world.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0.01);
                            }

                            @Override
                            public void intersect(LivingEntity e) {
                                if (!e.getType().equals(EntityType.ARMOR_STAND) && e != player) {
                                    if (e.getFireTicks() < 1)
                                        e.damage(0.5, player);
                                    e.setFireTicks(40);
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
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    private float random() {
        return (float) (Math.random() - 0.5);
    }
}
