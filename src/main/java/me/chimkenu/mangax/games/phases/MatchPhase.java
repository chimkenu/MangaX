package me.chimkenu.mangax.games.phases;

import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.games.Phase;
import me.chimkenu.mangax.games.TeamPlayers;
import me.chimkenu.mangax.worlddata.Position;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

public class MatchPhase implements Phase, Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final TeamPlayers players;

    private final Location spawnA;
    private final Location spawnB;

    int round;
    int teamOneWins;
    int teamTwoWins;

    int pause;
    int gracePeriod;
    int time;

    boolean hasWinner;

    public MatchPhase(@NotNull JavaPlugin plugin, @NotNull World world, @NotNull TeamPlayers players) {
        this.plugin = plugin;
        this.world = world;
        this.players = players;

        // get all the data
        WorldData data = WorldData.SPAWN_POSITIONS;
        Position[] spawnPositions = (Position[]) data.data.retrieveFrom(world, data.getKey());
        if (spawnPositions == null || spawnPositions.length < 2) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }

        spawnA = spawnPositions[0].toLocation(world);
        spawnB = spawnPositions[1].toLocation(world);
    }

    @Override
    public void start() {
        round = 0;
        teamOneWins = 0;
        teamTwoWins = 0;
        time = 0;
        pause = 0;
        gracePeriod = 100;
        hasWinner = false;

        reset();
    }

    @Override
    public boolean tick() {
        if (pause > 0) {
            pause--;
            if (pause == 0) reset();
            return true;
        }

        if (hasWinner)
            return false;

        time++;
        if (gracePeriod > 0) {
            if (gracePeriod == 100) {
                players.forEach(p -> p.showTitle(Title.title(text("Round " + round).decorate(TextDecoration.BOLD), text(""), Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500)))));
            }

            gracePeriod--;
            players.forEach(p -> p.sendActionBar(text("Grace period: ")
                    .append(text((gracePeriod - (gracePeriod % 20)) / 20))));
            if (gracePeriod == 0) {
                players.forEach(p -> {
                    p.sendActionBar(text("Fight!", NamedTextColor.RED).decorate(TextDecoration.BOLD));
                    p.removePotionEffect(PotionEffectType.HUNGER);
                });
            }
        }
        return true;
    }

    @Override
    public void stop() {
        players.forEach(p -> {
            // un-hide all players from each other
            players.forEach(q -> q.showPlayer(plugin, p));

            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.removePotionEffect(PotionEffectType.HUNGER);
        });
    }

    private void reset() {
        round++;
        players.forEach(p -> {
            // un-hide all players from each other
            players.forEach(q -> q.showPlayer(plugin, p));

            // remove cooldowns
            for (int i = 0; i < 9; i++) {
                ItemStack item = p.getInventory().getItem(i);
                if (item != null) p.setCooldown(item.getType(), 0);
            }

            p.setAllowFlight(false);
            p.setFlying(false);

            p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1000, 0, false, false, false));
            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(20);
            p.setFoodLevel(20);
        });

        players.getTeamOne().forEach(p -> p.teleport(spawnA));
        players.getTeamTwo().forEach(p -> p.teleport(spawnB));
        gracePeriod = 100; // reset grace period
    }

    private void checkWinConditions() {
        if (isAllDead(players.getTeamOne())) {
            doWinEffects(players.getTeamTwo());
            teamTwoWins++;
        } else if (isAllDead(players.getTeamTwo())) {
            doWinEffects(players.getTeamOne());
            teamOneWins++;
        }

        if (teamOneWins >= 2) {
            doWinEffects(players.getTeamOne());
            StringBuilder winners = new StringBuilder();
            for (Player player : players.getTeamOne()) {
                winners.append(player.getName()).append(", ");
            }
            winners.deleteCharAt(winners.length() - 1);
            winners.deleteCharAt(winners.length() - 1);

            players.forEach(p -> p.sendMessage(text("Game end! Winners: " + winners, NamedTextColor.YELLOW)));
            hasWinner = true;
        } else if (teamTwoWins >= 2) {
            doWinEffects(players.getTeamTwo());
            StringBuilder winners = new StringBuilder();
            for (Player player : players.getTeamTwo()) {
                winners.append(player.getName()).append(", ");
            }
            winners.deleteCharAt(winners.length() - 1);
            winners.deleteCharAt(winners.length() - 1);

            players.forEach(p -> p.sendMessage(text("Game end! Winners: " + winners, NamedTextColor.YELLOW)));
            hasWinner = true;
        }
    }

    private void doWinEffects(Set<Player> winningTeam) {
        pause = 100;

        StringBuilder winners = new StringBuilder();
        for (Player player : winningTeam) {
            winners.append(player.getName()).append(", ");
        }
        winners.deleteCharAt(winners.length() - 1);
        winners.deleteCharAt(winners.length() - 1);

        players.forEach(p -> {
            p.showTitle(Title.title(
                    text("Round end!").decorate(TextDecoration.BOLD),
                    text("Winners: " + winners)));

            p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0, false, false, false));
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setVelocity(new Vector(0, 2, 0));
        });
    }

    private boolean isAllDead(Set<Player> players) {
        boolean isAllDead = true;
        for (Player player : players) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                isAllDead = false;
            }
        }
        return isAllDead;
    }

    @EventHandler
    public void onMoveTarget(MoveTargetEvent e) {
        if (e.getSource() instanceof Player source && e.getTarget() instanceof Player target) {
            if (players.areTeammates(source, target) || target.getGameMode() == GameMode.SURVIVAL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player && player.getGameMode() == GameMode.SURVIVAL && world.getPlayers().contains(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        Location loc = player.getLocation();
        if (players.containsPlayer(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                    player.teleport(loc);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.setVelocity(new Vector(0, 2, 0));
                    player.sendActionBar(text("You have died, please wait for the round to end.", NamedTextColor.RED));

                    players.forEach(p -> {
                        if (p != player) {
                            p.hidePlayer(plugin, player);
                        }
                    });

                    checkWinConditions();
                }
            }.runTaskLater(plugin, 1);
        }
    }
}
