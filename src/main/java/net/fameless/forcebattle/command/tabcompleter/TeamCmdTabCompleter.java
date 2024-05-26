package net.fameless.forcebattle.command.tabcompleter;

import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamCmdTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("list", "join", "leave", "delete", "create", "invite", "open", "close", "accept", "decline", "kick"), new ArrayList<>());
        } else if (args.length == 2) {
            List<String> teams = new ArrayList<>();
            for (Team team : TeamManager.getTeams()) {
                teams.add(String.valueOf(team.getId()));
            }

            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }

            switch (args[0]) {
                case "join":
                case "leave":
                case "delete": {
                    return StringUtil.copyPartialMatches(args[1], teams, new ArrayList<>());
                }
                case "kick":
                case "invite": {
                    return StringUtil.copyPartialMatches(args[1], players, new ArrayList<>());
                }
            }

        }
        return null;
    }
}
