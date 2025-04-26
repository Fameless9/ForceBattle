package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.player.BattlePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DisplayResults extends Command {

    public DisplayResults() {
        super(
                "displayresults",
                List.of(),
                CallerType.NONE,
                "/displayresults",
                "forcebattle.displayresults",
                "Command to display the results of the battle"
        );
    }

    @Override
    protected void executeCommand(CommandCaller caller, String[] args) {
        if (!ForceBattle.getTimer().hasHitZero()) {
            caller.sendMessage(Caption.of("command.not_hit_zero"));
            return;
        }
        final HashMap<BattlePlayer<?>, Integer> POINTS_MAP = new HashMap<>();
        for (BattlePlayer<?> battlePlayer : BattlePlayer.BATTLE_PLAYERS) {
            POINTS_MAP.put(battlePlayer, battlePlayer.getPoints());
        }

        LinkedHashMap<BattlePlayer<?>, Integer> sortedPointsMap = POINTS_MAP.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        HashMap.Entry::getKey,
                        HashMap.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long delay = 750;
        final long[] counter = {0};

        sortedPointsMap.forEach((battlePlayer, points) -> {
            long place = sortedPointsMap.size() - counter[0];
            scheduler.schedule(
                    () -> ForceBattle.platform().broadcast(
                            MiniMessage.miniMessage().deserialize("<gold>" + place + ". <blue>" + battlePlayer.getName() + "<dark_gray>: <gold>" + points)
                    ), counter[0] * delay, TimeUnit.MILLISECONDS
            );
            counter[0]++;
        });

        scheduler.shutdown();
    }

    @Override
    protected List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
