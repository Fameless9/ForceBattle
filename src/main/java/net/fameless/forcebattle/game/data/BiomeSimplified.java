package net.fameless.forcebattle.game.data;

import lombok.Getter;
import org.bukkit.block.Biome;

import java.util.List;

@Getter
public enum BiomeSimplified {
    OCEAN("Ocean", List.of(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN)),
    PLAINS("Plains", List.of(Biome.PLAINS, Biome.SUNFLOWER_PLAINS, Biome.SNOWY_PLAINS)),
    DESERT("Desert", List.of(Biome.DESERT)),
    BIRCH("Birch", List.of(Biome.BIRCH_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST)),
    JUNGLE("Jungle", List.of(Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE)),
    TAIGA("Taiga", List.of(Biome.TAIGA, Biome.SNOWY_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.OLD_GROWTH_PINE_TAIGA)),
    SAVANNA("Savanna", List.of(Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA)),
    SWAMP("Swamp", List.of(Biome.SWAMP, Biome.MANGROVE_SWAMP)),
    BADLANDS("Badlands", List.of(Biome.BADLANDS, Biome.WOODED_BADLANDS, Biome.ERODED_BADLANDS)),
    MOUNTAINS("Mountains", List.of(Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_FOREST, Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.MEADOW, Biome.GROVE, Biome.SNOWY_SLOPES, Biome.FROZEN_PEAKS, Biome.JAGGED_PEAKS, Biome.STONY_PEAKS)),
    RIVER("River", List.of(Biome.RIVER, Biome.FROZEN_RIVER)),
    BEACH("Beach", List.of(Biome.BEACH, Biome.SNOWY_BEACH, Biome.STONY_SHORE)),
    CAVE("Cave", List.of(Biome.DRIPSTONE_CAVES, Biome.LUSH_CAVES, Biome.DEEP_DARK)),
    NETHER_WASTES("Nether Wastes", List.of(Biome.NETHER_WASTES)),
    SOUL_SAND_VALLEY("Soul Sand Valley", List.of(Biome.SOUL_SAND_VALLEY)),
    CRIMSON_FOREST("Crimson Forest", List.of(Biome.CRIMSON_FOREST)),
    WARPED_FOREST("Warped Forest", List.of(Biome.WARPED_FOREST)),
    BASALT_DELTAS("Basalt Deltas", List.of(Biome.BASALT_DELTAS)),
    END("The End", List.of(Biome.THE_END, Biome.SMALL_END_ISLANDS, Biome.END_MIDLANDS, Biome.END_HIGHLANDS, Biome.END_BARRENS)),
    FROZEN("Frozen", List.of(Biome.ICE_SPIKES, Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.FROZEN_PEAKS, Biome.FROZEN_RIVER)),
    MUSHROOM_FIELDS("Mushroom Fields", List.of(Biome.MUSHROOM_FIELDS)),
    ;

    private final String name;
    private final List<Biome> biomes;

    BiomeSimplified(String name, List<Biome> biomes) {
        this.name = name;
        this.biomes = biomes;
    }
}
