package net.fameless.core.player;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.game.Objective;
import net.fameless.core.game.Team;
import net.fameless.core.util.Format;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BattlePlayer<PlatformPlayer> implements CommandCaller {

    public static final List<BattlePlayer<?>> BATTLE_PLAYERS = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + BattlePlayer.class.getSimpleName());

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

        if (newObjective != null) {
            if (ForceBattle.getTimer().isRunning()) {
                sendMessage(Caption.of(
                        "notification.next_objective",
                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(newObjective.getObjectiveString())))),
                        TagResolver.resolver("objective_type", Tag.inserting(Component.text(newObjective.getBattleType().getPrefix())))
                ));
            }
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
        this.excluded = excluded;
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

    public void leaveTeam() {
        if (getTeam() != null) {
            getTeam().removePlayer(this);
        }
    }

    public void addToTeam(@NotNull Team team) {
        leaveTeam();
        if (getTeam() == null) {
            team.addPlayer(this);
        }
    }

    public void reset(boolean newObjective) {
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
        setCurrentObjective(newObjective, finishLast);
        if (PluginConfig.get().getBoolean("settings.enable-chain-mode", false)) {
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

    public abstract void handleReset(); // Method to handle player reset logic on the platform side - reset health, etc.

}
