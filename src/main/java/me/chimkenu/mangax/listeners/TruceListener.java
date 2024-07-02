package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.events.MoveTargetEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class TruceListener implements Listener {
    private final ArrayList<Truce> truces;

    /**
     * Makes the truce listener. Only one should exist per MangaX instance.
     */
    public TruceListener() {
        this.truces = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveTarget(MoveTargetEvent e) {
        Truce truce = new Truce(e.getSource(), e.getTarget());
        if (truces.contains(truce)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent e) {
        if (e.isCancelled()) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }


    }

    /**
     * Adds a truce between two living entities. Does nothing if it already exists.
     *
     * @param one first living entity
     * @param two second living entity
     */
    public void addTruce(LivingEntity one, LivingEntity two) {
        if (!truces.contains(new Truce(one, two)))
            truces.add(new Truce(one, two));
    }

    /**
     * Removes a truce between two living entities. Does nothing if it didn't exist.
     *
     * @param one first living entity
     * @param two second living entity
     */
    public void removeTruce(LivingEntity one, LivingEntity two) {
        truces.remove(new Truce(one, two));
    }

    /**
     * Returns {@code true} if a truce exists between the two entities
     *
     * @param one first living entity
     * @param two second living entity
     * @return {@code true} if list contains a truce containing the two entities
     */
    public boolean containsTruce(LivingEntity one, LivingEntity two) {
        return truces.contains(new Truce(one, two));
    }

    /**
     * Gets a list of an entity's truces
     *
     * @param entity first living entity
     * @return an immutable list containing the truces of the provided entity
     */
    public List<Truce> getTruceList(LivingEntity entity) {
        return truces.stream().filter(truce -> truce.contains(entity)).toList();
    }

    /**
     * A tuple of two living entities. Used for TruceListener and Truce command
     *
     * @param one first entity, used in player to player truces as the one who proposed the truce
     * @param two second entity
     */
    public record Truce(LivingEntity one, LivingEntity two) {
        @Override
        public boolean equals(Object o) {
            if (o instanceof Truce other) {
                return (other.one == this.one || other.one == this.two) && (other.two == this.one || other.two == this.two);
            }
            return false;
        }

        /**
         * Returns {@code true} if the provided entity is one of the parties in the truce
         *
         * @param entity the entity to check
         * @return {@code true} if the entity is one of the parties in the truce
         */
        public boolean contains(LivingEntity entity) {
            return one == entity || two == entity;
        }
    }
}
