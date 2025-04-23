package net.fameless.forceBattle.player;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.configuration.SettingsManager;
import net.fameless.forceBattle.event.EventDispatcher;
import net.fameless.forceBattle.event.ObjectiveUpdateEvent;
import net.fameless.forceBattle.event.PlayerExcludeEvent;
import net.fameless.forceBattle.event.PlayerResetEvent;
import net.fameless.forceBattle.event.PlayerTeamJoinEvent;
import net.fameless.forceBattle.event.PlayerTeamLeaveEvent;
import net.fameless.forceBattle.game.Objective;
import net.fameless.forceBattle.game.Team;
import net.fameless.forceBattle.util.Format;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BattlePlayer<PlatformPlayer> implements CommandCaller {

    public static final List<BattlePlayer<?>> BATTLE_PLAYERS = new ArrayList<>();
    private final UUID uuid;
    protected String name;
    private Objective objective;
    private boolean excluded;
    private int chainProgress = 0;
    private boolean receivedSkip = false;
    private boolean receivedSwap = false;
    private int points = 0;

    public BattlePlayer(UUID uuid) {
        this.uuid = uuid;
        BATTLE_PLAYERS.add(this);
    }

    public static @NotNull Optional<BattlePlayer<?>> of(String name) {
        for (BattlePlayer<?> battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.getName().equalsIgnoreCase(name)) {
                return Optional.of(battlePlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull Optional<BattlePlayer<?>> of(UUID uuid) {
        for (BattlePlayer<?> battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.getUniqueId().equals(uuid)) {
                return Optional.of(battlePlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull List<BattlePlayer<?>> getOnlinePlayers() {
        List<BattlePlayer<?>> onlinePlayers = new ArrayList<>();
        for (BattlePlayer<?> battlePlayer : BATTLE_PLAYERS) {
            if (battlePlayer.isOffline()) {
                continue;
            }
            onlinePlayers.add(battlePlayer);
        }
        return onlinePlayers;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Team getTeam() {
        for (Team team : Team.teams) {
            if (team.getPlayers().contains(this)) {
                return team;
            }
        }
        return null;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setCurrentObjective(Objective newObjective, boolean finishLast) {
        if (finishLast) {
            this.objective.setFinished(this);
            this.points++;
        }

        if (ForceBattle.getTimer().isRunning()) {
            sendMessage(Caption.of(
                    "notification.next_objective",
                    TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(newObjective.getObjectiveString())))),
                    TagResolver.resolver("objective_type", Tag.inserting(Component.text(newObjective.getBattleType().getPrefix())))
            ));
        }

        this.objective = newObjective;
    }

    public int getChainProgress() {
        return chainProgress;
    }

    public void setChainProgress(int chainProgress) {
        this.chainProgress = chainProgress;
    }

    public void increaseChainProgress() {
        chainProgress++;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        PlayerExcludeEvent excludeEvent = new PlayerExcludeEvent(this, excluded);
        EventDispatcher.post(excludeEvent);
        if (excludeEvent.isCancelled()) {
            ForceBattle.logger().info("PlayerExcludeEvent has been denied by an external plugin.");
            return;
        }
        this.excluded = excludeEvent.isNewExcluded();
    }

    public boolean hasReceivedSkip() {
        return receivedSkip;
    }

    public void setReceivedSkip(boolean receivedSkip) {
        this.receivedSkip = receivedSkip;
    }

    public boolean hasReceivedSwap() {
        return receivedSwap;
    }

    public void setReceivedSwap(boolean receivedSwap) {
        this.receivedSwap = receivedSwap;
    }

    public boolean leaveTeam() {
        if (getTeam() == null) {
            return false;
        }
        PlayerTeamLeaveEvent teamLeaveEvent = new PlayerTeamLeaveEvent(getTeam(), this);
        EventDispatcher.post(teamLeaveEvent);
        if (teamLeaveEvent.isCancelled()) {
            ForceBattle.logger().info("PlayerTeamLeaveEvent has been denied by an external plugin.");
            return false;
        }
        getTeam().removePlayer(this);
        return true;
    }

    public void addToTeam(@NotNull Team team) {
        PlayerTeamJoinEvent teamJoinEvent = new PlayerTeamJoinEvent(team, this);
        EventDispatcher.post(teamJoinEvent);
        if (teamJoinEvent.isCancelled()) {
            ForceBattle.logger().info("PlayerTeamJoinEvent has been denied by an external plugin.");
            return;
        }
        // Check if PlayerTeamLeaveEvent has been denied
        if (leaveTeam()) {
            team.addPlayer(this);
        }
    }

    public void reset(boolean newObjective) {
        PlayerResetEvent resetEvent = new PlayerResetEvent(this);
        EventDispatcher.post(resetEvent);
        if (resetEvent.isCancelled()) {
            ForceBattle.logger().info("PlayerResetEvent has been denied by an external plugin.");
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
            updateObjective(false);
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
        openInventory(getBackpack());
    }

    public void updateObjective(boolean finishLast) {
        Objective newObjective = ForceBattle.getObjectiveManager().getNewObjective(this);
        ObjectiveUpdateEvent updateEvent = new ObjectiveUpdateEvent(this, newObjective);
        EventDispatcher.post(updateEvent);
        if (updateEvent.isCancelled()) {
            ForceBattle.logger().info("ObjectiveUpdateEvent has been denied by an external plugin.");
            return;
        }
        setCurrentObjective(updateEvent.getNewObjective(), finishLast);
        if (SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE)) {
            increaseChainProgress();
        }
    }

    public void playSound(Sound sound) {
        getAudience().playSound(sound);
    }

    public abstract String getName();

    public abstract Audience getAudience();

    public abstract PlatformPlayer getPlatformPlayer();

    public abstract boolean isOffline();

    public abstract <platformInventory> platformInventory getInventory();

    public abstract <platformInventory> void openInventory(platformInventory inventory);

    public abstract <platformScoreboard> void setScoreboard(platformScoreboard scoreboard);

    public abstract <platformLocation> void teleport(platformLocation location);

    public abstract <platformWorld> platformWorld getWorld();

    public abstract <platformInventory> platformInventory getBackpack();

    public abstract boolean hasPermission(String permission);

    public abstract void teleportToSpawnLocation();

    public abstract void addSkip(int amount);

    public abstract void addSwap(int amount);

    public abstract void removeSkip(int amount);

    public abstract void removeSwap(int amount);

    // Method to handle player reset logic on the platform side - reset hunger, etc.
    public abstract void handleReset();

}
