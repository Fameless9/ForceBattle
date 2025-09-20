package net.fameless.core.command;

import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.util.StringUtil;
import net.fameless.core.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Result extends Command {

    public Result() {
        super(
                "result",
                List.of(),
                CallerType.PLAYER,
                "/result <team|player> <team id|player>",
                "forcebattle.result",
                "Command to display the results of a battle"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        // To be handled by Bukkit ResultGUI
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("player", "team"), new ArrayList<>());
        } else if (args.length == 2) {
            return switch (args[0]) {
                case "player" -> StringUtil.copyPartialMatches(args[1], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
                case "team" -> StringUtil.copyPartialMatches(args[1], TabCompletions.getTeamIdTabCompletions(), new ArrayList<>());
                default -> List.of();
            };
        }
        return List.of();
    }

}
