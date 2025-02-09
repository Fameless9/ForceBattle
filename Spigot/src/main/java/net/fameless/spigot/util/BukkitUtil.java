package net.fameless.spigot.util;

import net.fameless.forceBattle.util.BattleType;
import net.fameless.spigot.BukkitPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class BukkitUtil {

    public static final BukkitAudiences BUKKIT_AUDIENCES = BukkitAudiences.create(BukkitPlatform.get());

    private static final Map<BattleType, Function<String, Object>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(BattleType.FORCE_ITEM, BukkitUtil::tryMaterial);
        PARSERS.put(BattleType.FORCE_MOB, BukkitUtil::tryEntityType);
        PARSERS.put(BattleType.FORCE_BIOME, BukkitUtil::tryBiome);
        PARSERS.put(BattleType.FORCE_ADVANCEMENT, BukkitUtil::tryAdvancement);
        PARSERS.put(BattleType.FORCE_HEIGHT, BukkitUtil::tryHeight);
    }

    public static @Nullable Object convertObjective(@NotNull BattleType battleType, String objective) {
        Function<String, Object> parser = PARSERS.get(battleType);
        return (parser != null) ? parser.apply(objective) : null;
    }

    public static @Nullable BattleType getBattleType(String objectiveString) {
        return PARSERS.entrySet().stream()
                .filter(entry -> entry.getValue().apply(objectiveString) != null)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private static @Nullable Object tryMaterial(String value) {
        try {
            return Material.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static @Nullable Object tryEntityType(String value) {
        try {
            return EntityType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static @Nullable Object tryBiome(String value) {
        try {
            return Biome.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static @Nullable Object tryAdvancement(String value) {
        try {
            return Advancement.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Contract(pure = true)
    private static @Nullable @Unmodifiable Object tryHeight(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
