package net.fameless.forcebattle.timer;

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

    private Inventory inventory;

    public Inventory getTimerUI() {
        inventory = Bukkit.createInventory(this, 9, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Timer");
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.CLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Timer", "", ChatColor.GOLD + "Rightclick to add 1 Minute", ChatColor.GOLD + "Shift + Rightclick to add 1 Hour", "", ChatColor.GOLD + "Leftclick to subtract 1 Minute", ChatColor.GOLD + "Shift + Leftclick to subtract 1 Hour", "", ChatColor.GOLD + "Mouse Wheel to toggle the timer"));
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TimerUI)) return;
        if (event.getSlot() != 4) return;
        event.setCancelled(true);
        switch (event.getClick()) {
            case RIGHT: {
                Timer.setTime(Timer.getTime() + 60);
                Timer.setStartTime(Timer.getStartTime() + 60);
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Added 1 Minute to the timer.");
                Timer.sendActionbar();
                break;
            }
            case SHIFT_RIGHT: {
                Timer.setTime(Timer.getTime() + 3600);
                Timer.setStartTime(Timer.getStartTime() + 3600);
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Added 1 Hour to the timer.");
                Timer.sendActionbar();
                break;
            }
            case LEFT: {
                if (Timer.getTime() - 60 < 1) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Timer can't go below 1 second.");
                    Timer.sendActionbar();
                    return;
                }
                Timer.setTime(Timer.getTime() - 60);
                Timer.setStartTime(Timer.getStartTime() - 60);
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Subtracted 1 Minute from the timer.");
                Timer.sendActionbar();
                break;
            }
            case SHIFT_LEFT: {
                if (Timer.getTime() - 3600 < 1) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Timer can't go below 1 second.");
                    Timer.sendActionbar();
                    return;
                }
                Timer.setTime(Timer.getTime() - 3600);
                Timer.setStartTime(Timer.getStartTime() - 3600);
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Subtracted 1 Hour from the timer.");
                Timer.sendActionbar();
                break;
            }
            case MIDDLE: {
                Timer.toggle((Player) event.getWhoClicked());
                break;
            }
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}