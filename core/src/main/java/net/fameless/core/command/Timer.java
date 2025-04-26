package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Timer extends Command {

    public Timer() {
        super(
                "timer",
                List.of(),
                CallerType.NONE,
                "/timer <toggle|set|duration> <time>",
                "forcebattle.timer",
                "Command to control the timer: toggle, set, duration"
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String[] args) {
        if (args.length < 1) {
            sendUsage(caller);
            return;
        }
        net.fameless.core.game.Timer timer = ForceBattle.getTimer();
        switch (args[0]) {
            case "toggle" -> {
                if (timer.isRunning()) {
                    timer.pause();
                } else {
                    timer.start();
                }
                return;
            }
            case "set" -> {
                if (args.length < 2) {
                    sendUsage(caller);
                    return;
                }
                Integer newTime = parseInt(caller, args[1]);
                if (newTime == null) {
                    return;
                }
                timer.setTime(newTime);
                return;
            }
            case "duration" -> {
                if (args.length < 2) {
                    sendUsage(caller);
                    return;
                }
                Integer newStartTime = parseInt(caller, args[1]);
                if (newStartTime == null) {
                    return;
                }
                timer.setStartTime(newStartTime);
                return;
            }
        }
        sendUsage(caller);
    }

    private @Nullable Integer parseInt(CommandCaller caller, String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            caller.sendMessage(Caption.of("command.not_a_number"));
            return null;
        }
        return number;
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("toggle", "set", "duration"), new ArrayList<>());
        }
        return List.of();
    }

}
