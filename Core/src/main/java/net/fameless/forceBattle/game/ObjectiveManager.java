package net.fameless.forceBattle.game;

import net.fameless.forceBattle.player.BattlePlayer;

import java.util.List;

public interface ObjectiveManager {

    Objective getNewObjective(BattlePlayer<?> battlePlayer);

    List<?> getAvailableItems();

    List<?> getAvailableMobs();

    List<?> getAvailableBiomes();

    List<?> getAvailableAdvancements();

    List<?> getAvailableHeights();

    List<String> getChainList();

    void updateChainList();

}
