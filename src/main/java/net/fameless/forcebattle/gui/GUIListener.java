package net.fameless.forcebattle.gui;

import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ForceBattleGUI gui)) return;
        event.setCancelled(!gui.allowHotkeying());

        BattlePlayer player = BattlePlayer.adapt((Player) event.getWhoClicked());

        if (event.getRawSlot() < 0) return;
        if (event.getRawSlot() >= gui.getSize()) return;

        GUIItem item = gui.get(event.getRawSlot());
        if (item == null) return;
        item.onClick(event, player);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ForceBattleGUI) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof ForceBattleGUI gui) {
            gui.onClose(event, ForceBattleGUI.CloseReason.PLAYER_EXITED);
            ForceBattleGUI.GUI_MAP.remove(event.getPlayer().getUniqueId());
        }
    }
}
