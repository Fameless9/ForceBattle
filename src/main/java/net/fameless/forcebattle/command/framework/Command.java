package net.fameless.forcebattle.command.framework;

import lombok.Getter;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.BackpackCommand;
import net.fameless.forcebattle.command.ConfigSettingsCommand;
import net.fameless.forcebattle.command.DisplayResultsCommand;
import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.command.HelpCommand;
import net.fameless.forcebattle.command.JokerCommand;
import net.fameless.forcebattle.command.LanguageCommand;
import net.fameless.forcebattle.command.PointsCommand;
import net.fameless.forcebattle.command.RandomTeamsCommand;
import net.fameless.forcebattle.command.RecipeCommand;
import net.fameless.forcebattle.command.ResetCommand;
import net.fameless.forcebattle.command.ResultCommand;
import net.fameless.forcebattle.command.SettingsCommand;
import net.fameless.forcebattle.command.SkipCommand;
import net.fameless.forcebattle.command.TeamCommand;
import net.fameless.forcebattle.command.TimerCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class Command {

    public static final List<Command> COMMANDS = new ArrayList<>();
    public final String id;
    public final List<String> aliases;
    public final CallerType requiredType;
    public final String usage;
    public final String permission;
    public final String description;

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Command.class.getSimpleName());

    public Command(
            String id, List<String> aliases, CallerType requiredType,
            String usage, String permission, final String description
    ) {
        this.id = id;
        this.aliases = aliases;
        this.requiredType = requiredType;
        this.usage = usage;
        this.permission = permission;
        this.description = description;

        COMMANDS.add(this);
    }

    public static @NotNull Optional<Command> getCommandById(String commandId) {
        for (Command command : COMMANDS) {
            if (command.matches(commandId)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    public static void execute(String commandId, CommandCaller caller, String[] args) {
        @NotNull Optional<Command> commandOptional = getCommandById(commandId);
        commandOptional.ifPresentOrElse(
                command -> command.execute(caller, args),
                () -> logger.error("Error while trying to execute command: {}. Command not registered.", commandId)
        );
    }

    public static @NotNull @Unmodifiable List<String> tabComplete(String commandId, CommandCaller caller, String[] args) {
        @NotNull Optional<Command> commandOptional = getCommandById(commandId);
        if (commandOptional.isPresent()) {
            return commandOptional.get().getTabCompletions(caller, args);
        }
        logger.error("Error while trying to get tab-completions for command: {}. Command not registered.", commandId);
        return List.of();
    }

    public static void createInstances() {
        new BackpackCommand();
        new DisplayResultsCommand();
        new ExcludeCommand();
        new HelpCommand();
        new JokerCommand();
        new LanguageCommand();
        new PointsCommand();
        new RandomTeamsCommand();
        new RecipeCommand();
        new ResetCommand();
        new ResultCommand();
        new SettingsCommand();
        new ConfigSettingsCommand();
        new SkipCommand();
        new TeamCommand();
        new TimerCommand();
    }

    public boolean cannotExecute(CommandCaller caller, boolean message) {
        if (caller == null) {
            return false;
        }
        if (!this.requiredType.allows(caller)) {
            if (message) {
                caller.sendMessage(Caption.of(this.requiredType.getErrorMessageKey()));
            }
        } else if (!caller.hasPermission(getPermission())) {
            if (message) {
                caller.sendMessage(Caption.of(
                        "permission.no_permission",
                        TagResolver.resolver("permission", Tag.inserting(Component.text(this.permission)))
                ));
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean matches(String commandId) {
        return this.id.equalsIgnoreCase(commandId) || this.aliases.contains(commandId.toLowerCase());
    }

    public void sendUsage(@NotNull CommandCaller caller) {
        caller.sendMessage(Caption.of(
                "command.usage",
                TagResolver.resolver("usage", Tag.inserting(Component.text(this.usage)))
        ));
    }

    protected void execute(CommandCaller caller, String @NotNull [] args) {
        if (cannotExecute(caller, true)) {
            return;
        }
        executeCommand(caller, args);
    }

    protected List<String> getTabCompletions(CommandCaller caller, String[] args) {
        if (cannotExecute(caller, false)) {
            return List.of();
        }
        return tabComplete(caller, args);
    }

    protected abstract void executeCommand(CommandCaller caller, String[] args);

    protected abstract List<String> tabComplete(CommandCaller caller, String[] args);

}
