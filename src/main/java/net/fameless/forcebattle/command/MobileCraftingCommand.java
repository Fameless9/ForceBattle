package net.fameless.forcebattle.command;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MobileCraftingCommand extends Command {

    public MobileCraftingCommand() {
        super(
                "craft",
                List.of("crafting"),
                CallerType.PLAYER,
                "/craft",
                "forcebattle.craft",
                "Command to open the crafting table GUI"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.MOBILE_CRAFTING)) {
            caller.sendMessage(Caption.of("error.mobile_crafting_disabled"));
            return;
        }
        if (caller instanceof BattlePlayer battlePlayer) {
            Player player = battlePlayer.getPlayer();
            player.openWorkbench(null, true);
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }
}
