package net.fameless.forceBattle.command;

import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
import net.fameless.forceBattle.util.TabCompletions;

import java.util.ArrayList;
import java.util.List;

public class Points extends Command {

    public Points() {
        super(
                "points",
                List.of(),
                CallerType.NONE,
                "/points <add|set|remove> <player> <amount>",
                "forcebattle.points"
        );
    }

    @Override
    protected void executeCommand(final CommandCaller caller, final String[] args) {
        if (args.length < 3) {
            sendUsage(caller);
            return;
        }

        String action = args[0];
        BattlePlayer<?> target = BattlePlayer.of(args[1]).orElse(null);

        if (target == null) {
            caller.sendMessage(Caption.of("command.no_such_player"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            caller.sendMessage(Caption.of("command.not_a_number"));
            return;
        }

        if (amount < 0) {
            caller.sendMessage(Caption.of("command.negative_number"));
            return;
        }

        switch (action) {
            case "add" -> target.setPoints(target.getPoints() + amount);
            case "set" -> target.setPoints(amount);
            case "remove" -> target.setPoints(target.getPoints() - amount);
            default -> sendUsage(caller);
        }
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("add", "set", "remove"), new ArrayList<>());
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        } else if (args.length == 3 && args[2].isEmpty()) {
            return List.of("<amount>");
        }
        return List.of();
    }

}
