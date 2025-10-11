package net.fameless.forcebattle.gui;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.Format;
import net.fameless.forcebattle.util.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SettingsGUI implements Listener, InventoryHolder {

    private static SettingsGUI holderInstance;

    public SettingsGUI() {
        holderInstance = this;
    }

    private static final EnumSet<SettingsManager.Setting> MULTI_STATE_SETTINGS = EnumSet.of(
            SettingsManager.Setting.FORCE_ITEM,
            SettingsManager.Setting.FORCE_MOB,
            SettingsManager.Setting.FORCE_BIOME,
            SettingsManager.Setting.FORCE_ADVANCEMENT,
            SettingsManager.Setting.FORCE_HEIGHT,
            SettingsManager.Setting.FORCE_COORDS,
            SettingsManager.Setting.FORCE_STRUCTURE
    );

    @Getter
    private enum SettingButton {
        FORCE_ITEM(0, Material.ITEM_FRAME, SettingsManager.Setting.FORCE_ITEM, "gui.force_item_name",
                "gui.force_item_lore", "notification.force_item_changed"),
        FORCE_MOB(1, Material.DIAMOND_SWORD, SettingsManager.Setting.FORCE_MOB, "gui.force_mob_name",
                "gui.force_mob_lore", "notification.force_mob_changed"),
        FORCE_BIOME(2, Material.GRASS_BLOCK, SettingsManager.Setting.FORCE_BIOME, "gui.force_biome_name",
                "gui.force_biome_lore", "notification.force_biome_changed"),
        FORCE_ADVANCEMENT(3, Material.BREWING_STAND, SettingsManager.Setting.FORCE_ADVANCEMENT, "gui.force_advancement_name",
                "gui.force_advancement_lore", "notification.force_advancement_changed"),
        FORCE_HEIGHT(4, Material.SCAFFOLDING, SettingsManager.Setting.FORCE_HEIGHT, "gui.force_height_name",
                "gui.force_height_lore", "notification.force_height_changed"),
        FORCE_COORDS(5, Material.FILLED_MAP, SettingsManager.Setting.FORCE_COORDS, "gui.force_coords_name",
                "gui.force_coords_lore", "notification.force_coords_changed"),
        FORCE_STRUCTURE(6, Material.STRUCTURE_BLOCK, SettingsManager.Setting.FORCE_STRUCTURE, "gui.force_structure_name",
                "gui.force_structure_lore", "notification.force_structure_changed"),

        CHAIN_MODE(27, Material.CHAIN, SettingsManager.Setting.CHAIN_MODE, "gui.chain_mode_name",
                "gui.chain_mode_lore", "notification.chain_mode_changed"),
        BACKPACK(28, Material.CHEST, SettingsManager.Setting.BACKPACK, ChatColor.BLUE + "Backpacks",
                "gui.backpack_lore", "notification.backpack_changed"),
        HIDE_POINTS(29, Material.IRON_TRAPDOOR, SettingsManager.Setting.HIDE_POINTS, "gui.hide_points_name",
                "gui.hide_points_lore", "notification.hide_points_changed"),
        HIDE_OBJECTIVES(30, Material.DARK_OAK_DOOR, SettingsManager.Setting.HIDE_OBJECTIVES, "gui.hide_objectives_name",
                "gui.hide_objectives_lore", "notification.hide_objectives_changed"),
        NO_DUPLICATE_OBJECTIVES(31, Material.NAME_TAG, SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES, "gui.no_duplicate_objectives_name",
                "gui.no_duplicate_objectives_lore", "notification.no_duplicate_objectives_changed"),
        SIMPLIFIED_OBJECTIVES(32, Material.DIRT, SettingsManager.Setting.SIMPLIFIED_OBJECTIVES, "gui.simplified_objectives_name",
                "gui.simplified_objectives_lore", "notification.simplified_objectives_changed"),
        EXTRA_TEAM_OBJECTIVE(33, Material.ALLAY_SPAWN_EGG, SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE, "gui.extra_team_objective_name",
                "gui.extra_team_objective_lore", "notification.extra_team_objective_changed"),

        RESET(8, Material.BARRIER, null, ChatColor.RED + "RESET", null, null);

        private final int slot;
        private final Material material;
        private final SettingsManager.Setting setting;
        private final String name;
        private final String lore;
        private final String changedMsg;

        SettingButton(int slot, Material material, SettingsManager.Setting setting, String name, String lore, String changedMsg) {
            this.slot = slot;
            this.material = material;
            this.setting = setting;
            this.name = name;
            this.lore = lore;
            this.changedMsg = changedMsg;
        }


        public static SettingButton fromSlot(int slot) {
            for (SettingButton button : values()) {
                if (button.slot == slot) return button;
            }
            return null;
        }

        public ItemStack toItem() {
            ItemUtils.ItemBuilder builder = new ItemUtils.ItemBuilder().type(material);

            if (name.startsWith("gui.")) {
                builder.name(Caption.getAsLegacy(name));
            } else {
                builder.name(name);
            }

            List<String> loreLines = lore != null ? new ArrayList<>(Format.formatLineBreaks(
                    LegacyComponentSerializer.legacySection().serialize(
                            Caption.of(lore, TagResolver.resolver("state", Tag.inserting(Component.text(SettingsManager.getState(setting).getDisplayName()))))
                    )
            )) : new ArrayList<>();

            if (!loreLines.isEmpty()) builder.lore(loreLines);

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

            SettingsManager.SettingState current = SettingsManager.getState(setting);
            SettingsManager.SettingState next;

            if (MULTI_STATE_SETTINGS.contains(setting)) {
                next = current.next();
            } else {
                next = (current == SettingsManager.SettingState.OFF) ? SettingsManager.SettingState.PLAYER : SettingsManager.SettingState.OFF;
            }

            SettingsManager.setState(setting, next);

            ForceBattle.broadcast(Caption.of(changedMsg, TagResolver.resolver("state",
                    Tag.inserting(Component.text(SettingsManager.getState(setting).getDisplayName())))));
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
