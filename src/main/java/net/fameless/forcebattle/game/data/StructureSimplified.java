package net.fameless.forcebattle.game.data;

import lombok.Getter;

import java.util.List;

@Getter
public enum StructureSimplified {
    VILLAGE("Village", List.of(FBStructure.VILLAGE_PLAINS, FBStructure.VILLAGE_SAVANNA, FBStructure.VILLAGE_SNOWY, FBStructure.VILLAGE_TAIGA, FBStructure.VILLAGE_DESERT)),
    PILLAGER_OUTPOST("Pillager Outpost", List.of(FBStructure.PILLAGER_OUTPOST)),
    RUINED_PORTAL("Ruined Portal", List.of(FBStructure.RUINED_PORTAL_MOUNTAIN, FBStructure.RUINED_PORTAL_DESERT, FBStructure.RUINED_PORTAL_JUNGLE,
            FBStructure.RUINED_PORTAL_OCEAN, FBStructure.RUINED_PORTAL_SWAMP, FBStructure.RUINED_PORTAL_NETHER)),
    OCEAN_RUIN("Ocean Ruin", List.of(FBStructure.OCEAN_RUIN_COLD, FBStructure.OCEAN_RUIN_WARM)),
    TRIAL_CHAMBERS("Trial Chambers", List.of(FBStructure.TRIAL_CHAMBERS)),
    SHIPWRECK("Shipwreck", List.of(FBStructure.SHIPWRECK, FBStructure.SHIPWRECK_BEACHED)),
    TRAIL_RUINS("Trail Ruins", List.of(FBStructure.TRAIL_RUINS)),
    MANSION("Mansion", List.of(FBStructure.MANSION)),
    JUNGLE_PYRAMID("Jungle Pyramid", List.of(FBStructure.JUNGLE_PYRAMID)),
    MINESHAFT("Mineshaft", List.of(FBStructure.MINESHAFT, FBStructure.MINESHAFT_MESA)),
    SWAMP_HUT("Swamp Hut", List.of(FBStructure.SWAMP_HUT)),
    END_CITY("End City", List.of(FBStructure.END_CITY)),
    ANCIENT_CITY("Ancient City", List.of(FBStructure.ANCIENT_CITY)),
    DESERT_PYRAMID("Desert Pyramid", List.of(FBStructure.DESERT_PYRAMID)),
    MONUMENT("Monument", List.of(FBStructure.MONUMENT)),
    IGLOO("Igloo", List.of(FBStructure.IGLOO)),
    BASTION_REMNANT("Bastion Remnant", List.of(FBStructure.BASTION_REMNANT)),
    FORTRESS("Fortress", List.of(FBStructure.FORTRESS)),
    STRONGHOLD("Stronghold", List.of(FBStructure.STRONGHOLD));

    private final String name;
    private final List<FBStructure> structures;

    StructureSimplified(String name, List<FBStructure> structures) {
        this.name = name;
        this.structures = structures;
    }
}
