package net.fameless.forcebattle.game;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.BiomeSimplified;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.game.data.Advancement;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.game.data.Structure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectiveManager {

    private final List<String> chainList = new ArrayList<>();

    public Objective getNewObjective(BattlePlayer battlePlayer) {
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
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) availableBattleTypes.add(BattleType.FORCE_ITEM);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) availableBattleTypes.add(BattleType.FORCE_MOB);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) availableBattleTypes.add(BattleType.FORCE_BIOME);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) availableBattleTypes.add(BattleType.FORCE_ADVANCEMENT);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) availableBattleTypes.add(BattleType.FORCE_HEIGHT);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS)) availableBattleTypes.add(BattleType.FORCE_COORDS);
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) availableBattleTypes.add(BattleType.FORCE_STRUCTURE);

        Random random = new Random();
        BattleType battleType = availableBattleTypes.get(random.nextInt(availableBattleTypes.size()));
        String objectiveString;

        switch (battleType) {
            case FORCE_ITEM -> objectiveString = getAvailableItems().get(random.nextInt(getAvailableItems().size())).name();
            case FORCE_MOB -> objectiveString = getAvailableMobs().get(random.nextInt(getAvailableMobs().size())).name();
            case FORCE_BIOME -> {
                if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                    List<BiomeSimplified> list = getAvailableBiomesSimplified();
                    objectiveString = list.get(random.nextInt(list.size())).getName();
                } else {
                    List<Biome> list = getAvailableBiomes();
                    objectiveString = list.get(random.nextInt(list.size())).name();
                }
            }
            case FORCE_ADVANCEMENT -> objectiveString = getAvailableAdvancements().get(random.nextInt(getAvailableAdvancements().size())).toString();
            case FORCE_HEIGHT -> objectiveString = String.valueOf(getAvailableHeights().get(random.nextInt(getAvailableHeights().size())));
            case FORCE_COORDS -> {
                Location coords = getRandomLocation(battlePlayer);
                objectiveString = coords.getX() + "," + coords.getZ();
            }
            case FORCE_STRUCTURE -> {
                if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                    List<StructureSimplified> list = getAvailableStructuresSimplified();
                    objectiveString = list.get(random.nextInt(list.size())).getName();
                } else {
                    List<Structure> list = getAvailableStructures();
                    objectiveString = list.get(random.nextInt(list.size())).toString();
                }
            }
            default -> {
                return null;
            }
        }

        if (SettingsManager.isEnabled(SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES)) {
            List<Objective> objectives = new ArrayList<>();

            if (!battlePlayer.isInTeam()) {
                objectives.addAll(Objective.finishedBy(battlePlayer));
            } else {
                battlePlayer.getTeam().getPlayers().forEach(teamPlayer -> objectives.addAll(Objective.finishedBy(teamPlayer)));
            }

            for (Objective objective : objectives) {
                if (Objects.equals(objective.getObjectiveString(), objectiveString)) {
                    return getNewObjective(battlePlayer);
                }
            }
        }

        System.out.println(objectiveString);
        return new Objective(battleType, objectiveString);
    }

    public List<Material> getAvailableItems() {
        List<Material> availableItems = new ArrayList<>();

        List<String> itemsToExclude = ForceBattle.get().getConfig().getStringList("exclude.items");
        boolean excludeSpawnEggs = ForceBattle.get().getConfig().getBoolean("exclude.exclude_spawn_eggs", true);
        boolean excludeMusicDiscs = ForceBattle.get().getConfig().getBoolean("exclude.exclude_music_discs", false);
        boolean excludeBannerPatterns = ForceBattle.get().getConfig().getBoolean("exclude.exclude_banner_patterns", true);
        boolean excludeBanners = ForceBattle.get().getConfig().getBoolean("exclude.exclude_banners", true);
        boolean excludeArmorTemplates = ForceBattle.get().getConfig().getBoolean("exclude.exclude_armor_templates", false);
        boolean excludePotteryShreds = ForceBattle.get().getConfig().getBoolean("exclude.exclude_pottery_sherds", false);
        boolean excludeOres = ForceBattle.get().getConfig().getBoolean("exclude.exclude_ores", false);

        for (Material material : Material.values()) {
            if (!material.isItem()) continue;
            if (itemsToExclude.contains(material.name())) continue;
            if (excludeSpawnEggs && material.name().endsWith("SPAWN_EGG")) continue;
            if (excludeMusicDiscs && material.name().contains("DISC")) continue;
            if (excludeBannerPatterns && material.name().endsWith("BANNER_PATTERN")) continue;
            if (excludeBanners && material.name().endsWith("BANNER")) continue;
            if (excludeArmorTemplates && material.name().endsWith("TEMPLATE")) continue;
            if (excludePotteryShreds && material.name().endsWith("POTTERY_SHERD")) continue;
            if (excludeOres && material.name().endsWith("ORE")) continue;
            if (material.name().endsWith("CANDLE_CAKE")) continue;
            if (material.name().startsWith("POTTED")) continue;
            if (material.name().contains("PETRIFIED")) continue;
            if (material.name().contains("WALL") && material.name().contains("TORCH")) continue;
            if (material.name().contains("WALL") && material.name().contains("SIGN")) continue;
            if (material.name().contains("WALL") && material.name().contains("HEAD")) continue;
            if (material.name().contains("WALL") && material.name().contains("CORAL")) continue;
            if (material.name().contains("WALL") && material.name().contains("BANNER")) continue;
            if (material.name().contains("WALL") && material.name().contains("SKULL")) continue;
            if (material.name().endsWith("STEM")) continue;

            availableItems.add(material);
        }
        return availableItems;
    }

    public List<EntityType> getAvailableMobs() {
        List<EntityType> availableMobs = new ArrayList<>();

        List<String> mobsToExclude = ForceBattle.get().getConfig().getStringList("exclude.mobs");

        for (EntityType entity : EntityType.values()) {
            if (mobsToExclude.contains(entity.name())) {
                continue;
            }
            availableMobs.add(entity);
        }
        return availableMobs;
    }

    public List<Biome> getAvailableBiomes() {
        List<Biome> availableBiomes = new ArrayList<>();

        List<String> biomesToExclude = ForceBattle.get().getConfig().getStringList("exclude.biomes");

        for (Biome biome : Biome.values()) {
            if (biomesToExclude.contains(biome.name())) {
                continue;
            }
            availableBiomes.add(biome);
        }
        return availableBiomes;
    }

    public List<BiomeSimplified> getAvailableBiomesSimplified() {
        List<BiomeSimplified> list = new ArrayList<>(Arrays.asList(BiomeSimplified.values()));
        List<String> biomesToExclude = ForceBattle.get().getConfig().getStringList("exclude.biomes");
        list.removeIf(b -> biomesToExclude.contains(b.getName().toUpperCase(Locale.ROOT)));
        return list;
    }

    public List<Advancement> getAvailableAdvancements() {
        List<Advancement> availableAdvancements = new ArrayList<>();

        List<String> advancementsToExclude = ForceBattle.get().getConfig().getStringList("exclude.advancements");

        for (Advancement advancement : Advancement.values()) {
            if (advancementsToExclude.contains(advancement.name())) {
                continue;
            }
            availableAdvancements.add(advancement);
        }
        return availableAdvancements;
    }

    public List<Integer> getAvailableHeights() {
        List<Integer> availableHeights = new ArrayList<>();

        List<String> heightsToExclude = ForceBattle.get().getConfig().getStringList("exclude.heights");

        for (int i = -63; i < 321; i++) {
            if (heightsToExclude.contains(String.valueOf(i))) {
                continue;
            }
            availableHeights.add(i);
        }
        return availableHeights;
    }

    public Location getRandomLocation(BattlePlayer battlePlayer) {
        Player player = battlePlayer.getPlayer();
        if (player == null) return new Location(Bukkit.getWorld("world"), 0, 0, 0);

        int maxDistance = 2000;

        int offsetX = ThreadLocalRandom.current().nextInt(-maxDistance, maxDistance + 1);
        int offsetZ = ThreadLocalRandom.current().nextInt(-maxDistance, maxDistance + 1);

        Location base = player.getLocation();

        int x = (int) base.getX() + offsetX;
        int z = (int) base.getZ() + offsetZ;

        int y = player.getWorld().getHighestBlockYAt(x, z);

        return new Location(player.getWorld(), x, y, z);
    }

    public List<Structure> getAvailableStructures() {
        List<Structure> availableStructures = new ArrayList<>();

        List<String> structuresToExclude = ForceBattle.get().getConfig().getStringList("exclude.structures");

        for (Structure structure : Structure.values()) {
            if (structuresToExclude.contains(structure.getKey())) {
                continue;
            }

            availableStructures.add(structure);
        }
        return availableStructures;
    }

    public List<StructureSimplified> getAvailableStructuresSimplified() {
        List<StructureSimplified> list = new ArrayList<>(Arrays.asList(StructureSimplified.values()));
        List<String> structuresToExclude = ForceBattle.get().getConfig().getStringList("exclude.structures");
        list.removeIf(s -> structuresToExclude.contains(s.name().toUpperCase(Locale.ROOT)));
        return list;
    }

    public List<String> getChainList() {
        if (chainList.isEmpty()) {
            updateChainList();
        }
        return chainList;
    }

    public void updateChainList() {
        chainList.clear();
        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM))
            getAvailableItems().forEach(m -> chainList.add(m.name()));

        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB))
            getAvailableMobs().forEach(e -> chainList.add(e.name()));

        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) {
            if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                getAvailableBiomesSimplified().forEach(b -> chainList.add(b.getName()));
            } else {
                getAvailableBiomes().forEach(b -> chainList.add(b.name()));
            }
        }

        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT))
            getAvailableAdvancements().forEach(a -> chainList.add(a.name()));

        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT))
            getAvailableHeights().forEach(h -> chainList.add(String.valueOf(h)));

        if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) {
            if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                getAvailableStructuresSimplified().forEach(s -> chainList.add(s.name()));
            } else {
                getAvailableStructures().forEach(s -> chainList.add(s.name()));
            }
        }

        Collections.shuffle(chainList);
    }

}
