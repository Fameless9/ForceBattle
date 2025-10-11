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

            checkPlayerObjective(battlePlayer, player, registry);
            checkTeamObjective(battlePlayer, player, registry);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player, Registry<org.bukkit.generator.structure.Structure> registry) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_STRUCTURE) return;

        String objectiveString = battlePlayer.getObjective().getObjectiveString();
        if (isInsideStructure(player, objectiveString, registry)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player, Registry<org.bukkit.generator.structure.Structure> registry) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_STRUCTURE) return;

        String objectiveString = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isInsideStructure(player, objectiveString, registry)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isInsideStructure(Player player, String objectiveString, Registry<org.bukkit.generator.structure.Structure> registry) {
        Object parsed = BukkitUtil.convertObjective(BattleType.FORCE_STRUCTURE, objectiveString);

        if (parsed instanceof StructureSimplified simplified) {
            for (Structure structure : simplified.getStructures()) {
                org.bukkit.generator.structure.Structure bukkitStruct = registry.get(structure.getKey());
                if (bukkitStruct != null && checkPlayerLocationInStructure(player, bukkitStruct)) {
                    return true;
                }
            }
            return false;
        }

        if (parsed instanceof Structure structure) {
            org.bukkit.generator.structure.Structure bukkitStruct = registry.get(structure.getKey());
            return bukkitStruct != null && checkPlayerLocationInStructure(player, bukkitStruct);
        }

        return false;
    }

    private boolean checkPlayerLocationInStructure(Player player, org.bukkit.generator.structure.Structure bukkitStruct) {
        StructureSearchResult result = player.getWorld().locateNearestStructure(player.getLocation(), bukkitStruct, 10, false);
        if (result == null) return false;

        Location loc = result.getLocation();
        Optional<GeneratedStructure> generated = loc.getChunk().getStructures(bukkitStruct).stream().findFirst();
        if (generated.isEmpty()) return false;

        Location playerLoc = player.getLocation();
        for (StructurePiece piece : generated.get().getPieces()) {
            if (isInsideBoundingBox(playerLoc, piece.getBoundingBox())) return true;
        }
        return false;
    }

    private boolean isInsideBoundingBox(Location loc, BoundingBox box) {
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x >= box.getMinX() && x <= box.getMaxX()
                && y >= box.getMinY() && y <= box.getMaxY()
                && z >= box.getMinZ() && z <= box.getMaxZ();
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = Format.formatName(objective);
        Material toastIcon = Material.STRUCTURE_BLOCK;
        ForceBattle plugin = ForceBattle.get();

        if (player.isInTeam() && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
            Component teammateMsg = Caption.of(
                    "notification.objective_finished_by_teammate",
                    TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
            );

            player.getTeam().getPlayers().stream()
                    .filter(teammate -> teammate != player)
                    .forEach(teammate -> {
                        teammate.sendMessage(teammateMsg);
                        Toast.display(teammate.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
                    });
        }

        player.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
        ));
        Toast.display(player.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
    }
}
