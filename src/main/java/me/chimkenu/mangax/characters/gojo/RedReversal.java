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

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;
import static net.kyori.adventure.text.Component.text;

public class RedReversal extends Move implements Listener {
    public static final String tag = "GOJO_RED_REVERSAL";
    private final HashSet<ArmorStand> stands = new HashSet<>();

    public RedReversal() {
        super(null, null, 20 * 8, 20 * 14, Material.RED_CONCRETE, Component.text("Red Reversal", NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Location loc = getRelativeLocation(entity.getEyeLocation(), 0, 0, 2, 0, 0);
            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
            setUpArmorStand(stand);
            stand.addScoreboardTag(tag);
            stand.addScoreboardTag(entity.getUniqueId().toString());
            stand.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime(), 3, false, false, false));
            stands.add(stand);
            stand.setGravity(true);

            new BukkitRunnable() {
                int t = getFollowUpTime();
                @Override
                public void run() {
                    // show action bar charge up bar
                    Component bar = text("[", NamedTextColor.RED);
                    for (int i = 0; i < getFollowUpTime() / 8; i++) {
                        bar = bar.append(text("|", i < t / 8 ? NamedTextColor.RED: NamedTextColor.GRAY));
                    }
                    bar = bar.append(text("]", NamedTextColor.RED));
                    entity.sendActionBar(bar);

                    if (t <= 0 || entity.isDead() || stand.isDead()) {
                        stand.remove();
                        cancel();

                        if (entity instanceof Player player) {
                            player.setCooldown(getMaterial(), getCooldown());
                        }

                        return;
                    }

                    t--;

                    HollowPurple.makeSphere(stand.getLocation(), getColor(t), 50, 0.4);
                    stand.setVelocity(stand.getVelocity().add(new Vector(0, 0.06, 0)));

                    for (LivingEntity e : stand.getLocation().getNearbyLivingEntities(3)) {
                        if (e instanceof ArmorStand) {
                            if (e.getScoreboardTags().contains(CollapsingBlue.tag)) {
                                Vector direction = e.getLocation().toVector().subtract(stand.getLocation().toVector());
                                Vector v = e.getVelocity().add(direction.multiply(-0.1));
                                e.setVelocity(v);
                            }
                        } else {
                            if (t < getFollowUpTime() - 20 && e != entity && e.hasGravity() && stand.getLocation().distanceSquared(e.getLocation()) < 2 * 2) {
                                t = 0;

                                Vector direction = e.getLocation().toVector().subtract(stand.getLocation().toVector());
                                Vector v = null;

                                double distance = direction.lengthSquared();
                                if (distance < 0.0001) {
                                    v = new Vector();
                                }

                                direction = direction.normalize();
                                direction.setY(Math.abs(direction.getY()));
                                v = v == null ? direction.multiply(2) : v;

                                MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_RED_REVERSAL, entity, e, 7, v);
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return;
                                }

                                e.damage(event.getDamage(), entity);
                                if (event.getKnockback().lengthSquared() > 0)
                                    e.setVelocity(event.getKnockback());
                            }
                        }
                    }
                }

                private Color getColor(int t) {
                    if (t > getFollowUpTime() - 20) {
                        double amount = getFollowUpTime() - t;
                        amount /= 20;
                        amount = 1 - amount;
                        return Color.fromRGB(255, (int) (amount * 100), 0);
                    } else {
                        return Color.RED;
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
        if (move == Moves.GOJO_RED_REVERSAL && player.getCooldown(move.move.getMaterial()) > 0) {
            ArmorStand stand = getStand(player.getUniqueId().toString());
            if (stand == null) {
                return;
            }

            Vector displacement = stand.getEyeLocation().subtract(player.getLocation()).toVector();
            displacement.normalize();
            Vector v = player.getLocation().getDirection().multiply(2).subtract(displacement).normalize();
            stand.setVelocity(v.multiply(0.65));
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (e.getAction().isRightClick() && move == Moves.GOJO_RED_REVERSAL && player.getCooldown(move.move.getMaterial()) > 0) {
            ArmorStand stand = getStand(player.getUniqueId().toString());
            if (stand == null) {
                return;
            }

            Vector displacement = stand.getEyeLocation().subtract(player.getLocation()).toVector();
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
