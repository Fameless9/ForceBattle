package net.fameless.core.command;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Backpack extends Command {

    public Backpack() {
        super(
                "backpack",
                List.of("bp"),
                CallerType.PLAYER,
                "/backpack",
                "forcebattle.backpack",
                "Command to open the backpack"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (!PluginConfig.get().getBoolean("settings.enable-backpacks", false)) {
            caller.sendMessage(Caption.of("error.backpacks_disabled"));
            return;
        }
        ((BattlePlayer<?>) caller).openBackpack();
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
