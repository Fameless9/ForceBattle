package net.fameless.spigot.gui;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.Command;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import net.fameless.core.util.Format;
import net.fameless.spigot.player.BukkitPlayer;
import net.fameless.spigot.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SettingsGUI implements Listener, InventoryHolder {

    private static SettingsGUI holderInstance;

    public SettingsGUI() {
        holderInstance = this;

        Command.forId("settings")
                .ifPresent(command -> command.onExecute(
                        (caller, args) -> ((BattlePlayer<?>) caller).openInventory(getSettingsGUI())
                ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsGUI)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        event.setCancelled(true);
        BukkitPlayer whoClicked = BukkitPlayer.adapt(player);
        switch (event.getSlot()) {
            case 0 -> {
                BattleType.FORCE_ITEM.setEnabled(!BattleType.FORCE_ITEM.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_ITEM.isEnabled()
                        ? "notification.force_item_enabled"
                        : "notification.force_item_disabled"));
            }
            case 1 -> {
                BattleType.FORCE_MOB.setEnabled(!BattleType.FORCE_MOB.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_MOB.isEnabled()
                        ? "notification.force_mob_enabled"
                        : "notification.force_mob_disabled"));
            }
            case 2 -> {
                BattleType.FORCE_BIOME.setEnabled(!BattleType.FORCE_BIOME.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_BIOME.isEnabled()
                        ? "notification.force_biome_enabled"
                        : "notification.force_biome_disabled"));
            }
            case 9 -> {
                BattleType.FORCE_ADVANCEMENT.setEnabled(!BattleType.FORCE_ADVANCEMENT.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_ADVANCEMENT.isEnabled()
                        ? "notification.force_advancement_enabled"
                        : "notification.force_advancement_disabled"));
            }
            case 10 -> {
                BattleType.FORCE_HEIGHT.setEnabled(!BattleType.FORCE_HEIGHT.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_HEIGHT.isEnabled()
                        ? "notification.force_height_enabled"
                        : "notification.force_height_disabled"));
            }
            case 11 -> {
                BattleType.FORCE_COORDS.setEnabled(!BattleType.FORCE_COORDS.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_COORDS.isEnabled()
                        ? "notification.force_coords_enabled"
                        : "notification.force_coords_disabled"));
            }
            case 18 -> {
                BattleType.FORCE_STRUCTURE.setEnabled(!BattleType.FORCE_STRUCTURE.isEnabled());
                ForceBattle.platform().broadcast(Caption.of(BattleType.FORCE_STRUCTURE.isEnabled()
                        ? "notification.force_structure_enabled"
                        : "notification.force_structure_disabled"));
            }
            case 6 -> {
                boolean newEnabled = !PluginConfig.get().getBoolean("settings.enable-chain-mode", false);
                PluginConfig.get().set("settings.enable-chain-mode", newEnabled);
                ForceBattle.platform().broadcast(Caption.of(newEnabled
                        ? "notification.chain_mode_enabled"
                        : "notification.chain_mode_disabled"));
            }
            case 7 -> {
                boolean newEnabled = !PluginConfig.get().getBoolean("settings.enable-backpacks", false);
                PluginConfig.get().set("settings.enable-backpacks", newEnabled);
                ForceBattle.platform().broadcast(Caption.of(newEnabled
                        ? "notification.backpack_enabled"
                        : "notification.backpack_disabled"));
            }
            case 8 -> {
                ForceBattle.getTimer().setRunning(false);
                ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
                BukkitPlayer.BUKKIT_PLAYERS.forEach(bukkitPlayer -> {
                    bukkitPlayer.teleport(bukkitPlayer.getWorld().getSpawnLocation());
                    bukkitPlayer.getInventory().clear();
                    bukkitPlayer.reset(true);
                });
            }
            case 15 -> {
                boolean newEnabled = !PluginConfig.get().getBoolean("settings.hide-points", false);
                PluginConfig.get().set("settings.hide-points", newEnabled);
                ForceBattle.platform().broadcast(Caption.of(newEnabled
                        ? "notification.hide_points_enabled"
                        : "notification.hide_points_disabled"));
            }
            case 16 -> {
                boolean newEnabled = !PluginConfig.get().getBoolean("settings.hide-objectives", false);
                PluginConfig.get().set("settings.hide-objectives", newEnabled);
                ForceBattle.platform().broadcast(Caption.of(newEnabled
                        ? "notification.hide_objectives_enabled"
                        : "notification.hide_objectives_disabled"));
            }
        }
        whoClicked.openInventory(getSettingsGUI());
    }

    public @NotNull Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(SettingsGUI.holderInstance, 36, Caption.getAsLegacy("gui.settings_title"));

        inventory.setItem(
                0, new ItemUtils.ItemBuilder()
                        .type(Material.ITEM_FRAME)
                        .name(Caption.getAsLegacy("gui.force_item_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_item_lore")))
                        .build()
        );
        inventory.setItem(
                1, new ItemUtils.ItemBuilder()
                        .type(Material.DIAMOND_SWORD)
                        .name(Caption.getAsLegacy("gui.force_mob_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_mob_lore")))
                        .build()
        );
        inventory.setItem(
                2, new ItemUtils.ItemBuilder()
                        .type(Material.GRASS_BLOCK)
                        .name(Caption.getAsLegacy("gui.force_biome_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_biome_lore")))
                        .build()
        );
        inventory.setItem(
                9, new ItemUtils.ItemBuilder()
                        .type(Material.BREWING_STAND)
                        .name(Caption.getAsLegacy("gui.force_advancement_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_advancement_lore")))
                        .build()
        );
        inventory.setItem(
                10, new ItemUtils.ItemBuilder()
                        .type(Material.SCAFFOLDING)
                        .name(Caption.getAsLegacy("gui.force_height_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_height_lore")))
                        .build()
        );
        inventory.setItem(
                11, new ItemUtils.ItemBuilder()
                        .type(Material.FILLED_MAP)
                        .name(Caption.getAsLegacy("gui.force_coords_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_coords_lore")))
                        .build()
        );
        inventory.setItem(
                18, new ItemUtils.ItemBuilder()
                        .type(Material.STRUCTURE_BLOCK)
                        .name(Caption.getAsLegacy("gui.force_structure_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.force_structure_lore")))
                        .build()
        );
        inventory.setItem(
                6, new ItemUtils.ItemBuilder()
                        .type(Material.CHAIN)
                        .name(Caption.getAsLegacy("gui.chain_mode_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.chain_mode_lore")))
                        .build()
        );
        inventory.setItem(
                7, new ItemUtils.ItemBuilder()
                        .type(Material.CHEST)
                        .name(ChatColor.BLUE + "Backpacks")
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.backpack_lore")))
                        .build()
        );
        inventory.setItem(
                8, new ItemUtils.ItemBuilder()
                        .type(Material.BARRIER)
                        .name(ChatColor.RED + "RESET")
                        .build()
        );
        inventory.setItem(
                15, new ItemUtils.ItemBuilder()
                        .type(Material.IRON_TRAPDOOR)
                        .name(Caption.getAsLegacy("gui.hide_points_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.hide_points_lore")))
                        .build()
        );
        inventory.setItem(
                16, new ItemUtils.ItemBuilder()
                        .type(Material.DARK_OAK_DOOR)
                        .name(Caption.getAsLegacy("gui.hide_objectives_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy("gui.hide_objectives_lore")))
                        .build()
        );
        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getSettingsGUI();
    }

}
