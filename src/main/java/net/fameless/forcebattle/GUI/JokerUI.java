package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.ItemManager;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JokerUI implements Listener, CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("forcebattle.jokers")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.jokers'.");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (args.length == 0) {
            ((Player) sender).openInventory(getJokerInventory((Player) sender));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player couldn't be found.");
                return false;
            }
            ((Player) sender).openInventory(getJokerInventory(target));
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Edit amount for")) return;
        if (event.getSlot() != 4) return;

        Player target = Bukkit.getPlayer(event.getView().getTitle().split("Edit amount for ")[0]);
        if (target == null) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Player couldn't be found.");
            event.getWhoClicked().closeInventory();
            return;
        }

        int skipAmount = 0;
        int swapAmount = 0;
        Inventory playerInv = target.getInventory();

        for (ItemStack is : playerInv.getContents()) {
            if (is.equals(ItemProvider.getSkipItem(1))) {
                skipAmount++;
                continue;
            }
            if (is.equals(ItemProvider.getSwapitem(1))) {
                swapAmount++;
            }
        }

        switch (event.getClick()) {
            case LEFT: {
                ItemManager.giveSkipItem(target, skipAmount + 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Gave one skip to " + target.getName() + ".");
                break;
            }
            case SHIFT_LEFT: {
                ItemManager.giveSkipItem(target, skipAmount - 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Took one skip from " + target.getName() + ".");
                break;
            }
            case RIGHT: {
                ItemManager.giveSwapItem(target, swapAmount + 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Gave one swap to " + target.getName() + ".");
                break;
            }
            case SHIFT_RIGHT: {
                ItemManager.giveSwapItem(target, swapAmount - 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Took one swap from " + target.getName()  + ".");
                break;
            }
        }
    }

    public Inventory getJokerInventory(Player player) {

        int skipAmount = 0;
        int swapAmount = 0;
        Inventory playerInv = player.getInventory();

        for (ItemStack is : playerInv.getContents()) {
            if (is.equals(ItemProvider.getSkipItem(1))) {
                skipAmount++;
                continue;
            }
            if (is.equals(ItemProvider.getSwapitem(1))) {
                swapAmount++;
            }
        }

        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Edit amount for " + player.getName());
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.BARRIER), null, 0, null,
                ChatColor.GOLD + "Adjust amounts", "", ChatColor.BLUE + "Current skips: " + skipAmount,
                ChatColor.BLUE + "Current Swaps: " + swapAmount, "", ChatColor.GRAY + "Leftclick: Skips +1",
                ChatColor.GRAY + "Shift + Leftclick: Skips -1", "", ChatColor.GRAY + "Rightclick: Swaps +1", ChatColor.GRAY + "Shift + Rightclick: Swaps -1"));

        return inventory;
    }
}
