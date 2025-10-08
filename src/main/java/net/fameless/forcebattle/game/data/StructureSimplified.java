package net.fameless.forcebattle.game.data;

import lombok.Getter;
import java.util.List;

@Getter
public enum StructureSimplified {
    VILLAGE("Village", List.of(Structure.VILLAGE_PLAINS, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_SNOWY, Structure.VILLAGE_TAIGA, Structure.VILLAGE_DESERT)),
    PILLAGER_OUTPOST("Pillager Outpost", List.of(Structure.PILLAGER_OUTPOST)),
    RUINED_PORTAL("Ruined Portal", List.of(Structure.RUINED_PORTAL_MOUNTAIN, Structure.RUINED_PORTAL_DESERT, Structure.RUINED_PORTAL_JUNGLE, Structure.RUINED_PORTAL_OCEAN, Structure.RUINED_PORTAL_SWAMP, Structure.RUINED_PORTAL_NETHER)),
    OCEAN_RUIN("Ocean Ruin", List.of(Structure.OCEAN_RUIN_COLD, Structure.OCEAN_RUIN_WARM)),
    TRIAL_CHAMBERS("Trial Chambers", List.of(Structure.TRIAL_CHAMBERS)),
    SHIPWRECK("Shipwreck", List.of(Structure.SHIPWRECK, Structure.SHIPWRECK_BEACHED)),
    TRAIL_RUINS("Trail Ruins", List.of(Structure.TRAIL_RUINS)),
    MANSION("Mansion", List.of(Structure.MANSION)),
    JUNGLE_PYRAMID("Jungle Pyramid", List.of(Structure.JUNGLE_PYRAMID)),
    MINESHAFT("Mineshaft", List.of(Structure.MINESHAFT, Structure.MINESHAFT_MESA)),
    SWAMP_HUT("Swamp Hut", List.of(Structure.SWAMP_HUT)),
    END_CITY("End City", List.of(Structure.END_CITY)),
    ANCIENT_CITY("Ancient City", List.of(Structure.ANCIENT_CITY)),
    DESERT_PYRAMID("Desert Pyramid", List.of(Structure.DESERT_PYRAMID)),
    MONUMENT("Monument", List.of(Structure.MONUMENT)),
    IGLOO("Igloo", List.of(Structure.IGLOO)),
    BASTION_REMNANT("Bastion Remnant", List.of(Structure.BASTION_REMNANT)),
    FORTRESS("Fortress", List.of(Structure.FORTRESS)),
    STRONGHOLD("Stronghold", List.of(Structure.STRONGHOLD));

    private final String name;
    private final List<Structure> structures;

    StructureSimplified(String name, List<Structure> structures) {
        this.name = name;
        this.structures = structures;
    }
}
