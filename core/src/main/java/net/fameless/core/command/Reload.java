package net.fameless.core.command;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Reload extends Command {

    public Reload() {
        super(
                "reloadconfig",
                List.of("reload", "rl"),
                CallerType.NONE,
                "/rl",
                "forcebattle.reload",
                "Reloads the plugin configuration and language files"
        );
    }

    @Override
    protected void executeCommand(final @NotNull CommandCaller caller, final String[] args) {
        PluginConfig.reloadAll();
        caller.sendMessage(Caption.of("command.reload_success"));
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        return List.of();
    }

}
