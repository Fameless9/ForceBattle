package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChainManager {

    private final ObjectiveManager objectiveManager = ForceBattlePlugin.get().getObjectiveManager();
    private final ObjectiveLists objectiveLists = ForceBattlePlugin.get().getObjectiveLists();
    private final HashMap<Player, Integer> itemProgressMap = new HashMap<>();
    private final HashMap<Player, Integer> mobProgressMap = new HashMap<>();
    private final HashMap<Player, Integer> biomeProgressMap = new HashMap<>();
    private final HashMap<Player, Integer> advancementProgressMap = new HashMap<>();
    private final HashMap<Player, Integer> heightProgressMap = new HashMap<>();
    private List<Material> itemChainList = new ArrayList<>();
    private List<EntityType> mobChainList = new ArrayList<>();
    private List<Biome> biomeChainList = new ArrayList<>();
    private List<Advancement> advancementChainList = new ArrayList<>();
    private List<Integer> heightChainList = new ArrayList<>();
    private boolean chainModeEnabled;

    public ChainManager() {
        updateLists();
    }

    public void addPlayer(Player player) {
        if (!itemProgressMap.containsKey(player)) {
            itemProgressMap.put(player, 0);
        }
        if (!mobProgressMap.containsKey(player)) {
            mobProgressMap.put(player, 0);
        }
        if (!biomeProgressMap.containsKey(player)) {
            biomeProgressMap.put(player, 0);
        }
        if (!advancementProgressMap.containsKey(player)) {
            advancementProgressMap.put(player, 0);
        }
        if (!heightProgressMap.containsKey(player)) {
            heightProgressMap.put(player, 0);
        }
    }

    public void updateLists() {
        if (objectiveManager.getActiveChallenges().contains(Challenge.FORCE_ITEM)) {
            List<Material> itemList = objectiveLists.getAvailableItems();
            Collections.shuffle(itemList);
            this.itemChainList = itemList;
        }
        if (objectiveManager.getActiveChallenges().contains(Challenge.FORCE_MOB)) {
            List<EntityType> mobList = objectiveLists.getAvailableMobs();
            Collections.shuffle(mobList);
            this.mobChainList = mobList;
        }
        if (objectiveManager.getActiveChallenges().contains(Challenge.FORCE_BIOME)) {
            List<Biome> biomeList = objectiveLists.getAvailableBiomes();
            Collections.shuffle(biomeList);
            this.biomeChainList = biomeList;
        }

        if (objectiveManager.getActiveChallenges().contains(Challenge.FORCE_ADVANCEMENT)) {
            List<Advancement> advancementList = objectiveLists.getAvailableAdvancements();
            Collections.shuffle(advancementList);
            this.advancementChainList = advancementList;
        }
        if (objectiveManager.getActiveChallenges().contains(Challenge.FORCE_HEIGHT)) {
            List<Integer> heightList = objectiveLists.getAvailableHeights();
            Collections.shuffle(heightList);
            this.heightChainList = heightList;
        }
    }

    public boolean isChainModeEnabled() {
        return chainModeEnabled;
    }

    public void setChainModeEnabled(boolean chainModeEnabled) {
        this.chainModeEnabled = chainModeEnabled;
    }

    public HashMap<Player, Integer> getItemProgressMap() {
        return itemProgressMap;
    }

    public HashMap<Player, Integer> getMobProgressMap() {
        return mobProgressMap;
    }

    public HashMap<Player, Integer> getBiomeProgressMap() {
        return biomeProgressMap;
    }

    public HashMap<Player, Integer> getAdvancementProgressMap() {
        return advancementProgressMap;
    }

    public HashMap<Player, Integer> getHeightProgressMap() {
        return heightProgressMap;
    }

    public List<Material> getItemChainList() {
        return itemChainList;
    }

    public List<EntityType> getMobChainList() {
        return mobChainList;
    }

    public List<Biome> getBiomeChainList() {
        return biomeChainList;
    }

    public List<Advancement> getAdvancementChainList() {
        return advancementChainList;
    }

    public List<Integer> getHeightChainList() {
        return heightChainList;
    }
}
