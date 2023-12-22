package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.ChainManager;
import net.fameless.forcebattle.manager.ItemManager;
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
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

import static net.fameless.forcebattle.util.ItemProvider.buildItem;

public class MenuUI implements CommandExecutor, Listener {

    public static boolean isKeepInventory;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(menuGUI());
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith("Select Challenges")) return;
        if (!event.getWhoClicked().hasPermission("forcebattle.menu")) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.menu'.");
            event.getWhoClicked().closeInventory();
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();

        boolean isForceItemEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_ITEM);
        boolean isForceMobEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_MOB);
        boolean isForceBiomeEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_BIOME);
        boolean isForceAdvancementEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT);
        boolean isForceHeightEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_HEIGHT);

        switch (slot) {
            case 0: {
                if (isForceItemEnabled) {
                    ItemManager.removeChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Item has been disabled.");
                    ChainManager.updateLists();
                    if (ItemManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ItemManager.addChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Item has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 1: {
                if (isForceMobEnabled) {
                    ItemManager.removeChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Mob has been disabled.");
                    ChainManager.updateLists();
                    if (ItemManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ItemManager.addChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Mob has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 2: {
                if (isForceBiomeEnabled) {
                    ItemManager.removeChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Biome has been disabled.");
                    ChainManager.updateLists();
                    if (ItemManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ItemManager.addChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Biome has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 3: {
                if (isForceAdvancementEnabled) {
                    ItemManager.removeChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Advancement has been disabled.");
                    ChainManager.updateLists();
                    if (ItemManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ItemManager.addChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Advancement has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 4: {
                if (isForceHeightEnabled) {
                    ItemManager.removeChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Height has been disabled.");
                    ChainManager.updateLists();
                    if (ItemManager.activeChallenges.isEmpty()) {
                        Timer.setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ItemManager.addChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Force Height has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
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
                        ItemManager.updateObjective(player);
                    }
                } else {
                    ChainManager.setChainModeEnabled(true);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Chain Mode has been enabled.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemManager.updateObjective(player);
                    }
                }
                break;
            }
        }
        event.getWhoClicked().openInventory(menuGUI());
    }

    private Inventory menuGUI() {

        boolean isForceItemEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_ITEM);
        boolean isForceMobEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_MOB);
        boolean isForceBiomeEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_BIOME);
        boolean isForceAdvancementEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT);
        boolean isForceHeightEnabled = ItemManager.activeChallenges.contains(Challenge.FORCE_HEIGHT);

        Inventory inventory = Bukkit.createInventory(null, 9,ChatColor.GOLD + "Select Challenges");
        inventory.setItem(0, buildItem(new ItemStack(Material.ITEM_FRAME), null, 0, null, ChatColor.GOLD + "Force Item",
                ChatColor.GRAY + "Click to toggle Force Item", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceItemEnabled ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(1, buildItem(new ItemStack(Material.SPIDER_SPAWN_EGG), null, 0, null, ChatColor.GOLD + "Force Mob",
                ChatColor.GRAY + "Click to toggle Force Mob", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceMobEnabled ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(2, buildItem(new ItemStack(Material.GRASS), null, 0, null, ChatColor.GOLD + "Force Biome",
                ChatColor.GRAY + "Click to toggle Force Biome", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceBiomeEnabled ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(3, buildItem(new ItemStack(Material.BLAZE_ROD), null, 0, null, ChatColor.GOLD + "Force Advancement",
                ChatColor.GRAY + "Click to toggle Force Advancement", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceAdvancementEnabled ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(4, buildItem(new ItemStack(Material.SCAFFOLDING), null, 0, null, ChatColor.GOLD + "Force Height",
                ChatColor.GRAY + "Click to toggle Force Height", "", ChatColor.BLUE + "Currently set to: " +
                        (isForceHeightEnabled ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(8, buildItem(new ItemStack(Material.CHAIN), null, 0, null, ChatColor.GOLD + "Chain Mode",
                ChatColor.GRAY + "Click to toggle Chain Mode.", "", ChatColor.BLUE + "Currently set to: " + (ChainManager.isChainModeEnabled() ?
                        ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        inventory.setItem(7, buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Keep Inventory", "", ChatColor.GRAY + "Click to toggle Keep Inventory in all worlds.", "",
                ChatColor.BLUE + "Currently set to: " +
                        (isKeepInventory ? ChatColor.GREEN + "true." : ChatColor.RED + "false.")));
        return inventory;
    }
}
