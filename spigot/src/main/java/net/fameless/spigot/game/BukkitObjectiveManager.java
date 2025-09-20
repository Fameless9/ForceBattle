package net.fameless.spigot.game;

import net.fameless.core.config.PluginConfig;
import net.fameless.core.game.Objective;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.util.Advancement;
import net.fameless.spigot.util.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BukkitObjectiveManager extends ObjectiveManager {

    private final Random random = new Random();

    @Override
    public Objective getNewObjective(BattlePlayer<?> battlePlayer) {
        if (!BattleType.isAnyEnabled()) {
            return null;
        }
        if (PluginConfig.get().getBoolean("settings.enable-chain-mode", false)) {
            if (getChainList().isEmpty()) {
                return null;
            }
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

        List<BattleType> availableBattleTypes = BattleType.getEnabledBattleTypes();
        BattleType battleType = availableBattleTypes.get(random.nextInt(availableBattleTypes.size()));

        String objectiveString;
        switch (battleType) {
            case FORCE_ITEM -> objectiveString = getAvailableItems().get(random.nextInt(getAvailableItems().size()));
            case FORCE_MOB -> objectiveString = getAvailableMobs().get(random.nextInt(getAvailableMobs().size()));
            case FORCE_BIOME -> objectiveString = getAvailableBiomes().get(random.nextInt(getAvailableBiomes().size()));
            case FORCE_ADVANCEMENT -> objectiveString = getAvailableAdvancements().get(random.nextInt(getAvailableAdvancements().size()));
            case FORCE_HEIGHT -> objectiveString = getAvailableHeights().get(random.nextInt(getAvailableHeights().size()));
            default -> {
                return null;
            }
        }

        return new Objective(battleType, objectiveString);
    }

    @Override
    public List<String> getAvailableObjectives(final @NotNull BattleType battleType) {
        return switch (battleType) {
            case FORCE_ITEM -> getAvailableItems();
            case FORCE_MOB -> getAvailableMobs();
            case FORCE_BIOME -> getAvailableBiomes();
            case FORCE_ADVANCEMENT -> getAvailableAdvancements();
            case FORCE_HEIGHT -> getAvailableHeights();
        };
    }

    @Override
    public List<String> getAvailableItems() {
        List<String> excludedItems = PluginConfig.get().getStringList("modes.force-item.excluded");
        boolean excludeSpawnEggs = PluginConfig.get().getBoolean("modes.force-item.exclude-spawn-eggs", true);
        boolean excludeMusicDiscs = PluginConfig.get().getBoolean("modes.force-item.exclude-music-discs", true);
        boolean excludeBannerPatterns = PluginConfig.get().getBoolean("modes.force-item.exclude-banner-patterns", true);
        boolean excludeBanners = PluginConfig.get().getBoolean("modes.force-item.exclude-banners", true);
        boolean excludeArmorTemplates = PluginConfig.get().getBoolean("modes.force-item.exclude-armor-templates", true);

        return Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Enum::name)
                .filter(name -> !excludedItems.contains(name))
                .filter(name -> !(excludeSpawnEggs && name.endsWith("SPAWN_EGG")))
                .filter(name -> !(excludeMusicDiscs && name.contains("DISC")))
                .filter(name -> !(excludeBannerPatterns && name.endsWith("BANNER_PATTERN")))
                .filter(name -> !(excludeBanners && name.endsWith("BANNER")))
                .filter(name -> !(excludeArmorTemplates && name.endsWith("TEMPLATE")))
                .filter(name -> !name.endsWith("CANDLE_CAKE"))
                .filter(name -> !name.startsWith("POTTED"))
                .filter(name -> !name.contains("WALL") || !(name.contains("TORCH") ||
                        name.contains("SIGN") ||
                        name.contains("HEAD") ||
                        name.contains("CORAL") ||
                        name.contains("BANNER") ||
                        name.contains("SKULL")))
                .filter(name -> !name.endsWith("STEM"))
                .toList();
    }

    @Override
    public List<String> getAvailableMobs() {
        List<String> excludedMobs = PluginConfig.get().getStringList("modes.force-mob.excluded");

        return Arrays.stream(EntityType.values())
                .map(Enum::name)
                .filter(entityType -> !excludedMobs.contains(entityType))
                .toList();
    }

    @Override
    public List<String> getAvailableBiomes() {
        List<String> excludedBiomes = PluginConfig.get().getStringList("modes.force-biome.excluded");

        return Arrays.stream(Biome.values())
                .map(biome -> biome.name())
                .filter(biome -> !excludedBiomes.contains(biome))
                .toList();
    }

    @Override
    public List<String> getAvailableAdvancements() {
        List<String> excludedAdvancements = PluginConfig.get().getStringList("modes.force-advancement.excluded");

        return Arrays.stream(Advancement.values())
                .map(Advancement::name)
                .filter(advancement -> !excludedAdvancements.contains(advancement))
                .toList();
    }

    @Override
    public List<String> getAvailableHeights() {
        List<String> excludedHeights = BukkitPlatform.get().getConfig().getStringList("exclude.heights");

        List<String> availableHeights = new ArrayList<>();
        for (int i = -63; i < 321; i++) {
            String iString = String.valueOf(i);
            if (excludedHeights.contains(iString)) {
                continue;
            }
            availableHeights.add(iString);
        }
        return availableHeights;
    }

    @Override
    public List<String> getChainList() {
        if (chainList.isEmpty()) {
            updateChainList();
        }
        return chainList;
    }

}
