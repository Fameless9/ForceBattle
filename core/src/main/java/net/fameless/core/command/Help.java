package net.fameless.core.command;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class Help extends Command {

    public Help() {
        super(
                "help",
                List.of(),
                CallerType.NONE,
                "/help",
                "forcebattle.help",
                "Display commands/information about ForceBattle"
        );
    }

    @Override
    protected void executeCommand(final @NotNull CommandCaller caller, final String[] args) {
        String commandsList = Command.COMMANDS.stream()
                .map(cmd -> String.format("<gold>/%s <gray>- %s", cmd.getId(), cmd.getDescription()))
                .collect(Collectors.joining("\n"));

        caller.sendMessage(Caption.of(
                "command.help_message",
                TagResolver.resolver("command-list", Tag.inserting(MiniMessage.miniMessage().deserialize(commandsList)))
        ));
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        return List.of();
    }

}
