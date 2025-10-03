package net.fameless.forcebattle.command;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.fameless.forcebattle.util.TabCompletions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JokerCommand extends Command {

    public JokerCommand() {
        super(
                "joker",
                List.of(),
                CallerType.NONE,
                "/joker <add|remove> <player> <jokertype> <amount>",
                "forcebattle.joker",
                "Command to add or remove jokers from a player"
        );
    }

    @Override
    protected void executeCommand(final CommandCaller caller, final String[] args) {
        if (args.length < 3) {
            sendUsage(caller);
            return;
        }

        Optional<BattlePlayer> targetOptional = BattlePlayer.adapt(args[1]);
        if (targetOptional.isEmpty()) {
            caller.sendMessage(Caption.of("command.no_such_player"));
            return;
        }
        BattlePlayer target = targetOptional.get();

        JokerType jokerType = JokerType.of(args[2]);
        if (jokerType == null) {
            caller.sendMessage(Caption.of("command.invalid_joker_type"));
            return;
        }

        int amount = 1;
        if (args.length > 3) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                caller.sendMessage(Caption.of("command.not_a_number"));
                return;
            }
        }

        switch (args[0]) {
            case "add" -> {
                switch (jokerType) {
                    case SKIP -> target.addSkip(amount);
                    case SWAP -> target.addSwap(amount);
                }
            }
            case "remove" -> {
                switch (jokerType) {
                    case SKIP -> target.removeSkip(amount);
                    case SWAP -> target.removeSwap(amount);
                }
            }
        }
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("add", "remove"), new ArrayList<>());
        }
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        }
        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], List.of(JokerType.SKIP.getIdentifier(), JokerType.SWAP.getIdentifier()), new ArrayList<>());
        }
        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("amount"), new ArrayList<>());
        }
        return List.of();
    }

    private enum JokerType {
        SKIP,
        SWAP;

        private final String IDENTIFIER = name().toLowerCase();

        public String getIdentifier() {
            return IDENTIFIER;
        }

        public static JokerType of(final String identifier) {
            for (JokerType jokerType : values()) {
                if (jokerType.getIdentifier().equals(identifier)) {
                    return jokerType;
                }
            }
            return null;
        }
    }

}
