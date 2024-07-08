package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.ChainManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.ItemProvider;
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
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Lacking permission: 'forcebattle.menu'");
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
            event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Lacking permission: 'forcebattle.menu'");
            event.getWhoClicked().closeInventory();
            return;
        }
        int slot = event.getSlot();

        boolean isForceItemEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ITEM);
        boolean isForceMobEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_MOB);
        boolean isForceBiomeEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_BIOME);
        boolean isForceAdvancementEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT);
        boolean isForceHeightEnabled = ObjectiveManager.activeChallenges.contains(Challenge.FORCE_HEIGHT);

        switch (slot) {
            case 10: {
                if (isForceItemEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Item " + ChatColor.GRAY + "has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        ForceBattlePlugin.getInstance().getTimer().setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_ITEM);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Item " + ChatColor.GRAY + "has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 12: {
                if (isForceMobEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Mob " + ChatColor.GRAY + "has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        ForceBattlePlugin.getInstance().getTimer().setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_MOB);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Mob " + ChatColor.GRAY + "has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 14: {
                if (isForceBiomeEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Biome " + ChatColor.GRAY + "has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        ForceBattlePlugin.getInstance().getTimer().setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_BIOME);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Biome " + ChatColor.GRAY + "has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 16: {
                if (isForceAdvancementEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Advancement " + ChatColor.GRAY + "has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        ForceBattlePlugin.getInstance().getTimer().setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_ADVANCEMENT);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Advancement " + ChatColor.GRAY + "has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 28: {
                if (isForceHeightEnabled) {
                    ObjectiveManager.removeChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Height " + ChatColor.GRAY + "has been disabled.");
                    ChainManager.updateLists();
                    if (ObjectiveManager.activeChallenges.isEmpty()) {
                        ForceBattlePlugin.getInstance().getTimer().setRunning(false);
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ObjectiveManager.addChallenge(Challenge.FORCE_HEIGHT);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Force Height " + ChatColor.GRAY + "has been added.");
                    ChainManager.updateLists();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 30: {
                backpackEnabled = !backpackEnabled;
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Backpack usage has been set to " + ChatColor.GOLD + isBackpackEnabled());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory().getTitle().contains("Backpack")) {
                        player.closeInventory();
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 32: {
                isKeepInventory = !isKeepInventory;
                for (World world : Bukkit.getServer().getWorlds()) {
                    if (world != null) {
                        world.setGameRule(GameRule.KEEP_INVENTORY, isKeepInventory);
                        event.getWhoClicked().sendMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Keep Inventory " + ChatColor.GRAY + "has been set to " + ChatColor.GOLD + isKeepInventory + ChatColor.GRAY + " for world: " + world.getName());
                    }
                }
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Keep Inventory " + ChatColor.GRAY + "has been set to " + ChatColor.GOLD + isKeepInventory);
                event.getWhoClicked().openInventory(menuGUI());
                break;
            }
            case 34: {
                if (ChainManager.isChainModeEnabled()) {
                    ChainManager.setChainModeEnabled(false);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Chain Mode " + ChatColor.GRAY + "has been disabled.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                } else {
                    ChainManager.setChainModeEnabled(true);
                    Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Chain Mode " + ChatColor.GRAY + "has been enabled.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ObjectiveManager.updateObjective(player);
                    }
                }
                event.getWhoClicked().openInventory(menuGUI());
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

        inventory = Bukkit.createInventory(this, 54, "Select Challenges");
        ItemStack borderItem = buildItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), null, 0, null, " ");

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || (i + 1) % 9 == 0) {
                inventory.setItem(i, borderItem);
            }
        }

        ItemStack enabled = ItemProvider.buildItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), null, 0, null, ChatColor.GREEN.toString() + ChatColor.BOLD + "ENABLED");
        ItemStack disabled = ItemProvider.buildItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), null, 0, null, ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED");

        inventory.setItem(10, buildItem(new ItemStack(Material.ITEM_FRAME), null, 0, null, ChatColor.GOLD + "Force Item",
                ChatColor.GRAY + "Click to toggle Force Item.", "", ChatColor.GRAY + "Currently set to: " +
                        (isForceItemEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(19, isForceItemEnabled ? enabled : disabled);

        inventory.setItem(12, buildItem(new ItemStack(Material.SPIDER_SPAWN_EGG), null, 0, null, ChatColor.GOLD + "Force Mob",
                ChatColor.GRAY + "Click to toggle Force Mob.", "", ChatColor.GRAY + "Currently set to: " +
                        (isForceMobEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(21, isForceMobEnabled ? enabled : disabled);

        inventory.setItem(14, buildItem(new ItemStack(Material.TALL_GRASS), null, 0, null, ChatColor.GOLD + "Force Biome",
                ChatColor.GRAY + "Click to toggle Force Biome.", "", ChatColor.GRAY + "Currently set to: " +
                        (isForceBiomeEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(23, isForceBiomeEnabled ? enabled : disabled);

        inventory.setItem(16, buildItem(new ItemStack(Material.BLAZE_ROD), null, 0, null, ChatColor.GOLD + "Force Advancement",
                ChatColor.GRAY + "Click to toggle Force Advancement.", "", ChatColor.GRAY + "Currently set to: " +
                        (isForceAdvancementEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(25, isForceAdvancementEnabled ? enabled : disabled);

        inventory.setItem(28, buildItem(new ItemStack(Material.SCAFFOLDING), null, 0, null, ChatColor.GOLD + "Force Height",
                ChatColor.GRAY + "Click to toggle Force Height.", "", ChatColor.GRAY + "Currently set to: " +
                        (isForceHeightEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(37, isForceHeightEnabled ? enabled : disabled);

        inventory.setItem(30, buildItem(new ItemStack(Material.CHEST), null, 0, null, ChatColor.GOLD + "Backpack",
                ChatColor.GRAY + "Click to toggle team/personal backpacks.", "", ChatColor.GRAY + "Currently set to: " + (isBackpackEnabled() ?
                        ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(39, isBackpackEnabled() ? enabled : disabled);

        inventory.setItem(32, buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Keep Inventory", ChatColor.GRAY + "Click to toggle Keep Inventory in all worlds.", "",
                ChatColor.GRAY + "Currently set to: " +
                        (isKeepInventory ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(41, isKeepInventory ? enabled : disabled);

        inventory.setItem(34, buildItem(new ItemStack(Material.CHAIN), null, 0, null, ChatColor.GOLD + "Chain Mode",
                ChatColor.GRAY + "Click to toggle Chain Mode.", "", ChatColor.GRAY + "Currently set to: " + (ChainManager.isChainModeEnabled() ?
                        ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")));
        inventory.setItem(43, ChainManager.isChainModeEnabled() ? enabled : disabled);

        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
