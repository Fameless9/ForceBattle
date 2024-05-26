package net.fameless.forcebattle.command;

import net.fameless.forcebattle.manager.ObjectiveManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkipObjectiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!sender.hasPermission("forcebattle.skip")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.skip'");
            return false;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used on players.");
                return false;
            }
            if (ObjectiveManager.getChallenge(player) == null) {
                player.sendMessage(ChatColor.RED + "No objective to skip.");
                return false;
            }
            ObjectiveManager.updateObjective(player);
            player.sendMessage(ChatColor.GREEN + "Skipped objective.");
            return true;
        }
        for (String name : args) {
            if (Bukkit.getPlayerExact(name) == null) continue;
            Player target = Bukkit.getPlayer(name);
            if (target != null) {
                ObjectiveManager.updateObjective(target);
                target.sendMessage(ChatColor.GREEN + "Your objective was skipped by an operator.");
                sender.sendMessage(ChatColor.GREEN + "Skipped objective of " + target.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "Player couldn't be found.");
            }
        }

        return false;
    }
}
