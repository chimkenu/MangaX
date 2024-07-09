package me.chimkenu.mangax.characters.gojo;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;
import static me.chimkenu.mangax.utils.ArmorStandUtil.setUpArmorStand;

public class CollapsingBlue extends Move implements Listener {
    public static final String tag = "GOJO_COLLAPSING_BLUE";
    private final HashSet<ArmorStand> stands = new HashSet<>();

    public CollapsingBlue() {
        super(null, null, 20 * 10, 20 * 18, Material.BLUE_CONCRETE, Component.text("Collapsing Blue", NamedTextColor.BLUE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Location loc = getRelativeLocation(entity.getEyeLocation(), 0, 0, 2, 0, 0);
            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
            setUpArmorStand(stand);
            stand.setSmall(true);
            stand.addScoreboardTag(tag);
            stand.addScoreboardTag(entity.getUniqueId().toString());
            stand.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime(), 3, false, false, false));
            stands.add(stand);
            stand.setGravity(true);

            new BukkitRunnable() {
                int t = getFollowUpTime();
                @Override
                public void run() {
                    if (t <= 0 || entity.isDead() || stand.isDead()) {
                        stand.remove();
                        cancel();

                        if (entity instanceof Player player) {
                            player.setCooldown(getMaterial(), getCooldown());
                        }

                        return;
                    }
                    t--;

                    HollowPurple.makeSphere(stand.getLocation(), Color.fromRGB(0x2986ff), 50, 0.4);
                    stand.setVelocity(stand.getVelocity().add(new Vector(0, 0.06, 0)));
                    for (LivingEntity e : stand.getLocation().getNearbyLivingEntities(5)) {
                        if (!e.hasGravity() || e == entity || e == stand) {
                            continue;
                        }

                        Vector direction = e.getLocation().toVector().subtract(stand.getLocation().toVector());
                        Vector v = null;

                        double damage = 0;
                        double distance = direction.lengthSquared();
                        if (distance < 4) {
                            damage = 2;
                            if (distance < 0.05) v = new Vector();
                        }

                        direction = direction.normalize();
                        v = v == null ? e.getVelocity().add(direction.multiply(-0.1)) : v;

                        // hollow purple check
                        if (e.getScoreboardTags().contains(RedReversal.tag) &&
                                e.getScoreboardTags().contains(entity.getUniqueId().toString()) &&
                                e instanceof ArmorStand two &&
                                distance < 0.1) {
                            HollowPurple.activate(plugin, entity, stand, two);
                            return;
                        }

                        MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_RED_REVERSAL, entity, e, damage, v);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        if (event.getKnockback().lengthSquared() > 0)
                            e.setVelocity(event.getKnockback());
                        if (event.getDamage() > 0)
                            e.damage(event.getDamage(), entity);
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.MID, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.CONTROL, MoveInfo.Difficulty.TYPICAL, 4, 1, getFollowUpTime(), false);
    }

    @EventHandler
    public void onLeftClick(PlayerArmSwingEvent e) {
        Player player = e.getPlayer();
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == Moves.GOJO_COLLAPSING_BLUE && player.getCooldown(move.move.getMaterial()) > 0) {
            ArmorStand stand = getStand(player.getUniqueId().toString());
            if (stand == null) {
                return;
            }

            Vector displacement = stand.getLocation().subtract(player.getLocation()).toVector();
            displacement.normalize();
            Vector v = player.getLocation().getDirection().multiply(2).subtract(displacement).normalize();
            stand.setVelocity(v.multiply(0.65));
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == Moves.GOJO_COLLAPSING_BLUE && player.getCooldown(move.move.getMaterial()) > 0) {
            ArmorStand stand = getStand(player.getUniqueId().toString());
            if (stand == null) {
                return;
            }

            Vector displacement = stand.getLocation().subtract(player.getLocation()).toVector();
            displacement.normalize();
            Vector v = player.getLocation().getDirection().multiply(0.5).subtract(displacement).normalize();
            stand.setVelocity(v.multiply(0.65));
        }
    }

    private ArmorStand getStand(String user) {
        stands.removeIf(ArmorStand::isDead);
        for (ArmorStand stand : stands) {
            if (stand.getScoreboardTags().contains(tag) && stand.getScoreboardTags().contains(user)) {
                return stand;
            }
        }
        return null;
    }
}
