package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
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

import java.util.*;

public class ObjectiveManager {

    public static final HashMap<UUID, List<Object>> finishedObjectives = new HashMap<>();
    public static final HashMap<UUID, List<Integer>> objectiveTimeMap = new HashMap<>();
    public static final List<Challenge> activeChallenges = new ArrayList<>();
    private static final HashMap<UUID, Challenge> playerTypeMap = new HashMap<>();
    private static final HashMap<UUID, Material> itemMap = new HashMap<>();
    private static final HashMap<UUID, EntityType> mobMap = new HashMap<>();
    private static final HashMap<UUID, Biome> biomeMap = new HashMap<>();
    private static final HashMap<UUID, Advancement> advancementMap = new HashMap<>();
    private static final HashMap<UUID, Integer> heightMap = new HashMap<>();
    private static final Random random = new Random();

    public static void addChallenge(Challenge challenge) {
        activeChallenges.add(challenge);
    }

    public static void removeChallenge(Challenge challenge) {
        activeChallenges.remove(challenge);
    }

    public static void setChallengeType(Player player, Challenge challengeType) {
        playerTypeMap.put(player.getUniqueId(), challengeType);
    }

    public static Challenge getChallengeType(Player player) {
        if (playerTypeMap.containsKey(player.getUniqueId())) {
            return playerTypeMap.get(player.getUniqueId());
        }
        return null;
    }

    public static void setChallenge(Player player, Challenge challenge, Object objective) {
        setChallengeType(player, challenge);
        switch (challenge) {
            case FORCE_ITEM: {
                if (objective instanceof Material) {
                    itemMap.put(player.getUniqueId(), (Material) objective);
                }
                break;
            }
            case FORCE_MOB: {
                if (objective instanceof EntityType) {
                    mobMap.put(player.getUniqueId(), (EntityType) objective);
                }
                break;
            }
            case FORCE_BIOME: {
                if (objective instanceof Biome) {
                    biomeMap.put(player.getUniqueId(), (Biome) objective);
                }
                break;
            }
            case FORCE_ADVANCEMENT: {
                if (objective instanceof Advancement) {
                    advancementMap.put(player.getUniqueId(), (Advancement) objective);
                }
                break;
            }
            case FORCE_HEIGHT: {
                if (objective instanceof Integer) {
                    heightMap.put(player.getUniqueId(), (int) objective);
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

        return switch (playerChallenge) {
            case FORCE_ITEM -> itemMap.get(player.getUniqueId());
            case FORCE_MOB -> mobMap.get(player.getUniqueId());
            case FORCE_BIOME -> biomeMap.get(player.getUniqueId());
            case FORCE_ADVANCEMENT -> advancementMap.get(player.getUniqueId());
            case FORCE_HEIGHT -> heightMap.get(player.getUniqueId());
        };
    }

    private static void newObjective(Player player, Challenge challenge) {
        if (challenge == null) {
            itemMap.put(player.getUniqueId(), null);
            mobMap.put(player.getUniqueId(), null);
            biomeMap.put(player.getUniqueId(), null);
            advancementMap.put(player.getUniqueId(), null);
            heightMap.put(player.getUniqueId(), null);
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
                    itemMap.put(player.getUniqueId(), newMaterial);
                    ChainManager.itemProgressMap.put(player, ChainManager.itemProgressMap.get(player) + 1);
                    return;
                }
                List<Material> list = ObjectiveLists.getAvailableItems();
                if (list.isEmpty()) {
                    Bukkit.getLogger().info("Item list is empty.");
                    player.sendMessage(ChatColor.RED + "Item list is empty.");
                    return;
                }

                Material newMaterial = list.get(random.nextInt(list.size()));
                itemMap.put(player.getUniqueId(), newMaterial);
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
                    mobMap.put(player.getUniqueId(), newMob);
                    ChainManager.mobProgressMap.put(player, ChainManager.mobProgressMap.get(player) + 1);
                    return;
                }
                List<EntityType> availableMobs = ObjectiveLists.getAvailableMobs();
                if (availableMobs.isEmpty()) {
                    Bukkit.getLogger().info("Mob list is empty.");
                    player.sendMessage(ChatColor.RED + "Mob list is empty.");
                    return;
                }

                EntityType newMob = availableMobs.get(random.nextInt(availableMobs.size()));
                mobMap.put(player.getUniqueId(), newMob);
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
                    biomeMap.put(player.getUniqueId(), newBiome);
                    ChainManager.biomeProgressMap.put(player, ChainManager.biomeProgressMap.get(player) + 1);
                    return;
                }
                List<Biome> availableBiomes = ObjectiveLists.getAvailableBiomes();
                if (availableBiomes.isEmpty()) {
                    Bukkit.getLogger().info("Biome list is empty.");
                    player.sendMessage(ChatColor.RED + "Biome list is empty.");
                    return;
                }

                Biome newBiome = availableBiomes.get(random.nextInt(availableBiomes.size()));
                biomeMap.put(player.getUniqueId(), newBiome);
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
                    advancementMap.put(player.getUniqueId(), newAdvancement);
                    ChainManager.advancementProgressMap.put(player, ChainManager.advancementProgressMap.get(player) + 1);
                    if (Timer.isRunning()) {
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                        player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                        player.sendMessage(ChatColor.GOLD + newAdvancement.getDescription());
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                    }
                    return;
                }
                List<Advancement> availableAdvancements = ObjectiveLists.getAvailableAdvancements();
                if (availableAdvancements.isEmpty()) {
                    Bukkit.getLogger().info("Advancement list is empty.");
                    player.sendMessage(ChatColor.RED + "Advancement list is empty.");
                    return;
                }

                Advancement newAdvancement = availableAdvancements.get(random.nextInt(availableAdvancements.size()));
                advancementMap.put(player.getUniqueId(), newAdvancement);
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
                    heightMap.put(player.getUniqueId(), newHeight);
                    ChainManager.heightProgressMap.put(player, ChainManager.heightProgressMap.get(player) + 1);
                    return;
                }
                List<Integer> availableHeights = ObjectiveLists.getAvailableHeights();
                if (availableHeights.isEmpty()) {
                    Bukkit.getLogger().info("Height list is empty.");
                    player.sendMessage(ChatColor.RED + "Height list is empty.");
                    return;
                }

