package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.ObjectiveManager;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class JokerUI implements Listener, CommandExecutor, InventoryHolder {

    private Inventory inventory;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
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
        if (!(event.getInventory().getHolder() instanceof JokerUI)) return;
        if (event.getSlot() != 4) return;
        event.setCancelled(true);

        Player target = Bukkit.getPlayer(event.getView().getTitle().split("Edit amount for ")[1]);
        if (target == null) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Player couldn't be found.");
            event.getWhoClicked().closeInventory();
            return;
        }

        switch (event.getClick()) {
            case LEFT: {
                ObjectiveManager.giveSkipItem(target, 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Gave one skip to " + target.getName() + ".");
                break;
            }
            case SHIFT_LEFT: {
                Inventory inventory1 = target.getInventory();
                int skipSlot = -1;
                for (int i = 0; i < 36; i++) {
                    ItemStack stack = inventory1.getItem(i);
                    if (stack == null) continue;
                    if (stack.isSimilar(ItemProvider.getSkipItem(1))) {
                        skipSlot = i;
                        break;
                    }
                }
                if (skipSlot < 0) return;
                ItemStack stack = inventory1.getItem(skipSlot);
                if (stack == null) return;
                int newAmount = stack.getAmount() - 1;
                if (newAmount < 0) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "No skips left.");
                    return;
                }
                inventory1.setItem(skipSlot, ItemProvider.getSkipItem(newAmount));
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Took one skip from " + target.getName() + ".");
                break;
            }
            case RIGHT: {
                ObjectiveManager.giveSwapItem(target, 1);
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Gave one swap to " + target.getName() + ".");
                break;
            }
            case SHIFT_RIGHT: {
                Inventory inventory1 = target.getInventory();
                int swapSlot = -1;
                for (int i = 0; i < 36; i++) {
                    ItemStack stack = inventory1.getItem(i);
                    if (stack == null) continue;
                    if (stack.isSimilar(ItemProvider.getSwapitem(1))) {
                        swapSlot = i;
                        break;
                    }
                }
                if (swapSlot < 0) return;
                ItemStack stack = inventory1.getItem(swapSlot);
                if (stack == null) return;
                int newAmount = stack.getAmount() - 1;
                if (newAmount < 0) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "No swaps left.");
                    return;
                }
                inventory1.setItem(swapSlot, ItemProvider.getSwapitem(newAmount));
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Took one swap from " + target.getName() + ".");
                break;
            }
        }
        event.getWhoClicked().openInventory(getJokerInventory(target));
    }

    public Inventory getJokerInventory(Player player) {

        int skipAmount = 0;
        int swapAmount = 0;
        Inventory playerInv = player.getInventory();

        for (ItemStack is : playerInv.getContents()) {
            if (is == null) continue;
            if (is.isSimilar(ItemProvider.getSkipItem(1))) {
                skipAmount += is.getAmount();
                continue;
            }
            if (is.isSimilar(ItemProvider.getSwapitem(1))) {
                swapAmount += is.getAmount();
            }
        }

        inventory = Bukkit.createInventory(this, 9, ChatColor.GOLD + "Edit amount for " + player.getName());
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.BARRIER), null, 0, null,
                ChatColor.GOLD + "Adjust amounts", "", ChatColor.BLUE + "Current skips: " + skipAmount,
                ChatColor.BLUE + "Current Swaps: " + swapAmount, "", ChatColor.GRAY + "Left-click: Skips +1",
                ChatColor.GRAY + "Shift + Left-click: Skips -1", "", ChatColor.GRAY + "Right-click: Swaps +1", ChatColor.GRAY + "Shift + Right-click: Swaps -1"));

        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
