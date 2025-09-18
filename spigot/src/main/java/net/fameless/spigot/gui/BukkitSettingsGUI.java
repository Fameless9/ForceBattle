package net.fameless.spigot.gui;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.gui.SettingsGUI;
import net.fameless.core.util.Format;
import net.fameless.spigot.BukkitPlatform;
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

public class BukkitSettingsGUI implements SettingsGUI<Inventory>, Listener, InventoryHolder {

    private static BukkitSettingsGUI holderInstance;

    public BukkitSettingsGUI() {
        holderInstance = this;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BukkitSettingsGUI)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        event.setCancelled(true);
        BukkitPlayer whoClicked = BukkitPlayer.adapt(player);
        switch (event.getSlot()) {
            case 0 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_ITEM, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM))) {
                    break;
                }
                SettingsManager.setForceItemEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)
                        ? "notification.force_item_enabled"
                        : "notification.force_item_disabled"));
            }
            case 1 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_MOB, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB))) {
                    break;
                }
                SettingsManager.setForceMobEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)
                        ? "notification.force_mob_enabled"
                        : "notification.force_mob_disabled"));
            }
            case 2 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_BIOME, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME))) {
                    break;
                }
                SettingsManager.setForceBiomeEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)
                        ? "notification.force_biome_enabled"
                        : "notification.force_biome_disabled"));
            }
            case 9 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_ADVANCEMENT, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT))) {
                    break;
                }
                SettingsManager.setForceAdvancementEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)
                        ? "notification.force_advancement_enabled"
                        : "notification.force_advancement_disabled"));
            }
            case 10 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_HEIGHT, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT))) {
                    break;
                }
                SettingsManager.setForceHeightEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)
                        ? "notification.force_height_enabled"
                        : "notification.force_height_disabled"));
            }
            case 11 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_COORDS, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS))) {
                    break;
                }
                SettingsManager.setForceCoordsEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS)
                        ? "notification.force_coords_enabled"
                        : "notification.force_coords_disabled"));
            }
            case 18 -> {
                if (!canToggle(whoClicked, SettingsManager.Setting.FORCE_STRUCTURE, !SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE))) {
                    break;
                }
                SettingsManager.setForceStructureEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)
                        ? "notification.force_structure_enabled"
                        : "notification.force_structure_disabled"));
            }
            case 6 -> {
                SettingsManager.setChainModeEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE)
                        ? "notification.chain_mode_enabled"
                        : "notification.chain_mode_disabled"));
            }
            case 7 -> {
                SettingsManager.setBackpackEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.BACKPACK));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.BACKPACK)
                        ? "notification.backpack_enabled"
                        : "notification.backpack_disabled"));
            }
            case 8 -> {
                ForceBattle.getTimer().setRunning(false);
                ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
                BukkitPlayer.BUKKIT_PLAYERS.forEach(bukkitPlayer -> {
                    bukkitPlayer.reset(true);
                    bukkitPlayer.teleport(bukkitPlayer.getWorld().getSpawnLocation());
                    bukkitPlayer.getInventory().clear();
                    bukkitPlayer.getInventory().addItem(ItemUtils.SpecialItems.getSkipItem(BukkitPlatform.get().getConfig().getInt("skips", 3)));
                    bukkitPlayer.getInventory().addItem(ItemUtils.SpecialItems.getSwapItem(BukkitPlatform.get().getConfig().getInt("swaps", 1)));
                });
            }
            case 15 -> {
                SettingsManager.setHidePointsEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)
                        ? "notification.hide_points_enabled"
                        : "notification.hide_points_disabled"));
            }
            case 16 -> {
                SettingsManager.setHideObjectivesEnabled(!SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES));
                ForceBattle.platform().broadcast(Caption.of(SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES)
                        ? "notification.hide_objectives_enabled"
                        : "notification.hide_objectives_disabled"));
            }
        }
        whoClicked.openInventory(getSettingsGUI());
    }

    private boolean canToggle(BukkitPlayer player, SettingsManager.Setting setting, boolean newStatus) {
        if (SettingsManager.isEnabled(setting) && !newStatus && SettingsManager.getActiveChallenges().size() == 1) {
            player.sendMessage(Caption.of("error.no_challenge_selected"));
            return false;
        }
        return true;
    }

    @Override
    public @NotNull Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(BukkitSettingsGUI.holderInstance, 36, Caption.getAsLegacy("gui.settings_title"));

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
