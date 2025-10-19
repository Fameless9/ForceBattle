package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.Timer;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.StringUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimerCommand extends Command {

    public TimerCommand() {
        super(
                "timer",
                List.of(),
                CallerType.NONE,
                "/timer <toggle|set|duration> <time in s>",
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
        Timer timer = ForceBattle.getTimer();
        switch (args[0]) {
            case "toggle" -> {
                if (timer.isRunning()) {
                    timer.pause();
                    caller.sendMessage(Caption.of("command.timer_paused"));
                } else {
                    if (SettingsManager.getActiveChallenges().isEmpty()) {
                        caller.sendMessage(Caption.of("error.no_battletype_selected"));
                        return;
                    }
                    timer.start();
                    caller.sendMessage(Caption.of("command.timer_started"));
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
                caller.sendMessage(Caption.of("command.timer_set", TagResolver.resolver("time", Tag.inserting(Component.text(StringUtility.formatTime(newTime))))));
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
            return StringUtility.copyPartialMatches(args[0], List.of("toggle", "set", "duration"), new ArrayList<>());
        }
        return List.of();
    }
}
