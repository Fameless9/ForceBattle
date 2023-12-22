package net.fameless.forcebattle.command;

import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class BackpackCommand implements CommandExecutor {

    public static HashMap<Player, Inventory> backpackMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use backpacks.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length > 0 && args[0].equals("team")) {
            if (TeamManager.getTeam(player) == null) {
                player.sendMessage(ChatColor.RED + "You are not in a team.");
                return false;
            }
            player.openInventory(TeamManager.getTeam(player).backpack);
            return true;
        }

        Inventory backpack;

        if (!backpackMap.containsKey(player)) {
            backpack = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Backpack");
            backpackMap.put(player, backpack);
        } else {
            backpack = backpackMap.get(player);
        }

        player.openInventory(backpack);
        return false;
    }
}
