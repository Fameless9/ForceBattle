package net.fameless.forcebattle.manager;

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

    public static final HashMap<Player, Integer> itemProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> mobProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> biomeProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> advancementProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> heightProgressMap = new HashMap<>();
    public static List<Material> itemChainList = new ArrayList<>();
    public static List<EntityType> mobChainList = new ArrayList<>();
    public static List<Biome> biomeChainList = new ArrayList<>();
    public static List<Advancement> advancementChainList = new ArrayList<>();
    public static List<Integer> heightChainList = new ArrayList<>();
    private static boolean enabled;

    public static void addPlayer(Player player) {
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

    public static void updateLists() {
        if (ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ITEM)) {
            List<Material> itemList = ObjectiveLists.getAvailableItems();
            Collections.shuffle(itemList);
            ChainManager.itemChainList = itemList;
        }
        if (ObjectiveManager.activeChallenges.contains(Challenge.FORCE_MOB)) {
            List<EntityType> mobList = ObjectiveLists.getAvailableMobs();
            Collections.shuffle(mobList);
            ChainManager.mobChainList = mobList;
        }
        if (ObjectiveManager.activeChallenges.contains(Challenge.FORCE_BIOME)) {
            List<Biome> biomeList = ObjectiveLists.getAvailableBiomes();
            Collections.shuffle(biomeList);
            ChainManager.biomeChainList = biomeList;
        }

        if (ObjectiveManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT)) {
            List<Advancement> advancementList = ObjectiveLists.getAvailableAdvancements();
            Collections.shuffle(advancementList);
            ChainManager.advancementChainList = advancementList;
        }
        if (ObjectiveManager.activeChallenges.contains(Challenge.FORCE_HEIGHT)) {
            List<Integer> heightList = ObjectiveLists.getAvailableHeights();
            Collections.shuffle(heightList);
            ChainManager.heightChainList = heightList;
        }
    }

    public static boolean isChainModeEnabled() {
        return enabled;
    }

    public static void setChainModeEnabled(boolean enabled) {
        ChainManager.enabled = enabled;
    }
}
