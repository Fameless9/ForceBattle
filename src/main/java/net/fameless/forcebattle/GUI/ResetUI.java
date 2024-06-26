package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ResetUI implements Listener, CommandExecutor, InventoryHolder {

    private static final HashMap<UUID, UUID> commandMap = new HashMap<>();
    private Inventory inventory;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (!sender.hasPermission("forcebattle.reset.player")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.reset.player'");
            return false;
        }

        Player target = null;
        if (args.length == 1) {
            target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                ResetUI.commandMap.put(player.getUniqueId(), target.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "Player couldn't be found.");
            }
        }

        inventory = Bukkit.createInventory(this, 9, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Resets");
        if (target == null) {
            inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.CHAIN_COMMAND_BLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Quick Reset Everyone", "", ChatColor.BLUE + "Quick reset function.", "", ChatColor.BLUE + "Resets:", ChatColor.BLUE + "- Timer", ChatColor.BLUE + "- Challenge progress", ChatColor.BLUE + "- Postion", ChatColor.BLUE + "- Inventories", ChatColor.BLUE + "- Health/Food Level", ChatColor.BLUE + "- Jokers"));
        } else {
            inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.CHAIN_COMMAND_BLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Quick Reset " + target.getName(), "", ChatColor.BLUE + "Quick reset " + target.getName() + ".", "", ChatColor.BLUE + "Resets:", ChatColor.BLUE + "- Challenge progress", ChatColor.BLUE + "- Position", ChatColor.BLUE + "- Inventory", ChatColor.BLUE + "- Health/Food Level", ChatColor.BLUE + "- Jokers"));
        }
        inventory.setItem(1, ItemProvider.buildItem(new ItemStack(Material.CLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset Timer", "", ChatColor.BLUE + "Click to globally reset the timer"));

        if (target == null) {
            inventory.setItem(2, ItemProvider.buildItem(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Clear Inventories", "", ChatColor.BLUE + "Clears inventory of every player"));
        } else {
            inventory.setItem(2, ItemProvider.buildItem(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Clear Inventory of " + target.getName(), "", ChatColor.BLUE + "Clears inventory of " + target.getName()));
        }

        if (target == null) {
            inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Refill Jokers", "", ChatColor.BLUE + "Refill Jokers and Swappers for every player"));
        } else {
            inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Refill Jokers for " + target.getName(), "", ChatColor.BLUE + "Refill Jokers and Swappers for " + target.getName()));
        }

        if (target == null) {
            inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset points", "", ChatColor.BLUE + "Resets challenge progress of every player"));
        } else {
            inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset points of " + target.getName(), "", ChatColor.BLUE + "Resets challenge progress of " + target.getName()));
        }

        player.openInventory(inventory);

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ResetUI)) return;
        event.setCancelled(true);
        Player target = null;
        if (ResetUI.commandMap.containsKey(event.getWhoClicked().getUniqueId())) {
            if (Bukkit.getPlayer(ResetUI.commandMap.get(event.getWhoClicked().getUniqueId())) == null) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Target player is not available anymore.");
                event.getWhoClicked().closeInventory();
                return;
            }
            target = Bukkit.getPlayer(ResetUI.commandMap.get(event.getWhoClicked().getUniqueId()));
        }
        switch (event.getSlot()) {
            case 0: {
                if (target != null) {
                    target.getInventory().clear();
                    ObjectiveManager.giveJokers(target);
                    ObjectiveManager.resetProgress(target);
                    target.teleport(target.getWorld().getSpawnLocation());
                    target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    target.setFoodLevel(20);
                    target.sendMessage(ChatColor.GOLD + "You have been reset.");
                    break;
                }

                Timer.setTime(Timer.getStartTime());
                Timer.setRunning(false);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                    ObjectiveManager.giveJokers(player);
                    ObjectiveManager.resetProgress(player);
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    player.setFoodLevel(20);
                }

                Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge has been reset.");
                break;
            }
            case 1: {
                Timer.setTime(Timer.getStartTime());
                Timer.setRunning(false);
                Bukkit.broadcastMessage(ChatColor.GOLD + "Timer has been reset.");
                break;
            }
            case 2: {
                if (target != null) {
                    target.getInventory().clear();
                    target.sendMessage(ChatColor.GOLD + "Your inventory has been cleared.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                }

                Bukkit.broadcastMessage(ChatColor.GOLD + "Inventories have been cleared.");
                break;
            }
            case 3: {
                if (target != null) {
                    ObjectiveManager.giveJokers(target);
                    target.sendMessage(ChatColor.GOLD + "Your jokers have been refilled.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ObjectiveManager.giveJokers(player);
                }

                Bukkit.broadcastMessage(ChatColor.GOLD + "Jokers have been refilled.");
                break;
            }
            case 4: {
                if (target != null) {
                    ObjectiveManager.resetProgress(target);
                    NametagManager.updateNametag(target);
                    BossbarManager.updateBossbar(target);
                    target.sendMessage(ChatColor.GOLD + "Your points have been reset.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ObjectiveManager.resetProgress(player);
                    NametagManager.updateNametag(player);
                    BossbarManager.updateBossbar(player);
                }

                Bukkit.broadcastMessage(ChatColor.GOLD + "Points have been reset.");
                break;
            }
        }

        for (Team team : TeamManager.getTeams()) {
            team.updatePoints();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ResetUI)) return;
        ResetUI.commandMap.remove(event.getPlayer().getUniqueId());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}