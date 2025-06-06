package net.fameless.spigot.gui;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;
import net.fameless.spigot.util.ItemUtils;
import net.fameless.spigot.util.Skull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitLanguageGUI implements Listener, InventoryHolder, net.fameless.core.gui.LanguageGUI<Inventory> {

    @Override
    public @NotNull Inventory getLanguageGUI() {
        Inventory gui = Bukkit.createInventory(this, 9, Caption.getAsLegacy("gui.language_title"));
        gui.setItem(
                0,
                ItemUtils.buildItem(
                        Skull.FLAG_UK.asItemStack(),
                        ChatColor.GOLD + "English",
                        List.of(ChatColor.GRAY + "Click to set the language to english"),
                        null,
                        null
                )
        );
        gui.setItem(
                1,
                ItemUtils.buildItem(
                        Skull.FLAG_GERMANY.asItemStack(),
                        ChatColor.GOLD + "Deutsch",
                        List.of(ChatColor.GRAY + "Klicke, um die Sprache auf deutsch zu stellen"),
                        null,
                        null
                )
        );
        gui.setItem(
                2,
                ItemUtils.buildItem(
                        Skull.FLAG_CHINA.asItemStack(),
                        ChatColor.GOLD + "简体中文",
                        List.of(ChatColor.GRAY + "点击将语言设置为简体中文"),
                        null,
                        null
                )
        );
        gui.setItem(
                3,
                ItemUtils.buildItem(
                        Skull.FLAG_CHINA.asItemStack(),
                        ChatColor.GOLD + "繁体中文",
                        List.of(ChatColor.GRAY + "点击将语言设置为繁体中文"),
                        null,
                        null
                )
        );

        return gui;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BukkitLanguageGUI)) {
            return;
        }
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 0 -> {
                if (Caption.getCurrentLanguage() == Language.ENGLISH) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Language is already set to english!");
                    return;
                }
                Caption.setCurrentLanguage(Language.ENGLISH);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
            case 1 -> {
                if (Caption.getCurrentLanguage() == Language.GERMAN) {
                    event.getWhoClicked()
                            .sendMessage(ChatColor.RED + "Die Sprache ist bereits auf Deutsch eingestellt.");
                    return;
                }
                Caption.setCurrentLanguage(Language.GERMAN);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
            case 2 -> {
                if (Caption.getCurrentLanguage() == Language.CHINESE_SIMPLIFIED) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "成功设置为简体中文!");
                    return;
                }
                Caption.setCurrentLanguage(Language.CHINESE_SIMPLIFIED);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
            case 3 -> {
                if (Caption.getCurrentLanguage() == Language.CHINESE_TRADITIONAL) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "成功設寘爲緐體中文!");
                    return;
                }
                Caption.setCurrentLanguage(Language.CHINESE_TRADITIONAL);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
        }
        event.getWhoClicked().openInventory(getLanguageGUI());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getLanguageGUI();
    }

}
