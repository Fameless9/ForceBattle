package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.util.BattleType;
import net.fameless.core.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExcludeObjective extends Command {


    public ExcludeObjective() {
        super(
                "excludeobjective",
                List.of(),
                CallerType.NONE,
                "/excludeobjective <battle-type> <objective>",
                "forcebattle.excludeobjective",
                "Prevent a certain objective from being included in the battle."
        );
    }

    @Override
    protected void executeCommand(final CommandCaller caller, final String @NotNull [] args) {
        if (args.length < 2) {
            sendUsage(caller);
            return;
        }

        BattleType battleType;
        try {
            battleType = BattleType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            caller.sendMessage(Caption.of("command.invalid_battle_type"));
            return;
        }

        String objective = args[1].toLowerCase();

        boolean isExcluded = !ForceBattle.getObjectiveManager().getAvailableObjectives(battleType).contains(objective);

        if (isExcluded) {
            PluginConfig.get().getStringList(battleType.getConfigPath() + ".excluded").add(objective);
        } else {
            PluginConfig.get().getStringList(battleType.getConfigPath() + ".excluded").remove(objective);
        }
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            BattleType.getEnabledBattleTypes().stream()
                    .map(BattleType::name)
                    .forEach(completions::add);
        } else if (args.length == 2) {
            BattleType battleType;
            try {
                battleType = BattleType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of("<Invalid Battle Type>");
            }

            completions.addAll(ForceBattle.getObjectiveManager().getAvailableObjectives(battleType));
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }

}
