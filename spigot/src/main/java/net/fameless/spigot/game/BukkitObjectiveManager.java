package net.fameless.spigot.game;

import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.game.Objective;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import net.fameless.core.util.Coords;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.player.BukkitPlayer;
import net.fameless.spigot.util.Advancement;
import net.fameless.spigot.util.BukkitUtil;
import net.fameless.spigot.util.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BukkitObjectiveManager implements ObjectiveManager {

    private final List<String> chainList = new ArrayList<>();

    @Override
    public Objective getNewObjective(BattlePlayer<?> battlePlayer) {
        if (SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE)) {
            int progress = battlePlayer.getChainProgress();
            String objective;
            try {
                objective = getChainList().get(progress);
            } catch (IndexOutOfBoundsException e) {
                progress = 0;
                objective = getChainList().get(progress);
            }
            return new Objective(BukkitUtil.getBattleType(objective), objective);
        }

        List<BattleType> availableBattleTypes = new ArrayList<>();
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) {
            availableBattleTypes.add(BattleType.FORCE_ITEM);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) {
            availableBattleTypes.add(BattleType.FORCE_MOB);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) {
            availableBattleTypes.add(BattleType.FORCE_BIOME);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) {
            availableBattleTypes.add(BattleType.FORCE_ADVANCEMENT);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) {
            availableBattleTypes.add(BattleType.FORCE_HEIGHT);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS)) {
            availableBattleTypes.add(BattleType.FORCE_COORDS);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) {
            availableBattleTypes.add(BattleType.FORCE_STRUCTURE);
        }

        Random random = new Random();
        BattleType battleType = availableBattleTypes.get(random.nextInt(availableBattleTypes.size()));
        String objectiveString;

        switch (battleType) {
            case FORCE_ITEM -> objectiveString = getAvailableItems().get(random.nextInt(getAvailableItems().size())).name();
            case FORCE_MOB -> objectiveString = getAvailableMobs().get(random.nextInt(getAvailableMobs().size())).name();
            case FORCE_BIOME -> objectiveString = getAvailableBiomes().get(random.nextInt(getAvailableBiomes().size())).name();
            case FORCE_ADVANCEMENT -> objectiveString = getAvailableAdvancements().get(random.nextInt(getAvailableAdvancements().size())).toString();
            case FORCE_HEIGHT -> objectiveString = String.valueOf(getAvailableHeights().get(random.nextInt(getAvailableHeights().size())));
            case FORCE_COORDS -> {
                Coords coords = getRandomLocation(battlePlayer);
                objectiveString = coords.x() + "," + coords.z();
            }
            case FORCE_STRUCTURE -> objectiveString = getAvailableStructures().get(random.nextInt(getAvailableStructures().size())).toString();

            default -> {
                return null;
            }
        }

        return new Objective(battleType, objectiveString);
    }

    @Override
    public List<Material> getAvailableItems() {
        List<Material> availableItems = new ArrayList<>();

        List<String> itemsToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.items");
        boolean excludeSpawnEggs = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_spawn_eggs", true);
        boolean excludeMusicDiscs = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_music_discs", false);
        boolean excludeBannerPatterns = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_banner_patterns", true);
        boolean excludeBanners = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_banners", true);
        boolean excludeArmorTemplates = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_armor_templates", false);
        boolean excludePotteryShreds = BukkitPlatform.get().getConfig().getBoolean("exclude.exclude_pottery_sherds", false);

        for (Material material : Material.values()) {
            if (!material.isItem()) {
                continue;
            }
            if (itemsToExclude.contains(material.name())) {
                continue;
            }
            if (excludeSpawnEggs && material.name().endsWith("SPAWN_EGG")) {
                continue;
            }
            if (excludeMusicDiscs && material.name().contains("DISC")) {
                continue;
            }
            if (excludeBannerPatterns && material.name().endsWith("BANNER_PATTERN")) {
                continue;
            }
            if (excludeBanners && material.name().endsWith("BANNER")) {
                continue;
            }
            if (excludeArmorTemplates && material.name().endsWith("TEMPLATE")) {
                continue;
            }
            if (excludePotteryShreds && material.name().endsWith("POTTERY_SHERD")) {
                continue;
            }
            if (material.name().endsWith("CANDLE_CAKE")) {
                continue;
            }
            if (material.name().startsWith("POTTED")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                continue;
            }
            if (material.name().endsWith("STEM")) {
                continue;
            }
            availableItems.add(material);
        }
        return availableItems;
    }

    @Override
    public List<EntityType> getAvailableMobs() {
        List<EntityType> availableMobs = new ArrayList<>();

        List<String> mobsToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.mobs");

        for (EntityType entity : EntityType.values()) {
            if (mobsToExclude.contains(entity.name())) {
                continue;
            }
            availableMobs.add(entity);
        }
        return availableMobs;
    }

    @Override
    public List<Biome> getAvailableBiomes() {
        List<Biome> availableBiomes = new ArrayList<>();

        List<String> biomesToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.biomes");

        for (Biome biome : Biome.values()) {
            if (biomesToExclude.contains(biome.name())) {
                continue;
            }
            availableBiomes.add(biome);
        }
        return availableBiomes;
    }

    @Override
    public List<Advancement> getAvailableAdvancements() {
        List<Advancement> availableAdvancements = new ArrayList<>();

        List<String> advancementsToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.advancements");

        for (Advancement advancement : Advancement.values()) {
            if (advancementsToExclude.contains(advancement.name())) {
                continue;
            }
            availableAdvancements.add(advancement);
        }
        return availableAdvancements;
    }

    @Override
    public List<Integer> getAvailableHeights() {
        List<Integer> availableHeights = new ArrayList<>();

        List<String> heightsToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.heights");

        for (int i = -63; i < 321; i++) {
            if (heightsToExclude.contains(String.valueOf(i))) {
                continue;
            }
            availableHeights.add(i);
        }
        return availableHeights;
    }

    @Override
    public Coords getRandomLocation(BattlePlayer<?> battlePlayer) {
        Optional<BukkitPlayer> bukkitPlayer = BukkitPlayer.adapt(battlePlayer.getUniqueId());
        Player player = bukkitPlayer.get().getPlatformPlayer();
        int maxDistance = 2000;

        int offsetX = ThreadLocalRandom.current().nextInt(-maxDistance, maxDistance + 1);
        int offsetZ = ThreadLocalRandom.current().nextInt(-maxDistance, maxDistance + 1);

        Location base = player.getLocation();

        int x = (int) base.getX() + offsetX;
        int z = (int) base.getZ() + offsetZ;

        int y = player.getWorld().getHighestBlockYAt(x, z);

        return new Coords(x, y, z);
    }

    @Override
    public List<Structure> getAvailableStructures() {
        List<Structure> availableStructures = new ArrayList<>();

        List<String> structuresToExclude = BukkitPlatform.get().getConfig().getStringList("exclude.structures");

        for (Structure structure : Structure.values()) {
            if (structuresToExclude.contains(structure.getKey())) {
                continue;
            }

            availableStructures.add(structure);
        }
        return availableStructures;
    }

    @Override
    public List<String> getChainList() {
        if (chainList.isEmpty()) {
            updateChainList();
        }
        return chainList;
    }

    @Override
    public void updateChainList() {
        chainList.clear();
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) {
            List<String> itemStringList = new ArrayList<>();
            getAvailableItems().forEach(material -> itemStringList.add(material.name()));
            chainList.addAll(itemStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) {
            List<String> mobStringList = new ArrayList<>();
            getAvailableMobs().forEach(entityType -> mobStringList.add(entityType.name()));
            chainList.addAll(mobStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) {
            List<String> biomeStringList = new ArrayList<>();
            getAvailableBiomes().forEach(biome -> biomeStringList.add(biome.name()));
            chainList.addAll(biomeStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) {
            List<String> advancementStringList = new ArrayList<>();
            getAvailableAdvancements().forEach(advancement -> advancementStringList.add(advancement.name()));
            chainList.addAll(advancementStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) {
            List<String> heightStringList = new ArrayList<>();
            getAvailableHeights().forEach(height -> heightStringList.add(String.valueOf(height)));
            chainList.addAll(heightStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS)) {
            List<String> coordStringList = new ArrayList<>();
            getAvailableHeights().forEach(coord -> coordStringList.add(String.valueOf(coord)));
            chainList.addAll(coordStringList);
        }
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) {
            List<String> structureStringList = new ArrayList<>();
            getAvailableStructures().forEach(structure -> structureStringList.add(structure.name()));
            chainList.addAll(structureStringList);
        }
        Collections.shuffle(chainList);
    }

}
