package net.fameless.forcebattle.player;

import lombok.Getter;
import lombok.Setter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.event.ObjectiveUpdateEvent;
import net.fameless.forcebattle.event.PlayerExcludeEvent;
import net.fameless.forcebattle.event.PlayerResetEvent;
import net.fameless.forcebattle.game.GameListener;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.ItemUtils;
import net.fameless.forcebattle.util.StructureUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BattlePlayer implements CommandCaller {

    public static final List<BattlePlayer> BATTLE_PLAYERS = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + BattlePlayer.class.getSimpleName());

    @Getter
    private final UUID uuid;
    private final Inventory BACKPACK_INVENTORY;
    protected String name;
    @Getter
    private Objective objective;
    @Getter
    private boolean excluded;
    @Getter
    @Setter
    private int chainProgress = 0;
    @Getter
    @Setter
    private int points = 0;

    public BattlePlayer(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        BACKPACK_INVENTORY = Bukkit.createInventory(null, 27, Caption.getAsLegacy(
                        "gui.backpack_title", TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))))
        );
        BATTLE_PLAYERS.add(this);
    }

    public static @NotNull Optional<BattlePlayer> adapt(String name) {
        for (BattlePlayer battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.getName().equalsIgnoreCase(name)) {
                return Optional.of(battlePlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull Optional<BattlePlayer> adapt(UUID uuid) {
        for (BattlePlayer battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.getUuid().equals(uuid)) {
                return Optional.of(battlePlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull BattlePlayer adapt(Player object) {
        for (BattlePlayer battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.getUuid().equals(object.getUniqueId())) {
                return battlePlayer;
            }
        }
        return new BattlePlayer(object);
    }

    public static @NotNull List<BattlePlayer> getOnlinePlayers() {
        List<BattlePlayer> onlinePlayers = new ArrayList<>();
        for (BattlePlayer battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.isOffline()) {
                continue;
            }
            onlinePlayers.add(battlePlayer);
        }
        return onlinePlayers;
    }

    public static @NotNull List<UUID> getAllUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        for (BattlePlayer battlePlayer : BATTLE_PLAYERS) {
            uuids.add(battlePlayer.uuid);
        }
        return uuids;
    }

    public static Map<Integer, BattlePlayer> getPlaces() {
        Map<Integer, BattlePlayer> places = new HashMap<>();

        final Map<BattlePlayer, Integer> PLAYER_POINT_MAP = new HashMap<>();
        for (BattlePlayer player : BATTLE_PLAYERS) {
            PLAYER_POINT_MAP.put(player, player.getPoints());
        }

        List<Map.Entry<BattlePlayer, Integer>> sortedPlayers = PLAYER_POINT_MAP.entrySet()
                .stream()
                .sorted(Map.Entry.<BattlePlayer, Integer>comparingByValue().reversed())
                .toList();

        int place = 1;
        for (Map.Entry<BattlePlayer, Integer> entry : sortedPlayers) {
            places.put(place++, entry.getKey());
        }

        return places;
    }

    public static int getPlace(BattlePlayer player) {
        List<BattlePlayer> sortedPlayers = new ArrayList<>(BattlePlayer.BATTLE_PLAYERS);
        sortedPlayers.sort(Comparator.comparingInt(BattlePlayer::getPoints).reversed());

        int place = 1;
        int previousPoints = -1;

        for (int i = 0; i < sortedPlayers.size(); i++) {
            BattlePlayer current = sortedPlayers.get(i);

            if (current.getPoints() != previousPoints) {
                place = i + 1;
                previousPoints = current.getPoints();
            }

            if (current.equals(player)) {
                return place;
            }
        }

        return -1;
    }


    @Override
    public CallerType callerType() {
        return CallerType.PLAYER;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            if (getPlayer() == null) {
                return "N/A";
            }
            this.name = getPlayer().getName();
        }
        return this.name;
    }

    public Audience getAudience() {
        if (getPlayer() == null) {
            return Audience.empty();
        }
        return BukkitUtil.BUKKIT_AUDIENCES.player(getPlayer());
    }

    public Team getTeam() {
        for (Team team : Team.teams) {
            if (team.getPlayers().contains(this)) {
                return team;
            }
        }
        return null;
    }

    public boolean isInTeam() {
        return getTeam() != null;
    }

    public void updateObjective(boolean finishLast, boolean hasBeenSkipped) {
        if (finishLast && this.objective != null) {
            this.objective.setFinished(this);
            this.objective.setHasBeenSkipped(hasBeenSkipped);

            this.points++;
        }

        Objective newObjective = ForceBattle.getObjectiveManager().getNewObjective(this);
        if (newObjective == null) return;

        ObjectiveUpdateEvent updateEvent = new ObjectiveUpdateEvent(this, newObjective);
        Bukkit.getPluginManager().callEvent(updateEvent);
        if (updateEvent.isCancelled()) {
            logger.info("ObjectiveUpdateEvent has been denied by an external plugin.");
            return;
        }

        setCurrentObjective(updateEvent.getNewObjective(), false, false);

        if (SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE)) {
            increaseChainProgress();
        }
    }

    public void setCurrentObjective(Objective newObjective, boolean finishLast, boolean hasBeenSkipped) {
        if (finishLast && this.objective != null) {
            this.objective.setFinished(this);
            this.objective.setHasBeenSkipped(hasBeenSkipped);
            this.points++;
        }

        if (ForceBattle.getTimer().isRunning() && newObjective != null) {
            sendMessage(Caption.of(
                    "notification.next_objective",
                    TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(newObjective.getObjectiveString())))),
                    TagResolver.resolver("objective_type", Tag.inserting(Component.text(newObjective.getBattleType().getPrefix())))
            ));
        }

        this.objective = newObjective;
    }

    public void increaseChainProgress() {
        chainProgress++;
    }

    public void setExcluded(boolean excluded) {
        PlayerExcludeEvent excludeEvent = new PlayerExcludeEvent(this, excluded);
        Bukkit.getPluginManager().callEvent(excludeEvent);
        if (excludeEvent.isCancelled()) {
            logger.info("PlayerExcludeEvent has been denied by an external plugin.");
            return;
        }
        this.excluded = excludeEvent.isNewExcluded();
    }

    public void leaveTeam() {
        if (isInTeam()) {
            getTeam().removePlayer(this);
        }
    }

    public void addToTeam(@NotNull Team team) {
        leaveTeam();
        if (!isInTeam()) {
            team.addPlayer(this);
        }
    }

    public void reset(boolean newObjective) {
        PlayerResetEvent resetEvent = new PlayerResetEvent(this);
        Bukkit.getPluginManager().callEvent(resetEvent);
        if (resetEvent.isCancelled()) {
            logger.info("PlayerResetEvent has been denied by an external plugin.");
            return;
        }

        this.points = 0;
        this.chainProgress = 0;
        Objective.finished().forEach(objective -> {
            if (objective.getWhoFinished().equals(this)) {
                objective.delete();
            }
        });
        if (newObjective) {
            updateObjective(false, false);
        }

        handleReset();
    }

    public void sendMessage(Component message) {
        getAudience().sendMessage(message);
    }

    public void sendActionbar(Component message) {
        getAudience().sendActionBar(message);
    }

    public void openBackpack() {
        if (isInTeam()) {
            Team team = getTeam();
            team.getBACKPACK_INVENTORIES().getFirst().open(this);
        } else {
            openInventory(getBackpack());
        }
    }

    public void openBackpack(BattlePlayer viewer) {
        viewer.openInventory(getBackpack());
    }

    public void playSound(Sound sound) {
        getAudience().playSound(sound);
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        if (getPlayer() == null) {
            return false;
        }
        return getPlayer().hasPermission(permission);
    }

    public void addPlayerSkip(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getPlayerSkipItem(amount));
    }

    public void addTeamSkip(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getTeamSkipItem(amount));
    }

    public void addSwap(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSwapItem(amount));
    }

    public void removePlayerSkip(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().removeItem(ItemUtils.SpecialItems.getPlayerSkipItem(amount));
    }

    public void removeTeamSkip(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().removeItem(ItemUtils.SpecialItems.getTeamSkipItem(amount));
    }

    public void removeSwap(final int amount) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().getInventory().removeItem(ItemUtils.SpecialItems.getSwapItem(amount));
    }

    public void handleReset() {
        if (getPlayer() == null) return;
        if (!GameListener.spawnCreated) {
            StructureUtil.createSpawn();
            GameListener.spawnCreated = true;
        }

        getPlayer().setFoodLevel(20);
        getPlayer().setHealth(20);
        getPlayer().setSaturation(20);
        getPlayer().setFireTicks(0);
        getPlayer().getInventory().clear();
        getPlayer().setGameMode(GameMode.ADVENTURE);
        getPlayer().teleport(GameListener.spawn);
    }

    public boolean isOffline() {
        if (getPlayer() == null) {
            return true;
        }
        return !getPlayer().isOnline();
    }

    public void openInventory(Inventory inventory) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().openInventory(inventory);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().setScoreboard(scoreboard);
    }

    public void teleport(Location location) {
        if (getPlayer() == null) {
            return;
        }
        getPlayer().teleport(location);
    }

    public Inventory getInventory() {
        if (getPlayer() == null) {
            return null;
        }
        return getPlayer().getInventory();
    }

    public World getWorld() {
        if (getPlayer() == null) {
            return null;
        }
        return getPlayer().getWorld();
    }

    public Inventory getBackpack() {
        return BACKPACK_INVENTORY;
    }
}
