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

    // TODO everyone has their own objective + a harder team objective => make each force setting toggleable for player/team/both objective => multiple setting option
    //  needed

    // TODO simplified option for biome and structure => groups multiple into a category which is the objective (multiple structures/biomes count)

    @Getter
    private enum SettingButton {
        FORCE_ITEM(0, Material.ITEM_FRAME, SettingsManager.Setting.FORCE_ITEM, "gui.force_item_name", "gui.force_item_lore",
                "notification.force_item_enabled", "notification.force_item_disabled"),
        FORCE_MOB(1, Material.DIAMOND_SWORD, SettingsManager.Setting.FORCE_MOB, "gui.force_mob_name", "gui.force_mob_lore",
                "notification.force_mob_enabled", "notification.force_mob_disabled"),
        FORCE_BIOME(2, Material.GRASS_BLOCK, SettingsManager.Setting.FORCE_BIOME, "gui.force_biome_name", "gui.force_biome_lore",
                "notification.force_biome_enabled", "notification.force_biome_disabled"),
        FORCE_ADVANCEMENT(9, Material.BREWING_STAND, SettingsManager.Setting.FORCE_ADVANCEMENT, "gui.force_advancement_name",
                "gui.force_advancement_lore", "notification.force_advancement_enabled", "notification.force_advancement_disabled"),
        FORCE_HEIGHT(10, Material.SCAFFOLDING, SettingsManager.Setting.FORCE_HEIGHT, "gui.force_height_name",
                "gui.force_height_lore", "notification.force_height_enabled", "notification.force_height_disabled"),
        FORCE_COORDS(11, Material.FILLED_MAP, SettingsManager.Setting.FORCE_COORDS, "gui.force_coords_name",
                "gui.force_coords_lore", "notification.force_coords_enabled", "notification.force_coords_disabled"),
        FORCE_STRUCTURE(18, Material.STRUCTURE_BLOCK, SettingsManager.Setting.FORCE_STRUCTURE, "gui.force_structure_name",
                "gui.force_structure_lore", "notification.force_structure_enabled", "notification.force_structure_disabled"),

        CHAIN_MODE(6, Material.CHAIN, SettingsManager.Setting.CHAIN_MODE, "gui.chain_mode_name",
                "gui.chain_mode_lore", "notification.chain_mode_enabled", "notification.chain_mode_disabled"),
        BACKPACK(7, Material.CHEST, SettingsManager.Setting.BACKPACK, ChatColor.BLUE + "Backpacks",
                "gui.backpack_lore", "notification.backpack_enabled", "notification.backpack_disabled"),
        HIDE_POINTS(15, Material.IRON_TRAPDOOR, SettingsManager.Setting.HIDE_POINTS, "gui.hide_points_name",
                "gui.hide_points_lore", "notification.hide_points_enabled", "notification.hide_points_disabled"),
        HIDE_OBJECTIVES(16, Material.DARK_OAK_DOOR, SettingsManager.Setting.HIDE_OBJECTIVES, "gui.hide_objectives_name",
                "gui.hide_objectives_lore", "notification.hide_objectives_enabled", "notification.hide_objectives_disabled"),
        NO_DUPLICATE_OBJECTIVES(17, Material.NAME_TAG, SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES, "gui.no_duplicate_objectives_name",
                "gui.no_duplicate_objectives_lore", "notification.no_duplicate_objectives_enabled", "notification.no_duplicate_objectives_disabled"),
        SIMPLIFIED_OBJECTIVES(26, Material.DIRT, SettingsManager.Setting.SIMPLIFIED_OBJECTIVES, "gui.simplified_objectives_name",
                "gui.simplified_objectives_lore", "notification.simplified_objectives_enabled", "notification.simplified_objectives_disabled"),

        RESET(8, Material.BARRIER, null, ChatColor.RED + "RESET", null, null, null);

        private final int slot;
        private final Material material;
        private final SettingsManager.Setting setting;
        private final String name;
        private final String lore;
        private final String enabledMsg;
        private final String disabledMsg;

        SettingButton(int slot, Material material, SettingsManager.Setting setting, String name, String lore, String enabledMsg, String disabledMsg) {
            this.slot = slot;
            this.material = material;
            this.setting = setting;
            this.name = name;
            this.lore = lore;
            this.enabledMsg = enabledMsg;
            this.disabledMsg = disabledMsg;
        }

        public static SettingButton fromSlot(int slot) {
            for (SettingButton button : values()) {
                if (button.slot == slot) return button;
            }
            return null;
        }

        public ItemStack toItem() {
            ItemUtils.ItemBuilder builder = new ItemUtils.ItemBuilder().type(material);

            if (name.startsWith("gui.")) builder.name(Caption.getAsLegacy(name));
            else builder.name(name);

            if (lore != null)
                builder.lore(Format.formatLineBreaks(Caption.getAsLegacy(lore)));

            return builder.build();
        }

        public void handleClick(BattlePlayer whoClicked) {
            if (this == RESET) {
                ForceBattle.getTimer().setRunning(false);
                ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
                BattlePlayer.BATTLE_PLAYERS.forEach(p -> {
                    if (!p.isOffline()) p.reset(true);
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
        if (button != null) button.handleClick(whoClicked);

        whoClicked.openInventory(getSettingsGUI());
    }

    public @NotNull Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(SettingsGUI.holderInstance, 36, Caption.getAsLegacy("gui.settings_title"));

        for (SettingButton button : SettingButton.values()) {
            inventory.setItem(button.getSlot(), button.toItem());
        }

        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getSettingsGUI();
    }
}
