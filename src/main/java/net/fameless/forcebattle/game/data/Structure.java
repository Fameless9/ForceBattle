package net.fameless.forcebattle.game.data;

import lombok.Getter;
import org.bukkit.NamespacedKey;

@Getter
public enum Structure {
    VILLAGE_PLAINS(NamespacedKey.minecraft("village_plains"), "Village Plains"),
    VILLAGE_SAVANNA(NamespacedKey.minecraft("village_savanna"), "Village Savanna"),
    VILLAGE_SNOWY(NamespacedKey.minecraft("village_snowy"), "Village Snowy"),
    VILLAGE_TAIGA(NamespacedKey.minecraft("village_taiga"), "Village Taiga"),
    VILLAGE_DESERT(NamespacedKey.minecraft("village_desert"), "Village Desert"),
    PILLAGER_OUTPOST(NamespacedKey.minecraft("pillager_outpost"), "Pillager Outpost"),
    RUINED_PORTAL(NamespacedKey.minecraft("ruined_portal"), "Ruined Portal"),
    RUINED_PORTAL_MOUNTAIN(NamespacedKey.minecraft("ruined_portal_mountain"), "Ruined Portal Mountain"),
    RUINED_PORTAL_DESERT(NamespacedKey.minecraft("ruined_portal_desert"), "Ruined Portal Desert"),
    RUINED_PORTAL_JUNGLE(NamespacedKey.minecraft("ruined_portal_jungle"), "Ruined Portal Jungle"),
    RUINED_PORTAL_OCEAN(NamespacedKey.minecraft("ruined_portal_ocean"), "Ruined Portal Ocean"),
    RUINED_PORTAL_SWAMP(NamespacedKey.minecraft("ruined_portal_swamp"), "Ruined Portal Swamp"),
    RUINED_PORTAL_NETHER(NamespacedKey.minecraft("ruined_portal_nether"), "Ruined Portal Nether"),
    OCEAN_RUIN_COLD(NamespacedKey.minecraft("ocean_ruin_cold"), "Ocean Ruin Cold"),
    TRIAL_CHAMBERS(NamespacedKey.minecraft("trial_chambers"), "Trial Chambers"),
    SHIPWRECK(NamespacedKey.minecraft("shipwreck"), "Shipwreck"),
    SHIPWRECK_BEACHED(NamespacedKey.minecraft("shipwreck_beached"), "Shipwreck Beached"),
    OCEAN_RUIN_WARM(NamespacedKey.minecraft("ocean_ruin_warm"), "Ocean Ruin Warm"),
    TRAIL_RUINS(NamespacedKey.minecraft("trail_ruins"), "Trail Ruins"),
    MANSION(NamespacedKey.minecraft("mansion"), "Mansion"),
    JUNGLE_PYRAMID(NamespacedKey.minecraft("jungle_pyramid"), "Jungle Pyramid"),
    MINESHAFT_MESA(NamespacedKey.minecraft("mineshaft_mesa"), "Mineshaft Mesa"),
    SWAMP_HUT(NamespacedKey.minecraft("swamp_hut"), "Swamp Hut"),
    END_CITY(NamespacedKey.minecraft("end_city"), "End City"),
    ANCIENT_CITY(NamespacedKey.minecraft("ancient_city"), "Ancient City"),
    DESERT_PYRAMID(NamespacedKey.minecraft("desert_pyramid"), "Desert Pyramid"),
    MONUMENT(NamespacedKey.minecraft("monument"), "Monument"),
    IGLOO(NamespacedKey.minecraft("igloo"), "Igloo"),
    BASTION_REMNANT(NamespacedKey.minecraft("bastion_remnant"), "Bastion Remnant"),
    MINESHAFT(NamespacedKey.minecraft("mineshaft"), "Mineshaft"),
    FORTRESS(NamespacedKey.minecraft("fortress"), "Fortress"),
    STRONGHOLD(NamespacedKey.minecraft("stronghold"), "Stronghold");

    public final NamespacedKey key;
    public final String name;

    Structure(NamespacedKey key, String name) {
        this.key = key;
        this.name = name;
    }

}
