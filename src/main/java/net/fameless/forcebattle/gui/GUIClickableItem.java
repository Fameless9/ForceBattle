package net.fameless.forcebattle.gui;

import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.ItemStackCreator;
import net.fameless.forcebattle.util.Skull;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class GUIClickableItem extends GUIItem {

    public GUIClickableItem(final int slot) {
        super(slot);
    }

    public abstract void run(InventoryClickEvent event, BattlePlayer player);

    public static GUIClickableItem getCloseItem(int slot) {
        return new GUIClickableItem(slot) {
            @Override
            public void run(InventoryClickEvent e, BattlePlayer player) {
                player.getPlayer().closeInventory();
            }

            @Override
            public ItemStack getItem(BattlePlayer player) {
                return ItemStackCreator.createNamedItemStack(Material.BARRIER, "§cClose");
            }
        };
    }

    public static GUIClickableItem getGoBackItem(int slot, ForceBattleGUI gui) {
        return new GUIClickableItem(slot) {
            @Override
            public void run(InventoryClickEvent e, BattlePlayer player) {
                gui.open(player);
            }

            @Override
            public ItemStack getItem(BattlePlayer player) {
                return ItemStackCreator.getSkull(Skull.ARROW_LEFT, "§aGo Back", 1, "§7To " + gui.getTitle());
            }
        };
    }

    public static GUIClickableItem getGoForthItem(int slot, ForceBattleGUI gui) {
        return new GUIClickableItem(slot) {
            @Override
            public void run(InventoryClickEvent e, BattlePlayer player) {
                gui.open(player);
            }

            @Override
            public ItemStack getItem(BattlePlayer player) {
                return ItemStackCreator.getSkull(Skull.ARROW_RIGHT, "§aGo Forth", 1, "§7To " + gui.getTitle());
            }
        };
    }

    @Override
    public void onClick(InventoryClickEvent event, BattlePlayer player) {
        run(event, player);
    }

}
