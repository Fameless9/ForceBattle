package net.fameless.forcebattle.gui;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.caption.Language;
import net.fameless.forcebattle.util.ItemUtils;
import net.fameless.forcebattle.util.Skull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LanguageGUI implements Listener, InventoryHolder {

    private enum LanguageButton {
        ENGLISH(0, Language.ENGLISH, Skull.FLAG_UK, "English",
                List.of(ChatColor.GRAY + "Click to set the language to english"), "Language is already set to english!"),

        GERMAN(1, Language.GERMAN, Skull.FLAG_GERMANY, "Deutsch",
                List.of(ChatColor.GRAY + "Klicke, um die Sprache auf deutsch zu stellen"), "Die Sprache ist bereits auf Deutsch eingestellt."),

        CHINESE_SIMPLIFIED(2, Language.CHINESE_SIMPLIFIED, Skull.FLAG_CHINA, "简体中文",
                List.of(ChatColor.GRAY + "点击将语言设置为简体中文"), "成功设置为简体中文!"),

        CHINESE_TRADITIONAL(3, Language.CHINESE_TRADITIONAL, Skull.FLAG_CHINA, "繁体中文",
                List.of(ChatColor.GRAY + "点击将语言设置为繁体中文"), "成功設寘爲緐體中文!");

        @Getter
        private final int slot;
        private final Language language;
        private final Skull skull;
        private final String name;
        private final List<String> lore;
        private final String alreadyMessage;

        LanguageButton(int slot, Language language, Skull skull, String name, List<String> lore, String alreadyMessage) {
            this.slot = slot;
            this.language = language;
            this.skull = skull;
            this.name = name;
            this.lore = lore;
            this.alreadyMessage = alreadyMessage;
        }

        public ItemStack buildItem() {
            return ItemUtils.buildItem(skull.asItemStack(), ChatColor.GOLD + name, lore, null, null);
        }

        public static LanguageButton fromSlot(int slot) {
            for (LanguageButton button : values()) {
                if (button.slot == slot) return button;
            }
            return null;
        }

        public void handleClick(Player player) {
            if (Caption.getCurrentLanguage() == language) {
                player.sendMessage(ChatColor.RED + alreadyMessage);
                return;
            }
            Caption.setCurrentLanguage(language);
            ForceBattle.broadcast(MiniMessage.miniMessage().deserialize(
                    Caption.getCurrentLanguage().getUpdateMessage(),
                    Caption.prefixTagResolver()
            ));
        }
    }

    @NotNull public Inventory getLanguageGUI() {
        Inventory gui = Bukkit.createInventory(this, 9, Caption.getAsLegacy("gui.language_title"));
        for (LanguageButton button : LanguageButton.values()) {
            gui.setItem(button.getSlot(), button.buildItem());
        }
        return gui;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LanguageGUI)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        LanguageButton button = LanguageButton.fromSlot(event.getSlot());
        if (button != null) {
            button.handleClick(player);
        }

        player.openInventory(getLanguageGUI());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getLanguageGUI();
    }

}
