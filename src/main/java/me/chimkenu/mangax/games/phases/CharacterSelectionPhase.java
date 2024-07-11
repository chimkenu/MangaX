package me.chimkenu.mangax.games.phases;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.games.Phase;
import me.chimkenu.mangax.games.TeamPlayers;
import me.chimkenu.mangax.utils.PlayerDataUtil;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.text;

public class CharacterSelectionPhase implements Phase, Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final TeamPlayers players;

    private final TeamSelection teamOne;
    private final TeamSelection teamTwo;

    public CharacterSelectionPhase(@NotNull JavaPlugin plugin, @NotNull World world, @NotNull TeamPlayers players, @Nullable CharacterBanPhase banPhase) {
        this.plugin = plugin;
        this.world = world;
        this.players = players;

        // get all the data
        WorldData data = WorldData.CHARACTER_SELECTION_LOCATION_A;
        Position position = (Position) data.data.retrieveFrom(world, data.getKey());
        if (position == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        Location spawnA = position.toLocation(world);

        data = WorldData.CHARACTER_SELECTION_REGION_A;
        Region r = (Region) data.data.retrieveFrom(world, data.getKey());
        if (r == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        BoundingBox regionA = r.toBoundingBox();

        data = WorldData.CHARACTER_SELECTION_LOCATION_B;
        position = (Position) data.data.retrieveFrom(world, data.getKey());
        if (position == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        Location spawnB = position.toLocation(world);

        data = WorldData.CHARACTER_SELECTION_REGION_B;
        r = (Region) data.data.retrieveFrom(world, data.getKey());
        if (r == null) {
            throw new IllegalArgumentException("World does not have the necessary data!");
        }
        BoundingBox regionB = r.toBoundingBox();

        teamOne = new TeamSelection(players.getTeamOne(), regionA, spawnA, banPhase == null ? Set.of() : banPhase.getBannedCharacters());
        teamTwo = new TeamSelection(players.getTeamTwo(), regionB, spawnB, banPhase == null ? Set.of() : banPhase.getBannedCharacters());
    }

    @Override
    public void start() {
        players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));
        teamOne.start();
        teamTwo.start();
    }

    @Override
    public boolean tick() {
        boolean one = teamOne.tick();
        boolean two = teamTwo.tick();
        return one || two;
    }

    @Override
    public void stop() {
        players.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1000, 0, false, false, false)));
        teamOne.giveItems();
        teamTwo.giveItems();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        teamOne.onMove(e);
        teamTwo.onMove(e);
    }

    @EventHandler
    public void onClick(PlayerArmSwingEvent e) {
        teamOne.onClick(e);
        teamTwo.onClick(e);
    }

    private static class TeamSelection {
        private final Set<Player> players;
        private final BoundingBox region;
        private final Location spawn;

        private final Set<Characters> bannedCharacters;
        private final HashMap<Player, Characters> selectedCharacters;
        private final Queue<Player> selectionQueue;
        private Player active;
        private Characters selected;
        private int time;
        private boolean isAnimationActive;

        private TeamSelection(Set<Player> players, BoundingBox region, Location spawn, Set<Characters> bannedCharacters) {
            this.players = players;
            this.region = region;
            this.spawn = spawn;
            this.bannedCharacters = bannedCharacters;

            selectedCharacters = new HashMap<>();
            selectionQueue = new LinkedList<>();
            selectionQueue.addAll(players);
        }

        public void start() {
            isAnimationActive = false;
            next();
        }

        public boolean tick() {
            time--;
            if (selectedCharacters.size() == players.size()) {
                players.forEach(p -> p.sendActionBar(text("Waiting...", NamedTextColor.GRAY)));
                return false;
            }

            if (time < 0) {
                next();
                return true;
            }

            players.forEach(p -> p.sendActionBar(active.displayName()
                    .append(text(" is picking a character... "))
                    .append(text((time - (time % 20)) / 20))));

            if (!isAnimationActive) {
                ArmorStand target = getActiveTarget();
                if (target == null)
                    return true;
                Characters character = getCharacterFromArmorStand(target);
                if (character == null)
                    return true;

                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));

                if (selectedCharacters.containsValue(getCharacterFromArmorStand(target))) {
                    active.sendActionBar(text("This character is already banned.", NamedTextColor.RED));
                } else {
                    active.sendActionBar(text().content("[").color(NamedTextColor.YELLOW)
                            .append(keybind("key.attack").color(NamedTextColor.YELLOW))
                            .append(text("]", NamedTextColor.YELLOW))
                            .append(text(" to select ", NamedTextColor.WHITE))
                            .append(text(character.toString()))
                            .build());
                }
            }

            return true;
        }

        private void next() {
            if (active != null) {
                players.forEach(p -> p.sendMessage(active.displayName().color(NamedTextColor.RED)
                        .append(text(" has selected "))
                        .append(text(selected.toString()))));
                selectedCharacters.put(active, selected);

                active.setGameMode(GameMode.SPECTATOR);
            }

            active = selectionQueue.poll();
            if (active != null) {
                // Reset timer (15 seconds) to pick a character
                time = 15 * 20;
                selected = Arrays.stream(Characters.values()).filter(c -> !bannedCharacters.contains(c) && !selectedCharacters.containsValue(c)).toList().getFirst();

                active.setGameMode(GameMode.ADVENTURE);
                active.teleport(spawn);
            }
        }

        public void giveItems() {
            selectedCharacters.forEach((player, character) -> {
                List<Moves> moves = PlayerDataUtil.getMoves(player, character);
                for (int i = 0; i < 9; i++) {
                    player.getInventory().setItem(i, moves.get(i).move.getItem());
                }
            });
        }

        public void onMove(PlayerMoveEvent e) {
            Player player = e.getPlayer();
            if (players.contains(player)) {
                if (player == active && active.getLocation().distanceSquared(spawn) > 0.1) {
                    Location loc = spawn.clone();
                    loc.setPitch(active.getPitch());
                    loc.setYaw(active.getYaw());
                    player.teleport(loc);
                } else if (!region.contains(player.getLocation().toVector())) {
                    player.teleport(spawn);
                }
            }
        }

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

            if (stand.getScoreboardTags().contains(CharacterBanPhase.bannedCharacterTag)) {
                active.sendMessage(text("This character is banned, please pick another one.", NamedTextColor.RED));
                return;
            }

            if (selectedCharacters.containsValue(character)) {
                active.sendMessage(text("This character has already been selected!", NamedTextColor.RED));
                return;
            }

            selected = character;
            time = 0;
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
    }
}
