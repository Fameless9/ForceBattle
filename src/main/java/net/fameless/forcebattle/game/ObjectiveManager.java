package net.fameless.forcebattle.game;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.game.data.BiomeSimplified;
import net.fameless.forcebattle.game.data.FBStructure;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectiveManager {

    private final List<String> chainList = new ArrayList<>();
    private final List<String> teamChainList = new ArrayList<>();

    public Objective getNewObjective(Team team) {
        if (SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE)) {
            int progress = team.getChainProgress();
            String objective;
            try {
                objective = getTeamChainList().get(progress);
            } catch (IndexOutOfBoundsException e) {
                progress = 0;
                objective = getTeamChainList().get(progress);
            }
            return new Objective(BukkitUtil.getBattleType(objective), objective);
        }

        List<BattlePlayer> players = team.getPlayers();
        if (players.isEmpty()) return null;

        Random random = new Random();
        BattlePlayer player = players.get(random.nextInt(players.size()));

        List<BattleType> availableBattleTypes = new ArrayList<>();
        for (BattleType type : BattleType.values()) {
            SettingsManager.SettingState state = type.getSettingState();
            if (state == SettingsManager.SettingState.TEAM || state == SettingsManager.SettingState.BOTH) {
                availableBattleTypes.add(type);
            }
        }

        if (availableBattleTypes.isEmpty()) return null;

        BattleType battleType = availableBattleTypes.get(random.nextInt(availableBattleTypes.size()));
        return generateObjective(battleType, player, team);
    }

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
        for (BattleType type : BattleType.values()) {
            SettingsManager.SettingState state = type.getSettingState();
            if (state == SettingsManager.SettingState.PLAYER || state == SettingsManager.SettingState.BOTH) {
                availableBattleTypes.add(type);
            }
        }

        if (availableBattleTypes.isEmpty()) return null;

        Random random = new Random();
        BattleType battleType = availableBattleTypes.get(random.nextInt(availableBattleTypes.size()));

        return generateObjective(battleType, battlePlayer, null);
    }

    private Objective generateObjective(BattleType battleType, BattlePlayer battlePlayer, Team team) {
        Random random = new Random();
        List<String> allPossible = getAllPossibleObjectives(battleType, battlePlayer);
        if (allPossible.isEmpty()) return null;

        if (SettingsManager.isEnabled(SettingsManager.Setting.NO_DUPLICATE_OBJECTIVES)) {
            Set<String> finished = new HashSet<>();
            if (team == null || !battlePlayer.isInTeam()) {
                Objective.finishedBy(battlePlayer).forEach(obj -> finished.add(obj.getObjectiveString()));
            } else {
                team.getPlayers().forEach(p -> Objective.finishedBy(p).forEach(obj -> finished.add(obj.getObjectiveString())));
            }
            allPossible.removeIf(finished::contains);
        }

        if (allPossible.isEmpty()) {
            battlePlayer.sendMessage(Caption.of(
                    "error.no_objective_available", TagResolver.resolver("type", Tag.inserting(Component.text(battleType.name())))
            ));
            if (team != null && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
                return getNewObjective(team);
            } else {
                return getNewObjective(battlePlayer);
            }
        }

        String objectiveString = allPossible.get(random.nextInt(allPossible.size()));
        return new Objective(battleType, objectiveString);
    }

    private List<String> getAllPossibleObjectives(BattleType battleType, BattlePlayer battlePlayer) {
        List<String> list = new ArrayList<>();
        switch (battleType) {
            case FORCE_ITEM -> getAvailableItems().forEach(material -> list.add(material.name()));
            case FORCE_MOB -> getAvailableMobs().forEach(entityType -> list.add(entityType.name()));
            case FORCE_BIOME -> {
                if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES))
                    getAvailableBiomesSimplified().forEach(biomeSimplified -> list.add(biomeSimplified.getName()));
                else
                    getAvailableBiomes().forEach(biome -> list.add(biome.name()));
            }
            case FORCE_ADVANCEMENT -> getAvailableAdvancements().forEach(advancement -> list.add(advancement.toString()));
            case FORCE_HEIGHT -> getAvailableHeights().forEach(height -> list.add(String.valueOf(height)));
            case FORCE_COORDS -> {
                Location coords = getRandomLocation(battlePlayer);
                list.add(coords.getX() + "," + coords.getZ());
            }
            case FORCE_STRUCTURE -> {
                if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES))
                    getAvailableStructuresSimplified().forEach(structureSimplified -> list.add(structureSimplified.getName()));
                else
                    getAvailableStructures().forEach(structure -> list.add(structure.toString()));
            }
        }
        return list;
    }

    public List<Material> getAvailableItems() {
        List<Material> availableItems = new ArrayList<>();

        List<String> itemsToExclude = ForceBattle.get().getConfig().getStringList("exclude.items");
        boolean excludeMusicDiscs = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_MUSIC_DISCS);
        boolean excludeBannerPatterns = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_BANNER_PATTERNS);
        boolean excludeArmorTemplates = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_ARMOR_TEMPLATES);
        boolean excludePotteryShreds = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_POTTERY_SHERDS);
        boolean excludeOres = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_ORES);
        boolean excludeEndItems = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        for (Material material : Material.values()) {
            if (!material.isItem()) continue;
            if (itemsToExclude.contains(material.name())) continue;
            if (excludeMusicDiscs && material.name().contains("DISC")) continue;
            if (excludeBannerPatterns && material.name().endsWith("BANNER_PATTERN")) continue;
            if (excludeArmorTemplates && material.name().endsWith("TEMPLATE")) continue;
            if (excludePotteryShreds && material.name().endsWith("POTTERY_SHERD")) continue;
            if (excludeOres && material.name().endsWith("ORE")) continue;
            if (excludeEndItems && ENDITEMS.contains(material)) continue;
            if (material.name().endsWith("SPAWN_EGG")) continue;
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
            if (material.name().startsWith("INFESTED")) continue;

            availableItems.add(material);
        }
        return availableItems;
    }

    public List<EntityType> getAvailableMobs() {
        List<EntityType> availableMobs = new ArrayList<>();
        boolean excludeEndMobs = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        List<String> mobsToExclude = ForceBattle.get().getConfig().getStringList("exclude.mobs");

        for (EntityType entity : EntityType.values()) {
            if (mobsToExclude.contains(entity.name())) continue;
            if (excludeEndMobs && ENDMOBS.contains(entity)) continue;

            availableMobs.add(entity);
        }
        return availableMobs;
    }

    public List<Biome> getAvailableBiomes() {
        List<Biome> availableBiomes = new ArrayList<>();
        boolean excludeBiomes = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        List<String> biomesToExclude = ForceBattle.get().getConfig().getStringList("exclude.biomes");

        for (Biome biome : Biome.values()) {
            if (biomesToExclude.contains(biome.name())) continue;
            if (excludeBiomes && ENDBIOMES.contains(biome)) continue;

            availableBiomes.add(biome);
        }
        return availableBiomes;
    }

    public List<BiomeSimplified> getAvailableBiomesSimplified() {
        List<BiomeSimplified> availableBiomes = new ArrayList<>();
        boolean excludeBiomes = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        List<String> biomesToExclude = ForceBattle.get().getConfig().getStringList("exclude.biomes");

        for (BiomeSimplified simplified : BiomeSimplified.values()) {
            if (biomesToExclude.contains(simplified.getName().toUpperCase(Locale.ROOT))) continue;
            if (excludeBiomes && ENDBIOMESSIMPLIFIED.contains(simplified)) continue;

            availableBiomes.add(simplified);
        }
        return availableBiomes;
    }

    public List<FBAdvancement> getAvailableAdvancements() {
        List<FBAdvancement> availableAdvancements = new ArrayList<>();

        List<String> advancementsToExclude = ForceBattle.get().getConfig().getStringList("exclude.advancements");

        for (FBAdvancement advancement : FBAdvancement.values()) {
            if (advancementsToExclude.contains(advancement.name())) continue;

            availableAdvancements.add(advancement);
        }
        return availableAdvancements;
    }

    public List<Integer> getAvailableHeights() {
        List<Integer> availableHeights = new ArrayList<>();

        List<String> heightsToExclude = ForceBattle.get().getConfig().getStringList("exclude.heights");

        for (int i = -63; i < 321; i++) {
            if (heightsToExclude.contains(String.valueOf(i))) continue;

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

    public List<FBStructure> getAvailableStructures() {
        List<FBStructure> availableStructures = new ArrayList<>();
        boolean excludeEndStructures = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        List<String> structuresToExclude = ForceBattle.get().getConfig().getStringList("exclude.structures");

        for (FBStructure structure : FBStructure.values()) {
            if (structuresToExclude.contains(structure.getKey())) continue;
            if (excludeEndStructures && ENDSTRUCTURES.contains(structure)) continue;

            availableStructures.add(structure);
        }
        return availableStructures;
    }

    public List<StructureSimplified> getAvailableStructuresSimplified() {
        List<StructureSimplified> availableStructures = new ArrayList<>();
        boolean excludeEndStructures = SettingsManager.isEnabled(SettingsManager.Setting.EXCLUDE_END);

        List<String> structuresToExclude = ForceBattle.get().getConfig().getStringList("exclude.structures");

        for (StructureSimplified simplified : StructureSimplified.values()) {
            if (structuresToExclude.contains(simplified.name().toUpperCase(Locale.ROOT))) continue;
            if (excludeEndStructures && ENDSTRUCTURESSIMPLIFIED.contains(simplified)) continue;

            availableStructures.add(simplified);
        }
        return availableStructures;
    }

    public List<String> getChainList() {
        if (chainList.isEmpty()) {
            updateChainList();
        }
        return chainList;
    }

    public List<String> getTeamChainList() {
        if (teamChainList.isEmpty()) {
            updateTeamChainList();
        }
        return teamChainList;
    }

    public void updateChainList() {
        chainList.clear();

        for (BattleType type : BattleType.values()) {
            SettingsManager.SettingState state = type.getSettingState();

            if (state != SettingsManager.SettingState.PLAYER && state != SettingsManager.SettingState.BOTH) continue;

            switch (type) {
                case BattleType.FORCE_ITEM -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) {
                        getAvailableItems().forEach(m -> chainList.add(m.name()));
                    }
                }
                case BattleType.FORCE_MOB -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) {
                        getAvailableMobs().forEach(e -> chainList.add(e.name()));
                    }
                }
                case BattleType.FORCE_ADVANCEMENT -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) {
                        getAvailableAdvancements().forEach(a -> chainList.add(a.name()));
                    }
                }
                case BattleType.FORCE_HEIGHT -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) {
                        getAvailableHeights().forEach(h -> chainList.add(String.valueOf(h)));
                    }
                }
                case BattleType.FORCE_BIOME -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) {
                        if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                            getAvailableBiomesSimplified().forEach(b -> chainList.add(b.getName()));
                        } else {
                            getAvailableBiomes().forEach(b -> chainList.add(b.name()));
                        }
                    }
                }
                case BattleType.FORCE_STRUCTURE -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) {
                        if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                            getAvailableStructuresSimplified().forEach(s -> chainList.add(s.name()));
                        } else {
                            getAvailableStructures().forEach(s -> chainList.add(s.name()));
                        }
                    }
                }
            }
        }

        Collections.shuffle(chainList);
    }

    public void updateTeamChainList() {
        teamChainList.clear();

        for (BattleType type : BattleType.values()) {
            SettingsManager.SettingState state = type.getSettingState();

            if (state != SettingsManager.SettingState.TEAM && state != SettingsManager.SettingState.BOTH) continue;

            switch (type) {
                case BattleType.FORCE_ITEM -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) {
                        getAvailableItems().forEach(m -> teamChainList.add(m.name()));
                    }
                }
                case BattleType.FORCE_MOB -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) {
                        getAvailableMobs().forEach(e -> teamChainList.add(e.name()));
                    }
                }
                case BattleType.FORCE_ADVANCEMENT -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) {
                        getAvailableAdvancements().forEach(a -> teamChainList.add(a.name()));
                    }
                }
                case BattleType.FORCE_HEIGHT -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) {
                        getAvailableHeights().forEach(h -> teamChainList.add(String.valueOf(h)));
                    }
                }
                case BattleType.FORCE_BIOME -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) {
                        if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                            getAvailableBiomesSimplified().forEach(b -> teamChainList.add(b.getName()));
                        } else {
                            getAvailableBiomes().forEach(b -> teamChainList.add(b.name()));
                        }
                    }
                }
                case BattleType.FORCE_STRUCTURE -> {
                    if (SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) {
                        if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                            getAvailableStructuresSimplified().forEach(s -> teamChainList.add(s.name()));
                        } else {
                            getAvailableStructures().forEach(s -> teamChainList.add(s.name()));
                        }
                    }
                }
            }
        }

        Collections.shuffle(teamChainList);
    }

    private final List<Material> ENDITEMS = List.of(
            Material.END_STONE, Material.END_STONE_BRICK_SLAB, Material.END_STONE_BRICKS, Material.END_STONE_BRICK_STAIRS, Material.END_STONE_BRICK_WALL,
            Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.END_GATEWAY, Material.END_ROD, Material.ELYTRA, Material.PURPUR_BLOCK, Material.PURPUR_PILLAR,
            Material.PURPUR_SLAB, Material.PURPUR_STAIRS, Material.CHORUS_FLOWER, Material.CHORUS_FRUIT, Material.CHORUS_PLANT, Material.POPPED_CHORUS_FRUIT,
            Material.DRAGON_BREATH, Material.DRAGON_EGG, Material.DRAGON_HEAD, Material.DRAGON_WALL_HEAD, Material.SHULKER_SHELL, Material.SHULKER_BOX,
            Material.BLUE_SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE
    );
    private final List<EntityType> ENDMOBS = List.of(
            EntityType.ENDER_DRAGON, EntityType.SHULKER, EntityType.SHULKER_BULLET, EntityType.DRAGON_FIREBALL
    );
    private final List<Biome> ENDBIOMES = List.of(
            Biome.THE_END, Biome.END_BARRENS, Biome.END_HIGHLANDS, Biome.END_MIDLANDS, Biome.SMALL_END_ISLANDS
    );
    private final List<BiomeSimplified> ENDBIOMESSIMPLIFIED = List.of(
            BiomeSimplified.END
    );
    private final List<FBStructure> ENDSTRUCTURES = List.of(
            FBStructure.END_CITY
    );
    private final List<StructureSimplified> ENDSTRUCTURESSIMPLIFIED = List.of(
            StructureSimplified.END_CITY
    );
}
