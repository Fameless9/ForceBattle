package net.fameless.forcebattle.util;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.game.data.BiomeSimplified;
import net.fameless.forcebattle.game.data.FBStructure;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public static final BukkitAudiences BUKKIT_AUDIENCES = BukkitAudiences.create(ForceBattle.get());

    private static final Map<BattleType, Function<String, Object>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(BattleType.FORCE_ITEM, BukkitUtil::tryMaterial);
        PARSERS.put(BattleType.FORCE_MOB, BukkitUtil::tryEntityType);
        PARSERS.put(BattleType.FORCE_BIOME, BukkitUtil::tryBiome);
        PARSERS.put(BattleType.FORCE_ADVANCEMENT, BukkitUtil::tryAdvancement);
        PARSERS.put(BattleType.FORCE_HEIGHT, BukkitUtil::tryHeight);
        PARSERS.put(BattleType.FORCE_COORDS, BukkitUtil::tryCoords);
        PARSERS.put(BattleType.FORCE_STRUCTURE, BukkitUtil::tryStructure);
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
        if (value == null || value.isEmpty()) return null;

        for (BiomeSimplified simplified : BiomeSimplified.values()) {
            if (simplified.getName().equalsIgnoreCase(value)) {
                return simplified;
            }
        }

        try {
            return Biome.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static @Nullable Object tryAdvancement(String value) {
        try {
            return FBAdvancement.valueOf(value);
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

    @Contract(pure = true)
    private static @Nullable @Unmodifiable Object tryCoords(String value) {
        try {
            String[] parts = value.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            int z = Integer.parseInt(parts[2].trim());
            return new Location(Bukkit.getWorld("world"), x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static @Nullable Object tryStructure(String value) {
        if (value == null || value.isEmpty()) return null;

        for (StructureSimplified simplified : StructureSimplified.values()) {
            if (simplified.getName().equalsIgnoreCase(value)) {
                return simplified;
            }
        }

        try {
            return FBStructure.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
