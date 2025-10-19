package net.fameless.forcebattle.gui.impl;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.ItemStackCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsGUI extends ForceBattleGUI {

    public SettingsGUI() {
        super(Caption.getAsLegacy("gui.settings_title"), 36);
    }

    @Getter
    public enum SettingButton {
        FORCE_ITEM(0, Material.ITEM_FRAME, SettingsManager.Setting.FORCE_ITEM, true,
                "gui.force_item_name", "gui.force_item_lore", "notification.force_item_changed"),
        FORCE_MOB(1, Material.DIAMOND_SWORD, SettingsManager.Setting.FORCE_MOB, true,
                "gui.force_mob_name", "gui.force_mob_lore", "notification.force_mob_changed"),
        FORCE_BIOME(2, Material.GRASS_BLOCK, SettingsManager.Setting.FORCE_BIOME, true,
                "gui.force_biome_name", "gui.force_biome_lore", "notification.force_biome_changed"),
        FORCE_ADVANCEMENT(3, Material.BREWING_STAND, SettingsManager.Setting.FORCE_ADVANCEMENT, true,
                "gui.force_advancement_name", "gui.force_advancement_lore", "notification.force_advancement_changed"),
        FORCE_HEIGHT(4, Material.SCAFFOLDING, SettingsManager.Setting.FORCE_HEIGHT, true,
                "gui.force_height_name", "gui.force_height_lore", "notification.force_height_changed"),
        FORCE_COORDS(5, Material.FILLED_MAP, SettingsManager.Setting.FORCE_COORDS, true,
                "gui.force_coords_name", "gui.force_coords_lore", "notification.force_coords_changed"),
        FORCE_STRUCTURE(6, Material.STRUCTURE_BLOCK, SettingsManager.Setting.FORCE_STRUCTURE, true,
                "gui.force_structure_name", "gui.force_structure_lore", "notification.force_structure_changed"),

        CHAIN_MODE(27, Material.CHAIN, SettingsManager.Setting.CHAIN_MODE, false,
                "gui.chain_mode_name", "gui.chain_mode_lore", "notification.chain_mode_changed"),
        BACKPACK(28, Material.CHEST, SettingsManager.Setting.BACKPACK, false,
                ChatColor.BLUE + "Backpacks", "gui.backpack_lore", "notification.backpack_changed"),
        HIDE_POINTS(29, Material.IRON_TRAPDOOR, SettingsManager.Setting.HIDE_POINTS, false,
                "gui.hide_points_name", "gui.hide_points_lore", "notification.hide_points_changed"),
        HIDE_OBJECTIVES(30, Material.DARK_OAK_DOOR, SettingsManager.Setting.HIDE_OBJECTIVES, false,
                "gui.hide_objectives_name", "gui.hide_objectives_lore", "notification.hide_objectives_changed"),
        NO_DUPLICATE_OBJECTIVES(31, Material.NAME_TAG, SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES, false,
                "gui.no_duplicate_objectives_name", "gui.no_duplicate_objectives_lore", "notification.no_duplicate_objectives_changed"),
        SIMPLIFIED_OBJECTIVES(32, Material.DIRT, SettingsManager.Setting.SIMPLIFIED_OBJECTIVES, false,
                "gui.simplified_objectives_name", "gui.simplified_objectives_lore", "notification.simplified_objectives_changed"),
        EXTRA_TEAM_OBJECTIVE(33, Material.ALLAY_SPAWN_EGG, SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE, false,
                "gui.extra_team_objective_name", "gui.extra_team_objective_lore", "notification.extra_team_objective_changed"),
        SHOW_SCOREBOARD(34, Material.ARMOR_STAND, SettingsManager.Setting.SHOW_SCOREBOARD, false,
                "gui.show_scoreboard_name", "gui.show_scoreboard_lore", "notification.show_scoreboard_changed"),

        RESET(35, Material.BARRIER, null, false, ChatColor.RED + "RESET", null, null);

        private final int slot;
        private final Material material;
        private final SettingsManager.Setting setting;
        private final boolean multiState;
        private final String name;
        private final String lore;
        private final String changedMsg;

        SettingButton(int slot, Material material, SettingsManager.Setting setting, boolean multiState,
                      String name, String lore, String changedMsg) {
            this.slot = slot;
            this.material = material;
            this.setting = setting;
            this.multiState = multiState;
            this.name = name;
            this.lore = lore;
            this.changedMsg = changedMsg;
        }

        public ItemStack createItem() {
            if (lore != null && setting != null) {
                String state = SettingsManager.getState(setting).getDisplayName(SettingsManager.isMultiState(setting));
                Component componentLore = Caption.of(lore, TagResolver.resolver("state", Tag.inserting(Component.text(state))));

                String rawLore = LegacyComponentSerializer.legacySection().serialize(componentLore);
                List<String> loreLines = new ArrayList<>(List.of(rawLore.split("\n")));

                return ItemStackCreator.getStack(
                        name.startsWith("gui.") ? Caption.getAsLegacy(name) : name,
                        material,
                        1,
                        loreLines
                );
            } else {
                return ItemStackCreator.createNamedItemStack(material, name.startsWith("gui.") ? Caption.getAsLegacy(name) : name);
            }
        }

        public void handleClick(BattlePlayer player) {
            if (this == RESET) {
                ForceBattle.getTimer().setRunning(false);
                ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
                BattlePlayer.BATTLE_PLAYERS.forEach(p -> { if (!p.isOffline()) p.reset(true); });
                return;
            }

            if (setting == null) return;

            boolean multi = SettingsManager.isMultiState(setting);
            SettingsManager.SettingState next = SettingsManager.getState(setting).next(multi);
            SettingsManager.setState(setting, next);

            ForceBattle.broadcast(Caption.of(changedMsg, TagResolver.resolver(
                    "state", Tag.inserting(Component.text(SettingsManager.getState(setting)
                            .getDisplayName(SettingsManager.isMultiState(setting)))))));

            new SettingsGUI().open(player);
        }
    }

    @Override
    public void setItems(BattlePlayer player) {
        fill(ItemStackCreator.createNamedItemStack(Material.GRAY_STAINED_GLASS_PANE, " "));
        set(GUIClickableItem.getGoForthItem(8, new ConfigSettingsGUI()));

        for (SettingButton button : SettingButton.values()) {
            set(new GUIClickableItem(button.getSlot()) {
                @Override
                public void run(final InventoryClickEvent event, final BattlePlayer player) {
                    button.handleClick(player);
                }

                @Override
                public ItemStack getItem(BattlePlayer p) {
                    return button.createItem();
                }
            });
        }
    }

    @Override
    public boolean allowHotkeying() {
        return false;
    }
}
