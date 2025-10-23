package net.fameless.forcebattle.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ForceBattleGUI gui)) return;
        event.setCancelled(!gui.allowItemMoving());

        if (event.getRawSlot() < 0) return;
        if (event.getRawSlot() >= gui.getSize()) return;

        GUIItem clicked = gui.get(event.getRawSlot());
        if (clicked == null) return;

        if (clicked instanceof GUIClickableItem clickable) {
            event.setCancelled(true);
            clickable.onClick(event, gui.getPlayer());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ForceBattleGUI gui) {
            event.setCancelled(!gui.allowItemMoving());
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
