package net.fameless.forcebattle.command;

import net.fameless.forcebattle.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipObjectiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("forcebattle.skip")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.skip'");
            return false;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used for players.");
                return false;
            }
            Player player = (Player) sender;
            if (ItemManager.getChallenge(player) == null) {
                player.sendMessage(ChatColor.RED + "No objective to skip.");
                return false;
            }
            ItemManager.updateObjective(player);
            player.sendMessage(ChatColor.GREEN + "Skipped objective.");
            return true;
        }
        for (String name : args) {
            if (Bukkit.getPlayerExact(name) == null) continue;
            Player target = Bukkit.getPlayer(name);
            ItemManager.updateObjective(target);
            target.sendMessage(ChatColor.GREEN + "Your objective was skipped by an operator.");
            sender.sendMessage(ChatColor.GREEN + "Skipped objective of " + target.getName());
        }

        return false;
    }
}