                int newHeight = availableHeights.get(random.nextInt(availableHeights.size()));
                heightMap.put(player.getUniqueId(), newHeight);
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

    public static void updateObjective(Player player) {
        Challenge challenge = newChallengeType();
        if (Timer.isRunning()) {
            finishedObjectives.get(player.getUniqueId()).add(getChallenge(player));
            objectiveTimeMap.get(player.getUniqueId()).add(Timer.getTime());
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
        if (ForceBattlePlugin.getInstance().getConfig().get("jokers") == null) {
            skipAmount = 3;
        } else {
            skipAmount = ForceBattlePlugin.getInstance().getConfig().getInt("jokers");
        }
        if (skipAmount > 64) {
            skipAmount = 64;
        }

        int swapAmount;
        if (ForceBattlePlugin.getInstance().getConfig().get("swappers") == null) {
            swapAmount = 1;
        } else {
            swapAmount = ForceBattlePlugin.getInstance().getConfig().getInt("swappers");
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
            if (ForceBattlePlugin.getInstance().getConfig().get("jokers") == null) {
                skipAmount = 3;
            } else {
                skipAmount = ForceBattlePlugin.getInstance().getConfig().getInt("jokers");
            }
        }

        if (skipAmount > 64) {
            skipAmount = 64;
        }

        Inventory inventory = player.getInventory();
        inventory.addItem(ItemProvider.getSkipItem(skipAmount));
    }

    public static void giveSwapItem(Player player, Integer amount) {
        int swapAmount;
        if (amount != null) {
            swapAmount = amount;
        } else {
            if (ForceBattlePlugin.getInstance().getConfig().get("swappers") == null) {
                swapAmount = 1;
            } else {
                swapAmount = ForceBattlePlugin.getInstance().getConfig().getInt("swappers");
            }
        }
        if (swapAmount > 64) {
            swapAmount = 64;
        }

        Inventory inventory = player.getInventory();
        inventory.addItem(ItemProvider.getSwapitem(swapAmount));
    }
}