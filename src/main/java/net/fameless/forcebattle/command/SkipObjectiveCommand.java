package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
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
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Lacking permission: 'forcebattle.skip'");
            return false;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "This command can only be used on players.");
                return false;
            }
            if (ObjectiveManager.getObjective(player) == null) {
                player.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "No objective to skip.");
                return false;
            }
            ObjectiveManager.updateObjective(player);
            player.sendMessage(ForceBattlePlugin.prefix + ChatColor.GREEN + "Skipped your objective.");
            return true;
        }
        for (String name : args) {
            Player target = Bukkit.getPlayer(name);
            if (target == null) {
                sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Player couldn't be found.");
                continue;
            }
            ObjectiveManager.updateObjective(target);
            target.sendMessage(ForceBattlePlugin.prefix + ChatColor.GREEN + "Your objective was skipped by an operator.");
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.GREEN + "Skipped objective of " + target.getName());
        }

        return false;
    }
}
