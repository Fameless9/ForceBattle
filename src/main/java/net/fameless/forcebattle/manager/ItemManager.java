package net.fameless.forcebattle.manager;

import com.google.gson.JsonObject;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ItemManager {

    private static final HashMap<Player, Challenge> playerTypeMap = new HashMap<>();
    private static final HashMap<Player, Material> itemMap = new HashMap<>();
    private static final HashMap<Player, EntityType> mobMap = new HashMap<>();
    private static final HashMap<Player, Biome> biomeMap = new HashMap<>();
    private static final HashMap<Player, Advancement> advancementMap = new HashMap<>();
    private static final HashMap<Player, Integer> heightMap = new HashMap<>();
    public static final HashMap<Player, List<Object>> finishedObjectives = new HashMap<>();
    public static final HashMap<Player, List<Integer>> objectiveTimeMap = new HashMap<>();
    public static final List<Challenge> activeChallenges = new ArrayList<>();
    private static final Random random = new Random();

    public static void addChallenge(Challenge challenge) {
        activeChallenges.add(challenge);
    }

    public static void removeChallenge(Challenge challenge) {
        activeChallenges.remove(challenge);
    }

    public static void setChallengeType(Player player, Challenge challengeType) {
        playerTypeMap.put(player, challengeType);
    }

    public static Challenge getChallengeType(Player player) {
        if (playerTypeMap.containsKey(player)) {
            return playerTypeMap.get(player);
        }
        return null;
    }

    public static void setChallenge(Player player, Challenge challenge, Object objective) {
        setChallengeType(player, challenge);
        switch (challenge) {
            case FORCE_ITEM: {
                if (objective instanceof Material) {
                    itemMap.put(player, (Material) objective);
                }
                break;
            }
            case FORCE_MOB: {
                if (objective instanceof EntityType) {
                    mobMap.put(player, (EntityType) objective);
                }
                break;
            }
            case FORCE_BIOME: {
                if (objective instanceof Biome) {
                    biomeMap.put(player, (Biome) objective);
                }
                break;
            }
            case FORCE_ADVANCEMENT: {
                if (objective instanceof Advancement) {
                    advancementMap.put(player, (Advancement) objective);
                }
                break;
            }
            case FORCE_HEIGHT: {
                if (objective instanceof Integer) {
                    heightMap.put(player, (int) objective);
                }
                break;
            }
        }
    }

    public static Object getChallenge(Player player) {
        Challenge playerChallenge = getChallengeType(player);
        if (playerChallenge == null) {
            return null;
        }

        switch (playerChallenge) {
            case FORCE_ITEM: {
                return itemMap.get(player);
            }
            case FORCE_MOB: {
                return mobMap.get(player);
            }
            case FORCE_BIOME: {
                return biomeMap.get(player);
            }
            case FORCE_ADVANCEMENT: {
                return advancementMap.get(player);
            }
            case FORCE_HEIGHT: {
                return heightMap.get(player);
            }
        }
        return null;
    }

    private static void newObjective(Player player, Challenge challenge) {
        if (challenge == null) {
            itemMap.put(player, null);
            mobMap.put(player, null);
            biomeMap.put(player, null);
            advancementMap.put(player, null);
            heightMap.put(player, null);
            return;
        }
        switch (challenge) {
            case FORCE_ITEM: {
                if (ChainManager.isChainModeEnabled()) {
                    Material newMaterial;
                    try {
                        newMaterial = ChainManager.itemChainList.get(ChainManager.itemProgressMap.get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Item Chain list. Starting over.");
                        ChainManager.itemProgressMap.put(player, 0);
                        newMaterial = ChainManager.itemChainList.get(ChainManager.itemProgressMap.get(player));
                    }
                    itemMap.put(player, newMaterial);
                    ChainManager.itemProgressMap.put(player, ChainManager.itemProgressMap.get(player) + 1);
                    return;
                }
                List<String> list = toList(ItemFile.getItemObject());
                if (list.isEmpty()) {
                    Bukkit.getLogger().info("Item list is empty.");
                    player.sendMessage(ChatColor.RED + "Item list is empty.");
                    return;
                }

                Material newMaterial = Material.valueOf(list.get(random.nextInt(list.size())));
                itemMap.put(player, newMaterial);
                break;
            }
            case FORCE_MOB: {
                if (ChainManager.isChainModeEnabled()) {
                    EntityType newMob;
                    try {
                        newMob = ChainManager.mobChainList.get(ChainManager.mobProgressMap.get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Mob Chain list. Starting over.");
                        ChainManager.mobProgressMap.put(player, 0);
                        newMob = ChainManager.mobChainList.get(ChainManager.mobProgressMap.get(player));
                    }
                    mobMap.put(player, newMob);
                    ChainManager.mobProgressMap.put(player, ChainManager.mobProgressMap.get(player) + 1);
                    return;
                }
                List<String> availableMobs = toList(ItemFile.getMobObject());
                if (availableMobs.isEmpty()) {
                    Bukkit.getLogger().info("Mob list is empty.");
                    player.sendMessage(ChatColor.RED + "Mob list is empty.");
                    return;
                }

                EntityType newMob = EntityType.valueOf(availableMobs.get(random.nextInt(availableMobs.size())));
                mobMap.put(player, newMob);
                break;
            }
            case FORCE_BIOME: {
                if (ChainManager.isChainModeEnabled()) {
                    Biome newBiome;
                    try {
                        newBiome = ChainManager.biomeChainList.get(ChainManager.biomeProgressMap.get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Biome Chain list. Starting over.");
                        ChainManager.biomeProgressMap.put(player, 0);
                        newBiome = ChainManager.biomeChainList.get(ChainManager.biomeProgressMap.get(player));
                    }
                    biomeMap.put(player, newBiome);
                    ChainManager.biomeProgressMap.put(player, ChainManager.biomeProgressMap.get(player) + 1);
                    return;
                }
                List<String> availableBiomes = toList(ItemFile.getBiomeObject());
                if (availableBiomes.isEmpty()) {
                    Bukkit.getLogger().info("Biome list is empty.");
                    player.sendMessage(ChatColor.RED + "Biome list is empty.");
                    return;
                }

                Biome newBiome = Biome.valueOf(availableBiomes.get(random.nextInt(availableBiomes.size())));
                biomeMap.put(player, newBiome);
                break;
            }
            case FORCE_ADVANCEMENT: {
                if (ChainManager.isChainModeEnabled()) {
                    Advancement newAdvancement;
                    try {
                        newAdvancement = ChainManager.advancementChainList.get(ChainManager.advancementProgressMap.get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Advancement Chain list. Starting over.");
                        ChainManager.advancementProgressMap.put(player, 0);
                        newAdvancement = ChainManager.advancementChainList.get(ChainManager.advancementProgressMap.get(player));
                    }
                    advancementMap.put(player, newAdvancement);
                    ChainManager.advancementProgressMap.put(player, ChainManager.advancementProgressMap.get(player) + 1);
                    if (Timer.isRunning()) {
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                        player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                        player.sendMessage(ChatColor.GOLD + newAdvancement.getDescription());
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                    }
                    return;
                }
                List<String> availableAdvancements = toList(ItemFile.getAdvancementObject());
                if (availableAdvancements.isEmpty()) {
                    Bukkit.getLogger().info("Advancement list is empty.");
                    player.sendMessage(ChatColor.RED + "Advancement list is empty.");
                    return;
                }

                Advancement newAdvancement = Advancement.valueOf(availableAdvancements.get(random.nextInt(availableAdvancements.size())));
                advancementMap.put(player, newAdvancement);
                if (Timer.isRunning()) {
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                    player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                    player.sendMessage(ChatColor.GOLD + newAdvancement.getDescription());
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                }
                break;
            }
            case FORCE_HEIGHT: {
                if (ChainManager.isChainModeEnabled()) {
                    int newHeight;
                    try {
                        newHeight = ChainManager.heightChainList.get(ChainManager.heightProgressMap.get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Height Chain list. Starting over.");
                        ChainManager.heightProgressMap.put(player, 0);
                        newHeight = ChainManager.heightChainList.get(ChainManager.heightProgressMap.get(player));
                    }
                    heightMap.put(player, newHeight);
                    ChainManager.heightProgressMap.put(player, ChainManager.heightProgressMap.get(player) + 1);
                    return;
                }
                List<String> availableHeights = toList(ItemFile.getHeightObject());
                if (availableHeights.isEmpty()) {
                    Bukkit.getLogger().info("Height list is empty.");
                    player.sendMessage(ChatColor.RED + "Height list is empty.");
                    return;
                }

                int newHeight = Integer.parseInt(availableHeights.get(random.nextInt(availableHeights.size())));
                heightMap.put(player, newHeight);
                break;
            }
        }
    }

    private static Challenge newChallengeType() {
        if (activeChallenges.isEmpty()) {
            return null;
        }
        return activeChallenges.get(random.nextInt(activeChallenges.size()));
    }

    private static List<String> toList(JsonObject jsonObject) {
        return new ArrayList<>(jsonObject.keySet());
    }

    public static void updateObjective(Player player) {
        Challenge challenge = newChallengeType();
        if (Timer.isRunning()) {
            finishedObjectives.get(player).add(getChallenge(player));
            objectiveTimeMap.get(player).add(Timer.getTime());
        }
        setChallengeType(player, challenge);
        newObjective(player, challenge);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    public static void resetProgress(Player player) {
        PointsManager.setPoints(player, 0);
    }

    public static void giveJokers(Player player) {
        int skipAmount;
        if (ForceBattle.getInstance().getConfig().get("jokers") == null) {
            skipAmount = 3;
        } else {
            skipAmount = ForceBattle.getInstance().getConfig().getInt("jokers");
        }
        if (skipAmount > 64) {
            skipAmount = 64;
        }

        int swapAmount;
        if (ForceBattle.getInstance().getConfig().get("swappers") == null) {
            swapAmount = 1;
        } else {
            swapAmount = ForceBattle.getInstance().getConfig().getInt("swappers");
        }
        if (swapAmount > 64) {
            swapAmount = 64;
        }

        player.getInventory().setItem(7, ItemProvider.getSwapitem(swapAmount));
        player.getInventory().setItem(8, ItemProvider.getSkipItem(skipAmount));
    }

    public static void giveSkipItem(Player player, Integer amount) {

        int skipAmount;
        if (amount != null) {
            skipAmount = amount;
        } else {
            if (ForceBattle.getInstance().getConfig().get("jokers") == null) {
                skipAmount = 3;
            } else {
                skipAmount = ForceBattle.getInstance().getConfig().getInt("jokers");
            }
        }

        if (skipAmount > 64) {
            skipAmount = 64;
        }

        Inventory inventory = player.getInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack.getType().equals(ItemProvider.getSkipItem(1).getType())) {
                itemStack.setAmount(skipAmount);
                return;
            }
        }

        inventory.setItem(8, ItemProvider.getSkipItem(skipAmount));
    }

    public static void giveSwapItem(Player player, Integer amount) {
        int swapAmount;
        if (amount != null) {
            swapAmount = amount;
        } else {
            if (ForceBattle.getInstance().getConfig().get("swappers") == null) {
                swapAmount = 1;
            } else {
                swapAmount = ForceBattle.getInstance().getConfig().getInt("swappers");
            }
        }
        if (swapAmount > 64) {
            swapAmount = 64;
        }

        Inventory inventory = player.getInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack.getType().equals(ItemProvider.getSwapitem(1).getType())) {
                itemStack.setAmount(swapAmount);
                return;
            }
        }

        player.getInventory().setItem(7, ItemProvider.getSwapitem(swapAmount));
    }
}