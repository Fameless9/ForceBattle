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

    public static List<Material> itemChainList = new ArrayList<>();
    public static List<EntityType> mobChainList = new ArrayList<>();
    public static List<Biome> biomeChainList = new ArrayList<>();
    public static List<Advancement> advancementChainList = new ArrayList<>();
    public static List<Integer> heightChainList = new ArrayList<>();

    public static final HashMap<Player, Integer> itemProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> mobProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> biomeProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> advancementProgressMap = new HashMap<>();
    public static final HashMap<Player, Integer> heightProgressMap = new HashMap<>();

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
        if (ItemManager.activeChallenges.contains(Challenge.FORCE_ITEM)) {
            List<String> itemNameList = new ArrayList<>(ItemFile.getItemObject().keySet());
            List<Material> itemList = new ArrayList<>();
            for (String a : itemNameList) {
                itemList.add(Material.valueOf(a));
            }
            Collections.shuffle(itemList);
            ChainManager.itemChainList = itemList;
        }
        if (ItemManager.activeChallenges.contains(Challenge.FORCE_MOB)) {
            List<String> mobNameList = new ArrayList<>(ItemFile.getMobObject().keySet());
            List<EntityType> mobList = new ArrayList<>();
            for (String b : mobNameList) {
                mobList.add(EntityType.valueOf(b));
            }
            Collections.shuffle(mobList);
            ChainManager.mobChainList = mobList;
        }
        if (ItemManager.activeChallenges.contains(Challenge.FORCE_BIOME)) {
            List<String> biomeNameList = new ArrayList<>(ItemFile.getBiomeObject().keySet());
            List<Biome> biomeList = new ArrayList<>();
            for (String c : biomeNameList) {
                biomeList.add(Biome.valueOf(c));
            }
            Collections.shuffle(biomeList);
            ChainManager.biomeChainList = biomeList;
        }

        if (ItemManager.activeChallenges.contains(Challenge.FORCE_ADVANCEMENT)) {
            List<String> advancementNameList = new ArrayList<>(ItemFile.getAdvancementObject().keySet());
            List<Advancement> advancementList = new ArrayList<>();
            for (String d : advancementNameList) {
                advancementList.add(Advancement.valueOf(d));
            }
            Collections.shuffle(advancementList);
            ChainManager.advancementChainList = advancementList;
        }
        if (ItemManager.activeChallenges.contains(Challenge.FORCE_HEIGHT)) {
            List<String> heightNameList = new ArrayList<>(ItemFile.getHeightObject().keySet());
            List<Integer> heightList = new ArrayList<>();
            for (String e : heightNameList) {
                heightList.add(Integer.valueOf(e));
            }
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
