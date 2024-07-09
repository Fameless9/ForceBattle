package net.fameless.forcebattle.timer;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Format;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class TimerUI implements Listener, InventoryHolder {

    private final Timer timer;

    public TimerUI(Timer timer) {
        this.timer = timer;
    }

    public Inventory getTimerUI() {
        Inventory inventory = Bukkit.createInventory(this, 9, "Timer");
        inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.PURPLE_DYE),
                Collections.emptyList(),
                0,
                Collections.emptyList(),
                ChatColor.GOLD + "Change Color",
                ChatColor.GRAY + "Click to cycle through colors",
                ChatColor.GRAY + "Current color: " + (timer.getDecoration() != null ? timer.getDecoration() : "") + timer.getColor() + Format.formatName(timer.getColor().name()))
        );
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.CLOCK),
                Collections.emptyList(),
                0,
                Collections.emptyList(),
                ChatColor.GOLD + "Timer",
                ChatColor.GRAY + "Current time: " + ChatColor.GOLD + Format.formatTime(timer.getTime()),
                "",
                ChatColor.GRAY + "Right-click to add 1 Minute",
                ChatColor.GRAY + "Shift + Right-click to add 1 Hour",
                "",
                ChatColor.GRAY + "Left-click to subtract 1 Minute",
                ChatColor.GRAY + "Shift + Left-click to subtract 1 Hour",
                "",
                ChatColor.GRAY + "Mouse Wheel to toggle the timer")
        );
        inventory.setItem(5, ItemProvider.buildItem(new ItemStack(Material.FEATHER),
                Collections.emptyList(),
                0,
                Collections.emptyList(),
                ChatColor.GOLD + "Change Decoration",
                ChatColor.GRAY + "Click to cycle through decorations",
                ChatColor.GRAY + "Current decoration: " + timer.getColor() + (timer.getDecoration() != null ? timer.getDecoration() : "") + (timer.getDecoration() != null ? Format.formatName(timer.getDecoration().name()) : "None"))
        );
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TimerUI)) return;
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 3: {
                timer.cycleColors();
                break;
            }
            case 4: {
                switch (event.getClick()) {
                    case RIGHT: {
                        timer.setTime(timer.getTime() + 60);
                        timer.setStartTime(timer.getStartTime() + 60);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Added 1 Minute to the timer.");
                        timer.sendActionbar();
                        break;
                    }
                    case SHIFT_RIGHT: {
                        timer.setTime(timer.getTime() + 3600);
                        timer.setStartTime(timer.getStartTime() + 3600);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Added 1 Hour to the timer.");
                        timer.sendActionbar();
                        break;
                    }
                    case LEFT: {
                        if (timer.getTime() - 60 < 1) {
                            event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Timer can't go below 1 second.");
                            timer.sendActionbar();
                            return;
                        }
                        timer.setTime(timer.getTime() - 60);
                        timer.setStartTime(timer.getStartTime() - 60);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Subtracted 1 Minute from the timer.");
                        timer.sendActionbar();
                        break;
                    }
                    case SHIFT_LEFT: {
                        if (timer.getTime() - 3600 < 1) {
                            event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Timer can't go below 1 second.");
                            timer.sendActionbar();
                            return;
                        }
                        timer.setTime(timer.getTime() - 3600);
                        timer.setStartTime(timer.getStartTime() - 3600);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Subtracted 1 Hour from the timer.");
                        timer.sendActionbar();
                        break;
                    }
                    case MIDDLE: {
                        timer.toggle((Player) event.getWhoClicked());
                        break;
                    }
                }
                break;
            }
            case 5: {
                timer.cycleDecorations();
                break;
            }
        }
        event.getWhoClicked().openInventory(getTimerUI());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return getTimerUI();
    }
}