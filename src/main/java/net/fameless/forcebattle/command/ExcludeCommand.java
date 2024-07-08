package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExcludeCommand implements CommandExecutor {

    public static List<Player> excludedPlayers = new ArrayList<>();

    public static void setExcluded(Player player, boolean excluded) {
        if (excluded) {
            excludedPlayers.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
            player.sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "You are now excluded.");
            return;
        }
        excludedPlayers.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
        player.sendMessage(ForceBattlePlugin.prefix + ChatColor.GRAY + "You are not excluded anymore.");
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!sender.hasPermission("forcebattle.exclude")) {
            sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Lacking permission: 'forcebattle.exclude'");
            return false;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "This command can only be used for players.");
                return false;
            }
            if (excludedPlayers.contains(player)) {
                setExcluded(player, false);
                return true;
            }
            setExcluded(player, true);
            return true;
        }
        for (String name : args) {
            if (Bukkit.getPlayerExact(name) == null) continue;
            Player player = Bukkit.getPlayer(name);
            if (excludedPlayers.contains(player)) {
                setExcluded(player, false);
                continue;
            }
            setExcluded(player, true);
        }

        return false;
    }
}