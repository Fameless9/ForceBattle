package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.ChainManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Challenge;
import org.bukkit.*;
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

import java.util.Collections;

import static net.fameless.forcebattle.util.ItemProvider.buildItem;

public class MenuUI implements CommandExecutor, Listener, InventoryHolder {

    public static boolean isKeepInventory;
    private static boolean backpackEnabled;

    private Inventory inventory;

    public static boolean isBackpackEnabled() {
        return backpackEnabled;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!sender.hasPermission("forcebattle.menu")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.menu'");
            return false;
        }
        if (sender instanceof Player) {
            ((Player) sender).openInventory(menuGUI());
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuUI)) return;
        event.setCancelled(true);
        if (!event.getWhoClicked().hasPermission("forcebattle.menu")) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.menu'");
            return;
        }
        int slot = event.getSlot();

        boolean isForceItemEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ITEM);
        boolean isForceMobEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_MOB);
        boolean isForceBiomeEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_BIOME);
        boolean isForceAdvancementEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT);
        boolean isForceHeightEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_HEIGHT);

        switch (slot) {
            case 0: {
                if (isForceItemEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Item has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Item has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 1: {
                if (isForceMobEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Mob has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Mob has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 2: {
                if (isForceBiomeEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Biome has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Biome has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 3: {
                if (isForceAdvancementEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Advancement has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Advancement has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 4: {
                if (isForceHeightEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Height has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Height has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                break;
            }
            case 6: {
                backpackEnabled = !backpackEnabled;
                Bukkit.broadcastMessage(ChatColor.GOLD + "Backpack usage has been set to " + isBackpackEnabled());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory().getTitle().contains("Backpack")) {
                        player.closeInventory();
                    }
                }
                break;
            }
            case 7: {
                isKeepInventory = !isKeepInventory;
                for (World world : Bukkit.getServer().getWorlds()) {
                    if (world != null) {
                        world.setGameRule(GameRule.KEEP_INVENTORY, isKeepInventory);
                        event.getWhoClicked().sendMessage(ChatColor.GOLD + "Keep Inventory has been set to " + isKeepInventory + " for world: " + world.getName());
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + "Keep Inventory has been set to " + isKeepInventory);
                break;
            }
            case 8: {
                if (ChainManager.isChainModeEnabled()) {
                    ChainManager.setChainModeEnabled(false);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Chain Mode has been disabled.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ChainManager.setChainModeEnabled(true);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Chain Mode has been enabled.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                break;
            }
        }
        event.getWhoClicked().openInventory(menuGUI());
    }

    private Inventory menuGUI() {

        boolean isForceItemEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ITEM);
        boolean isForceMobEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_MOB);
        boolean isForceBiomeEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_BIOME);
        boolean isForceAdvancementEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT);
        boolean isForceHeightEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_HEIGHT);

        inventory = Bukkit.createInventory(this, 9, ChatColor.GOLD + "Select Challenges");
        inventory.setItem(0, buildItem(new ItemStack(Material.ITEM_FRAME), null, 0, null, ChatColor.GOLD + "Force Item",
                ChatColor.GRAY + "Click to toggle Force Item.", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceItemEnabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(1, buildItem(new ItemStack(Material.SPIDER_SPAWN_EGG), null, 0, null, ChatColor.GOLD + "Force Mob",
                ChatColor.GRAY + "Click to toggle Force Mob.", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceMobEnabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(2, buildItem(new ItemStack(Material.TALL_GRASS), null, 0, null, ChatColor.GOLD + "Force Biome",
                ChatColor.GRAY + "Click to toggle Force Biome.", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceBiomeEnabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(3, buildItem(new ItemStack(Material.BLAZE_ROD), null, 0, null, ChatColor.GOLD + "Force Advancement",
                ChatColor.GRAY + "Click to toggle Force Advancement.", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceAdvancementEnabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(4, buildItem(new ItemStack(Material.SCAFFOLDING), null, 0, null, ChatColor.GOLD + "Force Height",
                ChatColor.GRAY + "Click to toggle Force Height.", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceHeightEnabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(8, buildItem(new ItemStack(Material.CHAIN), null, 0, null, ChatColor.GOLD + "Chain Mode",
                ChatColor.GRAY + "Click to toggle Chain Mode.", "", ChatColor.BLUE + "Currently set to: " + (ChainManager.isChainModeEnabled() ?
                        ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(7, buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Keep Inventory", ChatColor.GRAY + "Click to toggle Keep Inventory in all worlds.", "",
                ChatColor.BLUE + "Currently set to: " +
                        (isKeepInventory ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        inventory.setItem(6, buildItem(new ItemStack(Material.CHEST), null, 0, null, ChatColor.GOLD + "Backpack",
                ChatColor.GRAY + "Click to toggle team/personal backpacks.", "", ChatColor.BLUE + "Currently set to: " + (isBackpackEnabled() ?
                        ChatColor.GREEN + "true" : ChatColor.RED + "false")));
        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
