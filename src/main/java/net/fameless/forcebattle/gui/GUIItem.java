package net.fameless.forcebattle.gui;

import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class GUIItem {
    public final int slot;
    public abstract ItemStack getItem(BattlePlayer player);

    public GUIItem(int slot) {
        this.slot = slot;
    }

    public void onClick(InventoryClickEvent event, BattlePlayer player) {
    }
}
