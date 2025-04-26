package net.fameless.core.game;

import net.fameless.core.player.BattlePlayer;

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
