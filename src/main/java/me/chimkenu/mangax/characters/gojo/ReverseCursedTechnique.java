package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ReverseCursedTechnique extends Move implements Punch, Listener {
    public final String tag = "GOJO_REVERSE_CURSED_TECHNIQUE";

    public ReverseCursedTechnique() {
        super(null, null, 10 * 20, 10 * 20, Material.WHITE_STAINED_GLASS, Component.text("Reverse Cursed Technique").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            entity.addScoreboardTag(tag);
            new BukkitRunnable() {
                int t = getFollowUpTime();
                @Override
                public void run() {
                    if (t < 0 || entity.isDead()) {
                        entity.removeScoreboardTag(tag);
                        cancel();
                        return;
                    }

                    entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, true));
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public @NotNull String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 1, 1, getFollowUpTime(), false);
    }

    @Override
    public void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp) {

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof LivingEntity entity)) {
            return;
        }

        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (entity.getScoreboardTags().contains(tag) && maxHealth != null) {
            entity.setHealth(Math.min(maxHealth.getValue(), entity.getHealth() + e.getDamage()));
        }
    }
}
