package net.fameless.forcebattle.gui;

import lombok.Getter;
import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class ForceBattleGUI implements Listener, InventoryHolder {
    public static final Map<UUID, ForceBattleGUI> GUI_MAP = new ConcurrentHashMap<>();

    protected String title;
    protected int size;
    protected final List<GUIItem> items;
    private Inventory inventory;

    private final Set<UUID> currentViewers = ConcurrentHashMap.newKeySet();

    private boolean hasFinishedLoading = false;

    public ForceBattleGUI(String title, int size) {
        this.title = title;
        this.size = size;
        this.items = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, size, title);
        }
        return inventory;
    }

    public void set(GUIItem item) {
        synchronized (items) {
            items.removeIf(i -> i.slot == item.slot);
            items.add(item);
        }
    }

    public GUIItem get(int slot) {
        synchronized (items) {
            return items.stream().filter(i -> i.slot == slot).findFirst().orElse(null);
        }
    }

    public void clear() {
        synchronized (items) {
            items.clear();
        }

        if (inventory != null) {
            inventory.clear();
        }
    }

    public int firstEmpty() {
        for (int i = 0; i < size; i++) {
            int finalI = i;
            long found = items.stream().filter(it -> it.slot == finalI).count();
            if (found == 0) return i;
        }
        return -1;
    }

    public void fill(ItemStack item, int from, int to, boolean overwrite) {
        for (int slot = from; slot <= to; slot++) {
            if (!overwrite && get(slot) != null) continue;
            set(new GUIItem(slot) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    return item;
                }
            });
        }
    }

    public void fill(ItemStack item) {
        fill(item, 0, size - 1, true);
    }

    public void border(ItemStack item) {
        border(item, 0, size - 1, true);
    }

    public void border(ItemStack item, int corner1, int corner2, boolean overwrite) {
        int width = 9;
        int topLeft = Math.min(corner1, corner2);
        int bottomRight = Math.max(corner1, corner2);
        for (int slot = topLeft; slot <= bottomRight; slot++) {
            int row = slot / width;
            int col = slot % width;

            int topRow = topLeft / width;
            int bottomRow = bottomRight / width;
            int leftCol = topLeft % width;
            int rightCol = bottomRight % width;

            boolean border =
                    row == topRow || row == bottomRow ||
                            col == leftCol || col == rightCol;

            if (border) {
                if (!overwrite && get(slot) != null) continue;
                set(new GUIItem(slot) {
                    @Override
                    public ItemStack getItem(BattlePlayer player) {
                        return item;
                    }
                });
            }
        }
    }

    public void open(BattlePlayer player) {
        Player p = player.getPlayer();
        ForceBattleGUI previous = GUI_MAP.get(p.getUniqueId());

        if (previous != null) {
            previous.removeViewer(p.getUniqueId());
            previous.onClose(new InventoryCloseEvent(p.getOpenInventory()), CloseReason.SERVER_EXITED);
            GUI_MAP.remove(p.getUniqueId());
        }

        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, size, title);
        }

        // Add to viewers
        this.currentViewers.add(p.getUniqueId());
        GUI_MAP.put(p.getUniqueId(), this);

        try {
            setItems(player);
            updateInventory(player);
            hasFinishedLoading = true;
        } catch (Exception e) {
            p.sendMessage("§cAn error occurred while opening the GUI.");
            e.printStackTrace();
            return;
        }

        p.openInventory(inventory);
        afterOpen(player);
    }

    public void close(BattlePlayer player, CloseReason reason) {
        UUID playerId = player.getPlayer().getUniqueId();
        player.getPlayer().closeInventory();
        onClose(new InventoryCloseEvent(player.getPlayer().getOpenInventory()), reason);
        removeViewer(playerId);
        GUI_MAP.remove(playerId);
    }

    private void removeViewer(UUID playerId) {
        currentViewers.remove(playerId);
    }

    public void updateInventory(BattlePlayer player) {
        synchronized (items) {
            Inventory inv = getInventory();
            for (GUIItem item : items) {
                inv.setItem(item.slot, item.getItem(player));
            }
        }
    }

    public void updateInventoryForPlayer(BattlePlayer player) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer.getOpenInventory().getTopInventory().getHolder() == this) {
            updateInventory(player);
        }
    }

    public abstract void setItems(BattlePlayer player);

    public abstract boolean allowItemMoving();

    public void onOpen(BattlePlayer player) {
    }

    public void afterOpen(BattlePlayer player) {
    }

    public void onClose(InventoryCloseEvent e, CloseReason reason) {
    }

    public enum CloseReason {
        PLAYER_EXITED,
        SERVER_EXITED,
        SIGN_OPENED
    }
}
