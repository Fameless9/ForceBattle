package net.fameless.forceBattle.gui;

import net.fameless.forceBattle.game.Team;
import net.fameless.forceBattle.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

public interface ResultGUI<PlatformInventory> {

    @NotNull PlatformInventory getResultGUI(BattlePlayer<?> whoOpens, @NotNull Team team, int page);

    @NotNull PlatformInventory getResultGUI(BattlePlayer<?> whoOpens, @NotNull BattlePlayer<?> target, int page);

    @NotNull PlatformInventory getResultGUI(BattlePlayer<?> whoOpens, int page);

}
