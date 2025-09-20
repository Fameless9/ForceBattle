package net.fameless.spigot.player;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.player.BattlePlayer;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.util.BackpackInventoryHolder;
import net.fameless.spigot.util.BukkitUtil;
import net.fameless.spigot.util.ItemUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BukkitPlayer extends BattlePlayer<Player> {

    public static final List<BukkitPlayer> BUKKIT_PLAYERS = new ArrayList<>();

    private final Inventory BACKPACK_INVENTORY;

    public BukkitPlayer(@NotNull Player player) {
        super(player.getUniqueId());
        this.name = player.getName();
        BACKPACK_INVENTORY = Bukkit.createInventory(new BackpackInventoryHolder(), 27, Caption.getAsLegacy("gui.backpack_title",
                TagResolver.resolver("player", Tag.inserting(Component.text(player.getName())))));
        BUKKIT_PLAYERS.add(this);
    }

    public static @NotNull Optional<BukkitPlayer> adapt(String name) {
        for (BukkitPlayer bukkitPlayer : BUKKIT_PLAYERS) {
            if (bukkitPlayer.getName().equals(name)) {
                return Optional.of(bukkitPlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull Optional<BukkitPlayer> adapt(UUID uuid) {
        for (BukkitPlayer bukkitPlayer : BUKKIT_PLAYERS) {
            if (bukkitPlayer.getUniqueId().equals(uuid)) {
                return Optional.of(bukkitPlayer);
            }
        }
        return Optional.empty();
    }

    public static @NotNull BukkitPlayer adapt(Player object) {
        for (BukkitPlayer bukkitPlayer : BUKKIT_PLAYERS) {
            if (bukkitPlayer.getUniqueId().equals(object.getUniqueId())) {
                return bukkitPlayer;
            }
        }
        return new BukkitPlayer(object);
    }

    public static @NotNull Optional<BukkitPlayer> adapt(@NotNull BattlePlayer<?> battlePlayer) {
        if (battlePlayer.getPlatformPlayer() instanceof Player player) {
            return Optional.of(BukkitPlayer.adapt(player));
        }
        return Optional.empty();
    }

    @Override
    public CallerType callerType() {
        return CallerType.PLAYER;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            if (getPlatformPlayer() == null) {
                return "N/A";
            }
            this.name = getPlatformPlayer().getName();
        }
        return this.name;
    }

    @Override
    public Audience getAudience() {
        if (getPlatformPlayer() == null) {
            return Audience.empty();
        }
        return BukkitUtil.BUKKIT_AUDIENCES.player(getPlatformPlayer());
    }

    @Override
    public @Nullable Player getPlatformPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        if (getPlatformPlayer() == null) {
            return false;
        }
        return getPlatformPlayer().hasPermission(permission);
    }

    @Override
    public void teleportToSpawnLocation() {
        if (getPlatformPlayer() == null) {
            return;
        }
        teleport(getWorld().getSpawnLocation());
    }

    @Override
    public void addSkip(final int amount) {
        if (getPlatformPlayer() == null) {
            return;
        }
        getPlatformPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSkipItem(amount));
    }

    @Override
    public void addSwap(final int amount) {
        if (getPlatformPlayer() == null) {
            return;
        }
        getPlatformPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSwapItem(amount));
    }

    @Override
    public void removeSkip(final int amount) {
        if (getPlatformPlayer() == null) {
            return;
        }
        getPlatformPlayer().getInventory().removeItem(ItemUtils.SpecialItems.getSkipItem(amount));
    }

    @Override
    public void removeSwap(final int amount) {
        if (getPlatformPlayer() == null) {
            return;
        }
        getPlatformPlayer().getInventory().removeItem(ItemUtils.SpecialItems.getSwapItem(amount));
    }

    @Override
    public void handleReset() {
        if (getPlatformPlayer() == null) return;
        getPlatformPlayer().setFoodLevel(20);
        getPlatformPlayer().setHealth(20);
        getPlatformPlayer().setSaturation(20);
        getPlatformPlayer().setFireTicks(0);
        getPlatformPlayer().getInventory().clear();

        addSkip(BukkitPlatform.get().getConfig().getInt("settings.skips", 3));
        addSwap(BukkitPlatform.get().getConfig().getInt("settings.swaps", 1));
    }

    @Override
    public boolean isOffline() {
        if (getPlatformPlayer() == null) {
            return true;
        }
        return !getPlatformPlayer().isOnline();
    }

    @Override
    public <platformInventory> void openInventory(platformInventory inventory) {
        if (getPlatformPlayer() == null) {
            return;
        }
        if (inventory instanceof Inventory inv) {
            getPlatformPlayer().openInventory(inv);
        } else {
            throw new IllegalArgumentException("Not a bukkit inventory");
        }
    }

    @Override
    public <platformScoreboard> void setScoreboard(platformScoreboard scoreboard) {
        if (getPlatformPlayer() == null) {
            return;
        }
        if (scoreboard instanceof Scoreboard sb) {
            getPlatformPlayer().setScoreboard(sb);
        } else {
            throw new IllegalArgumentException("Not a bukkit scoreboard");
        }
    }


    @Override
    public <platformLocation> void teleport(platformLocation location) {
        if (getPlatformPlayer() == null) {
            return;
        }
        if (location instanceof Location bukkitLoc) {
            getPlatformPlayer().teleport(bukkitLoc);
        } else {
            throw new IllegalArgumentException("Not a bukkit location");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Inventory getInventory() {
        if (getPlatformPlayer() == null) {
            return null;
        }
        return getPlatformPlayer().getInventory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public World getWorld() {
        if (getPlatformPlayer() == null) {
            return null;
        }
        return getPlatformPlayer().getWorld();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Inventory getBackpack() {
        return BACKPACK_INVENTORY;
    }

}
