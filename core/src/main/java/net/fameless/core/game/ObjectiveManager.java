package net.fameless.core.game;

import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.Coords;

import java.util.List;

public interface ObjectiveManager {

    Objective getNewObjective(BattlePlayer<?> battlePlayer);

    List<?> getAvailableItems();

    List<?> getAvailableMobs();

    List<?> getAvailableBiomes();

    List<?> getAvailableAdvancements();

    List<?> getAvailableHeights();

    List<?> getAvailableStructures();

    Coords getRandomLocation(BattlePlayer<?> battlePlayer);

    List<String> getChainList();

    void updateChainList();

}
