package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.Structure;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.StructureSearchResult;

import java.util.Optional;

public class StructureTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        Registry<org.bukkit.generator.structure.Structure> registry = Bukkit.getRegistry(org.bukkit.generator.structure.Structure.class);
        if (registry == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;
            if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_STRUCTURE) continue;

            String objectiveString = battlePlayer.getObjective().getObjectiveString();

            if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                handleSimplifiedObjective(player, battlePlayer, registry, objectiveString);
            } else {
                handleNormalObjective(player, battlePlayer, registry, objectiveString);
            }
        }
    }

    private void handleSimplifiedObjective(Player player, BattlePlayer battlePlayer, Registry<org.bukkit.generator.structure.Structure> registry, String objectiveString) {
        for (StructureSimplified simplified : StructureSimplified.values()) {
            if (simplified.getName().equalsIgnoreCase(objectiveString)) {
                for (Structure structure : simplified.getStructures()) {
                    org.bukkit.generator.structure.Structure bukkitStruct = registry.get(structure.getKey());
                    if (bukkitStruct == null) continue;

                    if (isPlayerInsideStructure(player, bukkitStruct)) {
                        sendCompletionMessage(battlePlayer, player, simplified.getName());
                        battlePlayer.updateObjective(true, false);
                        return;
                    }
                }
            }
        }
    }

    private void handleNormalObjective(Player player, BattlePlayer battlePlayer, Registry<org.bukkit.generator.structure.Structure> registry, String objectiveString) {
        Object converted = BukkitUtil.convertObjective(BattleType.FORCE_STRUCTURE, objectiveString);
        if (!(converted instanceof Structure structure)) return;

        org.bukkit.generator.structure.Structure bukkitStruct = registry.get(structure.getKey());
        if (bukkitStruct == null) return;

        if (isPlayerInsideStructure(player, bukkitStruct)) {
            sendCompletionMessage(battlePlayer, player, structure.getName());
            battlePlayer.updateObjective(true, false);
        }
    }

    private boolean isPlayerInsideStructure(Player player, org.bukkit.generator.structure.Structure bukkitStruct) {
        StructureSearchResult result = player.getWorld().locateNearestStructure(player.getLocation(), bukkitStruct, 10, false);
        if (result == null) return false;

        Location loc = result.getLocation();
        Optional<GeneratedStructure> generated = loc.getChunk().getStructures(bukkitStruct).stream().findFirst();
        if (generated.isEmpty()) return false;

        Location playerLoc = player.getLocation();
        for (StructurePiece piece : generated.get().getPieces()) {
            if (isInside(playerLoc, piece.getBoundingBox())) return true;
        }
        return false;
    }

    private boolean isInside(Location loc, BoundingBox box) {
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x <= box.getMaxX() && x >= box.getMinX()
                && y <= box.getMaxY() && y >= box.getMinY()
                && z <= box.getMaxZ() && z >= box.getMinZ();
    }

    private void sendCompletionMessage(BattlePlayer battlePlayer, Player player, String structureName) {
        String formatted = Format.formatName(structureName);
        battlePlayer.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(formatted)))
        ));
        Toast.display(
                player,
                Material.STRUCTURE_BLOCK,
                ChatColor.BLUE + formatted,
                Toast.Style.GOAL,
                ForceBattle.get()
        );
    }
}
