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
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigSettingsGUI extends ForceBattleGUI {

    public ConfigSettingsGUI() {
        super(Caption.getAsLegacy("gui.config_settings_title"), 27);
    }

    @Getter
    public enum ConfigSettingButton {
        MUSIC_DISCS(10, Material.MUSIC_DISC_CAT, SettingsManager.Setting.EXCLUDE_MUSIC_DISCS,
                "gui.exclude_music_discs_name", "gui.exclude_music_discs_lore", "notification.exclude_music_discs_changed"),
        BANNER_PATTERNS(11, Material.FLOWER_BANNER_PATTERN, SettingsManager.Setting.EXCLUDE_BANNER_PATTERNS,
                "gui.exclude_banner_patterns_name", "gui.exclude_banner_patterns_lore", "notification.exclude_banner_patterns_changed"),
        ARMOR_TEMPLATES(12, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SettingsManager.Setting.EXCLUDE_ARMOR_TEMPLATES,
                "gui.exclude_armor_templates_name", "gui.exclude_armor_templates_lore", "notification.exclude_armor_templates_changed"),
        POTTERY_SHERDS(13, Material.ANGLER_POTTERY_SHERD, SettingsManager.Setting.EXCLUDE_POTTERY_SHERDS,
                "gui.exclude_pottery_sherds_name", "gui.exclude_pottery_sherds_lore", "notification.exclude_pottery_sherds_changed"),
        ORES(14, Material.DIAMOND_ORE, SettingsManager.Setting.EXCLUDE_ORES,
                "gui.exclude_ores_name", "gui.exclude_ores_lore", "notification.exclude_ores_changed"),
        END(15, Material.END_STONE, SettingsManager.Setting.EXCLUDE_END,
                "gui.exclude_end_name", "gui.exclude_end_lore", "notification.exclude_end_changed"),
        TRIAL_ITEMS(16, Material.TRIAL_SPAWNER, SettingsManager.Setting.EXCLUDE_TRIAL_ITEMS,
                "gui.exclude_trial_items_name", "gui.exclude_trial_items_lore", "notification.exclude_trial_items_changed");

        private final int slot;
        private final Material material;
        private final SettingsManager.Setting setting;
        private final String name;
        private final String lore;
        private final String changedMsg;

        ConfigSettingButton(int slot, Material material, SettingsManager.Setting setting, String name, String lore, String changedMsg) {
            this.slot = slot;
            this.material = material;
            this.setting = setting;
            this.name = name;
            this.lore = lore;
            this.changedMsg = changedMsg;
        }

        public ItemStack createItem() {
            if (lore != null && setting != null) {
                String state = SettingsManager.getState(setting).getDisplayName(SettingsManager.isMultiState(setting));

                String rawLore = Caption.getAsLegacy(lore, TagResolver.resolver("state", Tag.inserting(Component.text(state))));
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

        public void handleClick(BattlePlayer whoClicked) {
            boolean newState = !SettingsManager.isEnabled(setting);
            SettingsManager.setState(setting, newState ? SettingsManager.SettingState.PLAYER : SettingsManager.SettingState.OFF);

            if (changedMsg != null) {
                ForceBattle.broadcast(Caption.of(changedMsg, TagResolver.resolver(
                        "state",
                        Tag.inserting(Component.text(SettingsManager.getState(setting).getDisplayName(false)))
                )));
            }

            new ConfigSettingsGUI().open(whoClicked);
        }
    }

    @Override
    public void setItems(BattlePlayer player) {
        fill(ItemStackCreator.fillerItem());
        set(GUIClickableItem.getGoBackItem(0, new SettingsGUI()));

        for (ConfigSettingButton button : ConfigSettingButton.values()) {
            set(new GUIClickableItem(button.getSlot()) {
                @Override
                public void run(InventoryClickEvent event, BattlePlayer player) {
                    button.handleClick(player);
                }

                @Override
                public ItemStack getItem(BattlePlayer player) {
                    return button.createItem();
                }
            });
        }
    }

    @Override
    public boolean allowItemMoving() {
        return false;
    }
}
