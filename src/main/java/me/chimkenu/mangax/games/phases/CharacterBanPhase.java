package me.chimkenu.mangax.games.phases;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.games.Phase;
import me.chimkenu.mangax.games.TeamPlayers;
import me.chimkenu.mangax.utils.RayTrace;
import me.chimkenu.mangax.worlddata.Position;
import me.chimkenu.mangax.worlddata.Region;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.text;

public class CharacterBanPhase implements Phase, Listener {
    public static final String bannedCharacterTag = "MANGAX_BANNED";

    private final JavaPlugin plugin;
    private final World world;
    private final TeamPlayers players;

    private final BoundingBox region;
    private final Location spectatorSpawn;

    private final Queue<Player> banQueue;
    private final HashSet<Characters> bannedCharacters;
    private final int maxBans;
    private int numOfBans;

    private Player active;
    private Characters selected;
    private int time;
    private boolean isAnimationActive;

    public CharacterBanPhase(JavaPlugin plugin, World world, TeamPlayers players) {
        this.plugin = plugin;
        this.world = world;
        this.players = players;

        bannedCharacters = new HashSet<>();
        maxBans = 2;
        // maxBans = Math.min(2, Math.max(0, (int) Math.floor(players.size() / 2f) - 1)) * 2;

        WorldData data = WorldData.CHARACTER_SELECTION_LOCATION_A;
        Position position = (Position) data.data.retrieveFrom(world, data.getKey());
        if (position == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        spectatorSpawn = position.toLocation(world);

        data = WorldData.CHARACTER_SELECTION_REGION_A;
        Region r = (Region) data.data.retrieveFrom(world, data.getKey());
        if (r == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        region = r.toBoundingBox();



        banQueue = new LinkedList<>();
        Iterator<Player> teamOne = players.getTeamOne().iterator();
        Iterator<Player> teamTwo = players.getTeamTwo().iterator();
        for (int i = 0; i < maxBans; i++) {
            if (i % 2 == 0) {
                if (!teamOne.hasNext())
                    teamOne = players.getTeamOne().iterator();
                banQueue.add(teamOne.next());
            } else {
                if (!teamTwo.hasNext())
                    teamTwo = players.getTeamTwo().iterator();
                banQueue.add(teamTwo.next());
            }
        }
    }

    @Override
    public void start() {
        isAnimationActive = false;
        players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));

        // remove all bans
        for (LivingEntity entity : world.getLivingEntities()) {
            entity.removeScoreboardTag(bannedCharacterTag);
        }

        next();
    }

    @Override
    public boolean tick() {
        time--;
        if (numOfBans >= maxBans) {
            return false;
        }

        if (time < 0) {
            next();
            return true;
        }

        players.forEach(p -> p.sendActionBar(active.displayName()
                .append(text(" is banning a character... "))
                .append(text((time - (time % 20)) / 20))));

        if (!isAnimationActive) {
            ArmorStand target = getActiveTarget();
            if (target == null)
                return true;
            Characters character = getCharacterFromArmorStand(target);
            if (character == null)
                return true;

            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));

            if (bannedCharacters.contains(character)) {
                active.sendActionBar(text("This character is already banned.", NamedTextColor.RED));
            } else {
                active.sendActionBar(text().content("[").color(NamedTextColor.YELLOW)
                        .append(keybind("key.attack").color(NamedTextColor.YELLOW))
                        .append(text("]", NamedTextColor.YELLOW))
                        .append(text(" to ban ", NamedTextColor.WHITE))
                        .append(text(character.toString()))
                        .build());
            }
        }

        return true;
    }

    private void next() {
        if (active != null) {

            // ban only if player selected something
            if (selected != null) {
                players.forEach(p -> p.sendMessage(active.displayName().color(NamedTextColor.RED)
                        .append(text(" has banned "))
                        .append(text(selected.toString()))));
                bannedCharacters.add(selected);
            }

            numOfBans++; // increment regardless if player selected to ban
            active.setGameMode(GameMode.SPECTATOR);
        }
        if (banQueue.peek() != null) {
            // Reset timer (15 seconds) to pick a character
            time = 15 * 20;
            selected = null;

            active = banQueue.poll();
            active.setGameMode(GameMode.ADVENTURE);
            active.teleport(spectatorSpawn);
        }
    }

    @Override
    public void stop() {
        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity instanceof ArmorStand stand && bannedCharacters.contains(getCharacterFromArmorStand(stand))) {
                stand.addScoreboardTag(bannedCharacterTag);
            }
        }
    }

    private ArmorStand getActiveTarget() {
        // get the entity active player is looking at
        RayTrace ray = new RayTrace(active.getEyeLocation().toVector(), active.getEyeLocation().getDirection());
        ArrayList<Vector> points = ray.traverse(10, 0.1);

        for (LivingEntity entity : active.getLocation().getNearbyLivingEntities(10)) {
            if (!(entity instanceof ArmorStand target)) {
                continue;
            }

            if (!region.contains(target.getLocation().toVector())) {
                continue;
            }

            for (Vector point : points) {
                if (target.getBoundingBox().contains(point)) {
                    return target;
                }
            }
        }
        return null;
    }

    private @Nullable Characters getCharacterFromArmorStand(ArmorStand stand) {
        for (Characters character : Characters.values()) {
            if (stand.getScoreboardTags().contains(character.toString())) {
                return character;
            }
        }
        return null;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (players.containsPlayer(player)) {
            if (player == active && active.getLocation().distanceSquared(spectatorSpawn) > 0.1) {
                Location loc = spectatorSpawn.clone();
                loc.setPitch(active.getPitch());
                loc.setYaw(active.getYaw());
                player.teleport(loc);
            } else if (!region.contains(player.getLocation().toVector())) {
                player.teleport(spectatorSpawn);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerArmSwingEvent e) {
        if (e.getPlayer() != active) {
            return;
        }

        ArmorStand stand = getActiveTarget();
        if (stand == null)
            return;
        Characters character = getCharacterFromArmorStand(stand);
        if (character == null)
            return;

        if (bannedCharacters.contains(getCharacterFromArmorStand(stand))) {
            active.sendMessage(text("This character is already banned!", NamedTextColor.RED));
            return;
        }

        selected = character;
        time = 0;
    }

    public Set<Characters> getBannedCharacters() {
        return bannedCharacters;
    }
}
