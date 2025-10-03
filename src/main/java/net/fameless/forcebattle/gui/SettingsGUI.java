package net.fameless.forcebattle.gui;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.Format;
import net.fameless.forcebattle.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingsGUI implements Listener, InventoryHolder {

    private static SettingsGUI holderInstance;

    public SettingsGUI() {
        holderInstance = this;
    }

    @Getter
    private enum SettingButton {
        FORCE_ITEM(0, SettingsManager.Setting.FORCE_ITEM, "notification.force_item_enabled", "notification.force_item_disabled"),
        FORCE_MOB(1, SettingsManager.Setting.FORCE_MOB, "notification.force_mob_enabled", "notification.force_mob_disabled"),
        FORCE_BIOME(2, SettingsManager.Setting.FORCE_BIOME, "notification.force_biome_enabled", "notification.force_biome_disabled"),
        FORCE_ADVANCEMENT(9, SettingsManager.Setting.FORCE_ADVANCEMENT, "notification.force_advancement_enabled", "notification.force_advancement_disabled"),
        FORCE_HEIGHT(10, SettingsManager.Setting.FORCE_HEIGHT, "notification.force_height_enabled", "notification.force_height_disabled"),
        FORCE_COORDS(11, SettingsManager.Setting.FORCE_COORDS, "notification.force_coords_enabled", "notification.force_coords_disabled"),
        FORCE_STRUCTURE(18, SettingsManager.Setting.FORCE_STRUCTURE, "notification.force_structure_enabled", "notification.force_structure_disabled"),

        CHAIN_MODE(6, SettingsManager.Setting.CHAIN_MODE, "notification.chain_mode_enabled", "notification.chain_mode_disabled"),
        BACKPACK(7, SettingsManager.Setting.BACKPACK, "notification.backpack_enabled", "notification.backpack_disabled"),
        HIDE_POINTS(15, SettingsManager.Setting.HIDE_POINTS, "notification.hide_points_enabled", "notification.hide_points_disabled"),
        HIDE_OBJECTIVES(16, SettingsManager.Setting.HIDE_OBJECTIVES, "notification.hide_objectives_enabled", "notification.hide_objectives_disabled"),
        NO_DUPLICATE_OBJECTIVES(17, SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES, "notification.no_duplicate_objectives_enabled", "notification.no_duplicate_objectives_disabled"),

        RESET(8, null, null, null);

        private final int slot;
        private final SettingsManager.Setting setting;
        private final String enabledMsg;
        private final String disabledMsg;

        SettingButton(int slot, SettingsManager.Setting setting, String enabledMsg, String disabledMsg) {
            this.slot = slot;
            this.setting = setting;
            this.enabledMsg = enabledMsg;
            this.disabledMsg = disabledMsg;
        }

        public static SettingButton fromSlot(int slot) {
            for (SettingButton button : values()) {
                if (button.slot == slot) return button;
            }
            return null;
        }

        public void handleClick(BattlePlayer whoClicked) {
            if (this == RESET) {
                ForceBattle.getTimer().setRunning(false);
                ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
                BattlePlayer.BATTLE_PLAYERS.forEach(p -> {
                    if (!p.isOffline()) {
                        p.reset(true);
                    }
                });
                return;
            }

            if (setting == null) return;

            boolean newStatus = !SettingsManager.isEnabled(setting);

            if (!canToggle(whoClicked, setting, newStatus)) return;

            SettingsManager.setEnabled(setting, newStatus);

            ForceBattle.broadcast(Caption.of(newStatus ? enabledMsg : disabledMsg));
        }

        private boolean canToggle(BattlePlayer player, SettingsManager.Setting setting, boolean newStatus) {
            if (setting.name().startsWith("FORCE_")) {
                if (SettingsManager.isEnabled(setting) && !newStatus && SettingsManager.getActiveChallenges().size() == 1) {
                    player.sendMessage(Caption.of("error.no_challenge_selected"));
                    return false;
                }
            }
            return true;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsGUI)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        event.setCancelled(true);
        BattlePlayer whoClicked = BattlePlayer.adapt(player);

        SettingButton button = SettingButton.fromSlot(event.getSlot());
        if (button != null) {
            button.handleClick(whoClicked);
        }

        whoClicked.openInventory(getSettingsGUI());
    }

    public @NotNull Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(SettingsGUI.holderInstance, 36, Caption.getAsLegacy("gui.settings_title"));

        for (SettingItem item : SettingItem.values()) {
            inventory.setItem(item.getSlot(), item.toItem());
        }

        return inventory;
    }

    @Getter
    private enum SettingItem {
        FORCE_ITEM(0, Material.ITEM_FRAME, "gui.force_item_name", "gui.force_item_lore"),
        FORCE_MOB(1, Material.DIAMOND_SWORD, "gui.force_mob_name", "gui.force_mob_lore"),
        FORCE_BIOME(2, Material.GRASS_BLOCK, "gui.force_biome_name", "gui.force_biome_lore"),
        FORCE_ADVANCEMENT(9, Material.BREWING_STAND, "gui.force_advancement_name", "gui.force_advancement_lore"),
        FORCE_HEIGHT(10, Material.SCAFFOLDING, "gui.force_height_name", "gui.force_height_lore"),
        FORCE_COORDS(11, Material.FILLED_MAP, "gui.force_coords_name", "gui.force_coords_lore"),
        FORCE_STRUCTURE(18, Material.STRUCTURE_BLOCK, "gui.force_structure_name", "gui.force_structure_lore"),
        CHAIN_MODE(6, Material.CHAIN, "gui.chain_mode_name", "gui.chain_mode_lore"),
        BACKPACK(7, Material.CHEST, ChatColor.BLUE + "Backpacks", "gui.backpack_lore"),
        RESET(8, Material.BARRIER, ChatColor.RED + "RESET", null),
        HIDE_POINTS(15, Material.IRON_TRAPDOOR, "gui.hide_points_name", "gui.hide_points_lore"),
        HIDE_OBJECTIVES(16, Material.DARK_OAK_DOOR, "gui.hide_objectives_name", "gui.hide_objectives_lore"),
        NO_DUPLICATE_OBJECTIVES(17, Material.NAME_TAG, "gui.no_duplicate_objectives_name", "gui.no_duplicate_objectives_lore");

        private final int slot;
        private final Material material;
        private final String name;
        private final String lore;

        SettingItem(int slot, Material material, String name, String lore) {
            this.slot = slot;
            this.material = material;
            this.name = name;
            this.lore = lore;
        }

        public ItemStack toItem() {
            ItemUtils.ItemBuilder builder = new ItemUtils.ItemBuilder()
                    .type(material);

            if (name.startsWith("gui.")) {
                builder.name(Caption.getAsLegacy(name));
            } else {
                builder.name(name);
            }

            if (lore != null) {
                builder.lore(Format.formatLineBreaks(Caption.getAsLegacy(lore)));
            }

            return builder.build();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getSettingsGUI();
    }

}
