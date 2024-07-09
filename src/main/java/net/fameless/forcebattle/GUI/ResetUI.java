package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
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

    private final ObjectiveManager objectiveManager;
    private final ForceBattlePlugin forceBattlePlugin;
    private final HashMap<UUID, UUID> commandMap = new HashMap<>();
    private Inventory inventory;

    public ResetUI(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
        this.objectiveManager = forceBattlePlugin.getObjectiveManager();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (!sender.hasPermission("forcebattle.reset.player")) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Lacking permission: 'forcebattle.reset.player'");
            return false;
        }

        Player target = null;
        if (args.length == 1) {
            target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                commandMap.put(player.getUniqueId(), target.getUniqueId());
            } else {
                sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player couldn't be found.");
            }
        }

        inventory = Bukkit.createInventory(this, 9, "Resets");
        if (target == null) {
            inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.CHAIN_COMMAND_BLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Quick Reset Everyone", "", ChatColor.GRAY + "Quick reset function.", "", ChatColor.GRAY + "Resets:", ChatColor.GRAY + "- Timer", ChatColor.GRAY + "- Challenge progress", ChatColor.GRAY + "- Postion", ChatColor.GRAY + "- Inventories", ChatColor.GRAY + "- Health/Food Level", ChatColor.GRAY + "- Jokers"));
        } else {
            inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.CHAIN_COMMAND_BLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Quick Reset " + target.getName(), "", ChatColor.GRAY + "Quick reset " + target.getName() + ".", "", ChatColor.GRAY + "Resets:", ChatColor.GRAY + "- Challenge progress", ChatColor.GRAY + "- Position", ChatColor.GRAY + "- Inventory", ChatColor.GRAY + "- Health/Food Level", ChatColor.GRAY + "- Jokers"));
        }
        inventory.setItem(1, ItemProvider.buildItem(new ItemStack(Material.CLOCK), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset Timer", "", ChatColor.GRAY + "Click to reset the timer"));

        if (target == null) {
            inventory.setItem(2, ItemProvider.buildItem(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Clear Inventories", "", ChatColor.GRAY + "Clears inventory of every player"));
        } else {
            inventory.setItem(2, ItemProvider.buildItem(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Clear Inventory of " + target.getName(), "", ChatColor.GRAY + "Clears inventory of " + target.getName()));
        }

        if (target == null) {
            inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Refill Jokers", "", ChatColor.GRAY + "Refill Jokers and Swappers for every player"));
        } else {
            inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Refill Jokers for " + target.getName(), "", ChatColor.GRAY + "Refill Jokers and Swappers for " + target.getName()));
        }

        if (target == null) {
            inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset points", "", ChatColor.GRAY + "Resets challenge progress of every player"));
        } else {
            inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), Collections.emptyList(), 0, Collections.emptyList(), ChatColor.GOLD + "Reset points of " + target.getName(), "", ChatColor.GRAY + "Resets challenge progress of " + target.getName()));
        }

        player.openInventory(inventory);

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ResetUI)) return;
        event.setCancelled(true);
        Player target = null;
        if (commandMap.containsKey(event.getWhoClicked().getUniqueId())) {
            if (Bukkit.getPlayer(commandMap.get(event.getWhoClicked().getUniqueId())) == null) {
                event.getWhoClicked().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Target player is not available anymore.");
                event.getWhoClicked().closeInventory();
                return;
            }
            target = Bukkit.getPlayer(commandMap.get(event.getWhoClicked().getUniqueId()));
        }
        switch (event.getSlot()) {
            case 0: {
                if (target != null) {
                    target.getInventory().clear();
                    objectiveManager.giveJokers(target);
                    objectiveManager.resetProgress(target);
                    target.teleport(target.getWorld().getSpawnLocation());
                    target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    target.setFoodLevel(20);
                    forceBattlePlugin.getBossbarManager().updateBossbar(target);
                    forceBattlePlugin.getNametagManager().updateNametag(target);
                    target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "You have been reset.");
                    break;
                }

                forceBattlePlugin.getTimer().setTime(forceBattlePlugin.getTimer().getStartTime());
                forceBattlePlugin.getTimer().setRunning(false);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                    objectiveManager.giveJokers(player);
                    objectiveManager.resetProgress(player);
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    player.setFoodLevel(20);
                    forceBattlePlugin.getBossbarManager().updateBossbar(player);
                    forceBattlePlugin.getNametagManager().updateNametag(player);
                }

                Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Challenge has been reset.");
                break;
            }
            case 1: {
                forceBattlePlugin.getTimer().setTime(forceBattlePlugin.getTimer().getStartTime());
                forceBattlePlugin.getTimer().setRunning(false);
                Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Timer has been reset.");
                break;
            }
            case 2: {
                if (target != null) {
                    target.getInventory().clear();
                    target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Your inventory has been cleared.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                }

                Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Inventories have been cleared.");
                break;
            }
            case 3: {
                if (target != null) {
                    objectiveManager.giveJokers(target);
                    target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Your jokers have been refilled.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    objectiveManager.giveJokers(player);
                }

                Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Jokers have been refilled.");
                break;
            }
            case 4: {
                if (target != null) {
                    objectiveManager.resetProgress(target);
                    forceBattlePlugin.getNametagManager().updateNametag(target);
                    forceBattlePlugin.getBossbarManager().updateBossbar(target);
                    target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Your points have been reset.");
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    objectiveManager.resetProgress(player);
                    forceBattlePlugin.getNametagManager().updateNametag(player);
                    forceBattlePlugin.getBossbarManager().updateBossbar(player);
                }

                Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Points have been reset.");
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
        commandMap.remove(event.getPlayer().getUniqueId());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}