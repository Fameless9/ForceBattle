package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
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

    private final ForceBattlePlugin forceBattlePlugin;
    private final ObjectiveLists objectiveLists;
    private ChainManager chainManager;
    private final HashMap<UUID, List<Object>> finishedObjectives = new HashMap<>();
    private final HashMap<UUID, List<Integer>> objectiveTimeMap = new HashMap<>();
    private final HashMap<UUID, Challenge> playerTypeMap = new HashMap<>();
    private final HashMap<UUID, Material> itemMap = new HashMap<>();
    private final HashMap<UUID, EntityType> mobMap = new HashMap<>();
    private final HashMap<UUID, Biome> biomeMap = new HashMap<>();
    private final HashMap<UUID, Advancement> advancementMap = new HashMap<>();
    private final HashMap<UUID, Integer> heightMap = new HashMap<>();
    private final List<Challenge> activeChallenges = new ArrayList<>();
    private final Random random = new Random();

    public ObjectiveManager(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
        this.objectiveLists = forceBattlePlugin.getObjectiveLists();
    }

    public void addChallenge(Challenge challenge) {
        activeChallenges.add(challenge);
    }

    public void removeChallenge(Challenge challenge) {
        activeChallenges.remove(challenge);
    }

    public void setChallengeType(Player player, Challenge challengeType) {
        playerTypeMap.put(player.getUniqueId(), challengeType);
    }

    public Challenge getChallengeType(Player player) {
        if (playerTypeMap.containsKey(player.getUniqueId())) {
            return playerTypeMap.get(player.getUniqueId());
        }
        return null;
    }

    public void setChallenge(Player player, Challenge challenge, Object objective) {
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

    public Object getObjective(Player player) {
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

    private void newObjective(Player player, Challenge challenge) {
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
                if (chainManager.isChainModeEnabled()) {
                    Material newMaterial;
                    try {
                        newMaterial = chainManager.getItemChainList().get(chainManager.getItemProgressMap().get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Item Chain list. Starting over.");
                        chainManager.getItemProgressMap().put(player, 0);
                        newMaterial = chainManager.getItemChainList().get(chainManager.getItemProgressMap().get(player));
                    }
                    itemMap.put(player.getUniqueId(), newMaterial);
                    chainManager.getItemProgressMap().put(player, chainManager.getItemProgressMap().get(player) + 1);
                    return;
                }
                List<Material> list = objectiveLists.getAvailableItems();
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
                if (chainManager.isChainModeEnabled()) {
                    EntityType newMob;
                    try {
                        newMob = chainManager.getMobChainList().get(chainManager.getMobProgressMap().get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Mob Chain list. Starting over.");
                        chainManager.getMobProgressMap().put(player, 0);
                        newMob = chainManager.getMobChainList().get(chainManager.getMobProgressMap().get(player));
                    }
                    mobMap.put(player.getUniqueId(), newMob);
                    chainManager.getMobProgressMap().put(player, chainManager.getMobProgressMap().get(player) + 1);
                    return;
                }
                List<EntityType> availableMobs = objectiveLists.getAvailableMobs();
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
                if (chainManager.isChainModeEnabled()) {
                    Biome newBiome;
                    try {
                        newBiome = chainManager.getBiomeChainList().get(chainManager.getBiomeProgressMap().get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Biome Chain list. Starting over.");
                        chainManager.getBiomeProgressMap().put(player, 0);
                        newBiome = chainManager.getBiomeChainList().get(chainManager.getBiomeProgressMap().get(player));
                    }
                    biomeMap.put(player.getUniqueId(), newBiome);
                    chainManager.getBiomeProgressMap().put(player, chainManager.getBiomeProgressMap().get(player) + 1);
                    return;
                }
                List<Biome> availableBiomes = objectiveLists.getAvailableBiomes();
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
                if (chainManager.isChainModeEnabled()) {
                    Advancement newAdvancement;
                    try {
                        newAdvancement = chainManager.getAdvancementChainList().get(chainManager.getAdvancementProgressMap().get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Advancement Chain list. Starting over.");
                        chainManager.getAdvancementProgressMap().put(player, 0);
                        newAdvancement = chainManager.getAdvancementChainList().get(chainManager.getAdvancementProgressMap().get(player));
                    }
                    advancementMap.put(player.getUniqueId(), newAdvancement);
                    chainManager.getAdvancementProgressMap().put(player, chainManager.getAdvancementProgressMap().get(player) + 1);
                    if (forceBattlePlugin.getTimer().isRunning()) {
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                        player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                        player.sendMessage(ChatColor.GOLD + newAdvancement.getDescription());
                        player.sendMessage(ChatColor.GRAY + "-----------------------");
                    }
                    return;
                }
                List<Advancement> availableAdvancements = objectiveLists.getAvailableAdvancements();
                if (availableAdvancements.isEmpty()) {
                    Bukkit.getLogger().info("Advancement list is empty.");
                    player.sendMessage(ChatColor.RED + "Advancement list is empty.");
                    return;
                }

                Advancement newAdvancement = availableAdvancements.get(random.nextInt(availableAdvancements.size()));
                advancementMap.put(player.getUniqueId(), newAdvancement);
                if (forceBattlePlugin.getTimer().isRunning()) {
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                    player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                    player.sendMessage(ChatColor.GOLD + newAdvancement.getDescription());
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                }
                break;
            }
            case FORCE_HEIGHT: {
                if (chainManager.isChainModeEnabled()) {
                    int newHeight;
                    try {
                        newHeight = chainManager.getHeightChainList().get(chainManager.getHeightProgressMap().get(player));
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.GOLD + "Reached the end of Height Chain list. Starting over.");
                        chainManager.getHeightProgressMap().put(player, 0);
                        newHeight = chainManager.getHeightChainList().get(chainManager.getHeightProgressMap().get(player));
                    }
                    heightMap.put(player.getUniqueId(), newHeight);
                    chainManager.getHeightProgressMap().put(player, chainManager.getHeightProgressMap().get(player) + 1);
                    return;
                }
                List<Integer> availableHeights = objectiveLists.getAvailableHeights();
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

    private Challenge newChallengeType() {
        if (activeChallenges.isEmpty()) {
            return null;
        }
        return activeChallenges.get(random.nextInt(activeChallenges.size()));
    }

    public void updateObjective(Player player) {
        Challenge challenge = newChallengeType();
        if (forceBattlePlugin.getTimer().isRunning()) {
            finishedObjectives.get(player.getUniqueId()).add(getObjective(player));
            objectiveTimeMap.get(player.getUniqueId()).add(forceBattlePlugin.getTimer().getTime());
        }
        setChallengeType(player, challenge);
        newObjective(player, challenge);
        forceBattlePlugin.getNametagManager().updateNametag(player);
        forceBattlePlugin.getBossbarManager().updateBossbar(player);
    }

    public void resetProgress(Player player) {
        forceBattlePlugin.getPointsManager().setPoints(player, 0);
        finishedObjectives.put(player.getUniqueId(), new ArrayList<>());
    }

    public void giveJokers(Player player) {
        int skipAmount = forceBattlePlugin.getConfig().getInt("jokers", 3);

        if (skipAmount > 64) {
            skipAmount = 64;
        }

        int swapAmount;
        if (forceBattlePlugin.getConfig().get("swappers") == null) {
            swapAmount = 1;
        } else {
            swapAmount = forceBattlePlugin.getConfig().getInt("swappers");
        }
        if (swapAmount > 64) {
            swapAmount = 64;
        }

        player.getInventory().setItem(7, ItemProvider.getSwapitem(swapAmount));
        player.getInventory().setItem(8, ItemProvider.getSkipItem(skipAmount));
    }

    public void giveSkipItem(Player player, Integer amount) {
        int skipAmount = Objects.requireNonNullElseGet(amount, () -> forceBattlePlugin.getConfig().getInt("jokers", 3));

        if (skipAmount > 64) {
            skipAmount = 64;
        }

        Inventory inventory = player.getInventory();
        inventory.addItem(ItemProvider.getSkipItem(skipAmount));
    }

    public void giveSwapItem(Player player, Integer amount) {
        int swapAmount = Objects.requireNonNullElseGet(amount, () -> forceBattlePlugin.getConfig().getInt("swappers", 1));

        if (swapAmount > 64) {
            swapAmount = 64;
        }

        Inventory inventory = player.getInventory();
        inventory.addItem(ItemProvider.getSwapitem(swapAmount));
    }

    public List<Challenge> getActiveChallenges() {
        return activeChallenges;
    }

    public HashMap<UUID, List<Object>> getFinishedObjectives() {
        return finishedObjectives;
    }

    public HashMap<UUID, List<Integer>> getObjectiveTimeMap() {
        return objectiveTimeMap;
    }

    public void setChainManager(ChainManager chainManager) {
        this.chainManager = chainManager;
    }
}