package net.fameless.forcebattle.command.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BackpackCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("team"), new ArrayList<>());
        }
        return null;
    }
}
