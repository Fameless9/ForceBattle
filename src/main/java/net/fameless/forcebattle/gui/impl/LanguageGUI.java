package net.fameless.forcebattle.gui.impl;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.caption.Language;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.ItemStackCreator;
import net.fameless.forcebattle.util.Skull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LanguageGUI extends ForceBattleGUI {

    public LanguageGUI() {
        super(Caption.getAsLegacy("gui.language_title"), 9);
    }

    @Getter
    public enum LanguageButton {
        ENGLISH(0, Language.ENGLISH, Skull.FLAG_UK, "English",
                List.of("Click to set the language to English"), "Language is already set to English!"),
        GERMAN(1, Language.GERMAN, Skull.FLAG_GERMANY, "Deutsch",
                List.of("Klicke, um die Sprache auf Deutsch zu stellen"), "Die Sprache ist bereits auf Deutsch eingestellt."),
        CHINESE_SIMPLIFIED(2, Language.CHINESE_SIMPLIFIED, Skull.FLAG_CHINA, "简体中文",
                List.of("点击将语言设置为简体中文"), "成功设置为简体中文!"),
        CHINESE_TRADITIONAL(3, Language.CHINESE_TRADITIONAL, Skull.FLAG_CHINA, "繁体中文",
                List.of("点击将语言设置为繁体中文"), "成功設寘爲緐體中文!");

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

        public ItemStack createItem() {
            return ItemStackCreator.getSkull(skull, ChatColor.GOLD + name, 1,
                    lore.stream().map(line -> ChatColor.GRAY + line).toList());
        }

        public void handleClick(BattlePlayer player) {
            if (Caption.getCurrentLanguage() == language) {
                player.getPlayer().sendMessage(ChatColor.RED + alreadyMessage);
                return;
            }

            Caption.setCurrentLanguage(language);
            ForceBattle.broadcast(MiniMessage.miniMessage().deserialize( Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));

            new LanguageGUI().open(player);
        }
    }

    @Override
    public void setItems(BattlePlayer player) {
        for (LanguageButton button : LanguageButton.values()) {
            set(new GUIClickableItem(button.getSlot()) {
                @Override
                public void run(InventoryClickEvent event, BattlePlayer p) {
                    button.handleClick(p);
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
