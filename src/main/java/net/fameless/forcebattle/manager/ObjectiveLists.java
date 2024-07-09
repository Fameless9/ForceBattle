package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Advancement;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveLists {

    private final ForceBattlePlugin forceBattlePlugin;
    private final FileConfiguration configuration;

    private boolean excludeSpawnEggs;
    private boolean excludeMusicDiscs;
    private boolean excludeBannerPatterns;
    private boolean excludeBanners;
    private boolean excludeArmorTemplates;

    private final List<Material> availableItems = new ArrayList<>();
    private final List<EntityType> availableMobs = new ArrayList<>();
    private final List<Biome> availableBiomes = new ArrayList<>();
    private final List<Advancement> availableAdvancements = new ArrayList<>();
    private final List<Integer> availableHeights = new ArrayList<>();

    public ObjectiveLists(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
        this.configuration = forceBattlePlugin.getConfig();
        this.excludeSpawnEggs = configuration.getBoolean("exclude.exclude_spawn_eggs", true);
        this.excludeMusicDiscs = configuration.getBoolean("exclude.exclude_music_discs", true);
        this.excludeBannerPatterns = configuration.getBoolean("exclude.exclude_banner_patterns", true);
        this.excludeBanners = configuration.getBoolean("exclude.exclude_banners", false);
        this.excludeArmorTemplates = configuration.getBoolean("exclude.exclude_armor_templates", false);
        initLists();
    }

    public void update() {
        this.excludeSpawnEggs = forceBattlePlugin.getConfig().getBoolean("exclude.exclude_spawn_eggs");
        this.excludeMusicDiscs = forceBattlePlugin.getConfig().getBoolean("exclude.exclude_music_discs");
        this.excludeBannerPatterns = forceBattlePlugin.getConfig().getBoolean("exclude.exclude_banner_patterns");
        this.excludeBanners = forceBattlePlugin.getConfig().getBoolean("exclude.exclude_banners");
        this.excludeArmorTemplates = forceBattlePlugin.getConfig().getBoolean("exclude.exclude_armor_templates");

        this.availableItems.clear();
        this.availableMobs.clear();
        this.availableBiomes.clear();
        this.availableAdvancements.clear();
        this.availableHeights.clear();

        initLists();
    }

    private void initLists() {
        List<String> itemsToExclude = configuration.getStringList("exclude.items");

        for (Material material : Material.values()) {
            if (itemsToExclude.contains(material.name())) continue;
            if (excludeSpawnEggs && material.name().endsWith("SPAWN_EGG")) continue;
            if (excludeMusicDiscs && material.name().contains("DISC")) continue;
            if (excludeBannerPatterns && material.name().endsWith("BANNER_PATTERN")) continue;
            if (excludeBanners && material.name().endsWith("BANNER")) continue;
            if (excludeArmorTemplates && material.name().endsWith("TEMPLATE")) continue;
            if (material.name().endsWith("CANDLE_CAKE")) continue;
            if (material.name().startsWith("POTTED")) continue;
            if (material.name().contains("WALL") && material.name().contains("TORCH")) continue;
            if (material.name().contains("WALL") && material.name().contains("SIGN")) continue;
            if (material.name().contains("WALL") && material.name().contains("HEAD")) continue;
            if (material.name().contains("WALL") && material.name().contains("CORAL")) continue;
            if (material.name().contains("WALL") && material.name().contains("BANNER")) continue;
            if (material.name().contains("WALL") && material.name().contains("SKULL")) continue;
            if (material.name().endsWith("STEM")) continue;
            availableItems.add(material);
        }

        List<String> mobsToExclude = configuration.getStringList("exclude.mobs");

        for (EntityType entity : EntityType.values()) {
            if (mobsToExclude.contains(entity.name())) continue;
            availableMobs.add(entity);
        }

        List<String> biomesToExclude = configuration.getStringList("exclude.biomes");

        for (Biome biome : Biome.values()) {
            if (biomesToExclude.contains(biome.name())) continue;
            availableBiomes.add(biome);
        }

        List<String> advancementsToExclude = configuration.getStringList("exclude.advancements");

        for (Advancement advancement : Advancement.values()) {
            if (advancementsToExclude.contains(advancement.name())) continue;
            availableAdvancements.add(advancement);
        }

        List<String> heightsToExclude = configuration.getStringList("exclude.heights");

        for (int i = -63; i < 321; i++) {
            if (heightsToExclude.contains(String.valueOf(i))) continue;
            availableHeights.add(i);
        }
    }

    public List<Material> getAvailableItems() {
        return availableItems;
    }

    public List<EntityType> getAvailableMobs() {
        return availableMobs;
    }

    public List<Biome> getAvailableBiomes() {
        return availableBiomes;
    }

    public List<Advancement> getAvailableAdvancements() {
        return availableAdvancements;
    }

    public List<Integer> getAvailableHeights() {
        return availableHeights;
    }
}