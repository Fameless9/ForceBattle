package net.fameless.forcebattle.command.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class JokerCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }

            return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>());
        }
        return null;
    }
}
