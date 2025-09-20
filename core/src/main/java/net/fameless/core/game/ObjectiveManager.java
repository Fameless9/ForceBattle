package net.fameless.core.game;

import net.fameless.core.config.PluginConfig;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ObjectiveManager {

    protected final List<String> chainList = new ArrayList<>();

    public void updateChainList() {
        chainList.clear();
        if (PluginConfig.get().getBoolean("modes.force-item.enabled", true)) {
            chainList.addAll(getAvailableItems());
        }
        if (PluginConfig.get().getBoolean("modes.force-mob.enabled", false)) {
            chainList.addAll(getAvailableMobs());
        }
        if (PluginConfig.get().getBoolean("modes.force-biome.enabled", false)) {
            chainList.addAll(getAvailableBiomes());
        }
        if (PluginConfig.get().getBoolean("modes.force-advancement.enabled", false)) {
            chainList.addAll(getAvailableAdvancements());
        }
        if (PluginConfig.get().getBoolean("modes.force-height.enabled", false)) {
            chainList.addAll(getAvailableHeights());
        }
        Collections.shuffle(chainList);
    }

    public abstract Objective getNewObjective(BattlePlayer<?> battlePlayer);

    public abstract List<String> getAvailableObjectives(BattleType battleType);

    public abstract List<String> getAvailableItems();

    public abstract List<String> getAvailableMobs();

    public abstract List<String> getAvailableBiomes();

    public abstract List<String> getAvailableAdvancements();

    public abstract List<String> getAvailableHeights();

    public abstract List<String> getChainList();

}
