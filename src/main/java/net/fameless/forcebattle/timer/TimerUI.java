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

    public Inventory getTimerUI() {
        Inventory inventory = Bukkit.createInventory(this, 9, "Timer");
        inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.PURPLE_DYE),
                Collections.emptyList(),
                0,
                Collections.emptyList(),
                ChatColor.GOLD + "Change Color",
                ChatColor.GRAY + "Click to cycle through colors",
                ChatColor.GRAY + "Current color: " + (ForceBattlePlugin.getInstance().getTimer().getDecoration() != null ? ForceBattlePlugin.getInstance().getTimer().getDecoration() : "") + ForceBattlePlugin.getInstance().getTimer().getColor() + Format.formatName(ForceBattlePlugin.getInstance().getTimer().getColor().name()))
        );
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.CLOCK),
                Collections.emptyList(),
                0,
                Collections.emptyList(),
                ChatColor.GOLD + "Timer",
                ChatColor.GRAY + "Current time: " + ChatColor.GOLD + Format.formatTime(ForceBattlePlugin.getInstance().getTimer().getTime()),
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
                ChatColor.GRAY + "Current decoration: " + ForceBattlePlugin.getInstance().getTimer().getColor() + (ForceBattlePlugin.getInstance().getTimer().getDecoration() != null ? ForceBattlePlugin.getInstance().getTimer().getDecoration() : "") + (ForceBattlePlugin.getInstance().getTimer().getDecoration() != null ? Format.formatName(ForceBattlePlugin.getInstance().getTimer().getDecoration().name()) : "None"))
        );
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TimerUI)) return;
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 3: {
                ForceBattlePlugin.getInstance().getTimer().cycleColors();
                break;
            }
            case 4: {
                switch (event.getClick()) {
                    case RIGHT: {
                        ForceBattlePlugin.getInstance().getTimer().setTime(ForceBattlePlugin.getInstance().getTimer().getTime() + 60);
                        ForceBattlePlugin.getInstance().getTimer().setStartTime(ForceBattlePlugin.getInstance().getTimer().getStartTime() + 60);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Added 1 Minute to the timer.");
                        ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                        break;
                    }
                    case SHIFT_RIGHT: {
                        ForceBattlePlugin.getInstance().getTimer().setTime(ForceBattlePlugin.getInstance().getTimer().getTime() + 3600);
                        ForceBattlePlugin.getInstance().getTimer().setStartTime(ForceBattlePlugin.getInstance().getTimer().getStartTime() + 3600);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Added 1 Hour to the timer.");
                        ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                        break;
                    }
                    case LEFT: {
                        if (ForceBattlePlugin.getInstance().getTimer().getTime() - 60 < 1) {
                            event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Timer can't go below 1 second.");
                            ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                            return;
                        }
                        ForceBattlePlugin.getInstance().getTimer().setTime(ForceBattlePlugin.getInstance().getTimer().getTime() - 60);
                        ForceBattlePlugin.getInstance().getTimer().setStartTime(ForceBattlePlugin.getInstance().getTimer().getStartTime() - 60);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Subtracted 1 Minute from the timer.");
                        ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                        break;
                    }
                    case SHIFT_LEFT: {
                        if (ForceBattlePlugin.getInstance().getTimer().getTime() - 3600 < 1) {
                            event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Timer can't go below 1 second.");
                            ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                            return;
                        }
                        ForceBattlePlugin.getInstance().getTimer().setTime(ForceBattlePlugin.getInstance().getTimer().getTime() - 3600);
                        ForceBattlePlugin.getInstance().getTimer().setStartTime(ForceBattlePlugin.getInstance().getTimer().getStartTime() - 3600);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Subtracted 1 Hour from the timer.");
                        ForceBattlePlugin.getInstance().getTimer().sendActionbar();
                        break;
                    }
                    case MIDDLE: {
                        ForceBattlePlugin.getInstance().getTimer().toggle((Player) event.getWhoClicked());
                        break;
                    }
                }
                break;
            }
            case 5: {
                ForceBattlePlugin.getInstance().getTimer().cycleDecorations();
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