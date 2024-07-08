package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Only operators may use this command.");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Only players may use this command.");
            return false;
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("is_reset")) {
            ForceBattlePlugin.getInstance().getConfig().set("is_reset", false);
            ForceBattlePlugin.getInstance().saveConfig();
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.GREEN + "Your worlds won't be reset on the next restart.");
        } else {
            ForceBattlePlugin.getInstance().getConfig().set("is_reset", true);
            ForceBattlePlugin.getInstance().saveConfig();
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Restart the server to reset the worlds. /resetworld to cancel.");
        }

        return false;
    }
}