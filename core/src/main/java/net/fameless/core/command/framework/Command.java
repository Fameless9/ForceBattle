package net.fameless.core.command.framework;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.Backpack;
import net.fameless.core.command.DisplayResults;
import net.fameless.core.command.Exclude;
import net.fameless.core.command.Help;
import net.fameless.core.command.Joker;
import net.fameless.core.command.Language;
import net.fameless.core.command.Points;
import net.fameless.core.command.Reset;
import net.fameless.core.command.Result;
import net.fameless.core.command.Settings;
import net.fameless.core.command.Skip;
import net.fameless.core.command.Team;
import net.fameless.core.command.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Command {

    public static final List<Command> COMMANDS = new ArrayList<>();
    public final String id;
    public final List<String> aliases;
    public final CallerType requiredType;
    public final String usage;
    public final String permission;
    public final String description;

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
                () -> ForceBattle.platform().getLogger().severe("Error while trying to execute command: " + commandId + ". Command not registered.")
        );
    }

    public static @NotNull @Unmodifiable List<String> tabComplete(String commandId, CommandCaller caller, String[] args) {
        @NotNull Optional<Command> commandOptional = getCommandById(commandId);
        if (commandOptional.isPresent()) {
            return commandOptional.get().getTabCompletions(caller, args);
        }
        ForceBattle.platform().getLogger().severe("Error while trying to get tab-completions for command: " + commandId + ". Command not registered.");
        return List.of();
    }

    public static void createInstances() {
        new Backpack();
        new DisplayResults();
        new Exclude();
        new Help();
        new Joker();
        new Language();
        new Points();
        new Reset();
        new Result();
        new Settings();
        new Skip();
        new Team();
        new Timer();
    }

    public String getPermission() {
        return permission;
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

    public String getId() {
        return id;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public CallerType getRequiredType() {
        return requiredType;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    protected abstract void executeCommand(CommandCaller caller, String[] args);

    protected abstract List<String> tabComplete(CommandCaller caller, String[] args);

}
