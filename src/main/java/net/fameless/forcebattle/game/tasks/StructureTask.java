package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.FBStructure;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StructureTask implements ForceTask {

    private static final Map<String, Set<BoundingBox>> STRUCTURE_CACHE = new ConcurrentHashMap<>();

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        Registry<Structure> registry = Bukkit.getRegistry(Structure.class);
        if (registry == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            checkPlayerObjective(battlePlayer, player, registry);
            checkTeamObjective(battlePlayer, player, registry);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player, Registry<Structure> registry) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_STRUCTURE) return;

        String objectiveString = battlePlayer.getObjective().getObjectiveString();
        if (isInsideStructure(player, objectiveString, registry)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player, Registry<Structure> registry) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_STRUCTURE) return;

        String objectiveString = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isInsideStructure(player, objectiveString, registry)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isInsideStructure(Player player, String objectiveString, Registry<Structure> registry) {
        Object parsed = BukkitUtil.convertObjective(BattleType.FORCE_STRUCTURE, objectiveString);

        if (parsed instanceof StructureSimplified simplified) {
            for (FBStructure structure : simplified.getStructures()) {
                Structure bukkitStruct = registry.get(structure.getKey());
                if (bukkitStruct != null && checkPlayerLocationInStructure(player, bukkitStruct)) {
                    return true;
                }
            }
            return false;
        }

        if (parsed instanceof FBStructure structure) {
            Structure bukkitStruct = registry.get(structure.getKey());
            return bukkitStruct != null && checkPlayerLocationInStructure(player, bukkitStruct);
        }

        return false;
    }

    private boolean checkPlayerLocationInStructure(Player player, Structure bukkitStruct) {
        String worldName = player.getWorld().getName();
        String cacheKey = worldName + ":" + bukkitStruct.getKey().getKey();
        Set<BoundingBox> cachedBoxes = STRUCTURE_CACHE.get(cacheKey);
        Location playerLoc = player.getLocation();

        if (cachedBoxes != null) {
            for (BoundingBox box : cachedBoxes) {
                if (isInsideBoundingBox(playerLoc, box)) return true;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(ForceBattle.get(), () -> {
            try {
                World world = player.getWorld();
                Chunk center = playerLoc.getChunk();
                int radius = 6;

                Set<BoundingBox> newBoxes = new HashSet<>();
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Chunk chunk = world.getChunkAt(center.getX() + dx, center.getZ() + dz);
                        for (GeneratedStructure generated : chunk.getStructures(bukkitStruct)) {
                            for (StructurePiece piece : generated.getPieces()) {
                                newBoxes.add(piece.getBoundingBox());
                            }
                        }
                    }
                }

                if (!newBoxes.isEmpty()) {
                    STRUCTURE_CACHE.compute(cacheKey, (key, oldSet) -> {
                        if (oldSet == null) return newBoxes;
                        oldSet.addAll(newBoxes);
                        return oldSet;
                    });
                }
            } catch (Exception ignored) {}
        });

        return false;
    }

    private boolean isInsideBoundingBox(Location loc, BoundingBox box) {
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x >= box.getMinX() && x <= box.getMaxX()
                && y >= box.getMinY() && y <= box.getMaxY()
                && z >= box.getMinZ() && z <= box.getMaxZ();
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = StringUtility.formatName(objective);
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
