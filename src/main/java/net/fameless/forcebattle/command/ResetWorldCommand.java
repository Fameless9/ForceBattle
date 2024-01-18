package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only operators may use this command.");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("is_reset")) {
            ForceBattlePlugin.getInstance().getConfig().set("is_reset", false);
            ForceBattlePlugin.getInstance().saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Your worlds won't be reset on the next restart.");
        } else {
            ForceBattlePlugin.getInstance().getConfig().set("is_reset", true);
            ForceBattlePlugin.getInstance().saveConfig();
            sender.sendMessage(ChatColor.RED + "Restart the server to reset the worlds. /resetworld to cancel.");
        }

        return false;
    }
}