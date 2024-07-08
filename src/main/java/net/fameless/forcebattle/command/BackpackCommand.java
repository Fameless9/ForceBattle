package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.GUI.MenuUI;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class BackpackCommand implements CommandExecutor {

    public static HashMap<UUID, Inventory> backpackMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "Only players can use backpacks.");
            return false;
        }
        if (!MenuUI.isBackpackEnabled()) {
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Backpacks are disabled.");
            return false;
        }

        if (args.length > 0 && args[0].equals("team")) {
            Team team = TeamManager.getTeam(player);
            if (team == null) {
                player.sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "You are not in a team.");
                return false;
            }
            player.openInventory(team.backpack);
            return true;
        }

        Inventory backpack;

        if (!backpackMap.containsKey(player.getUniqueId())) {
            backpack = Bukkit.createInventory(null, 27, "Backpack");
            backpackMap.put(player.getUniqueId(), backpack);
        } else {
            backpack = backpackMap.get(player.getUniqueId());
        }

        player.openInventory(backpack);
        return false;
    }
}
