package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.StructureSearchResult;

import java.util.Collection;

public class StructureTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_STRUCTURE)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            String objectiveString = battlePlayer.getObjective().getObjectiveString();

            if (BukkitUtil.convertObjective(BattleType.FORCE_STRUCTURE, objectiveString)
                    instanceof net.fameless.forcebattle.game.data.Structure structEnum) {

                Registry<Structure> structureRegistry = Bukkit.getRegistry(Structure.class);

                Structure bukkitStruct = structureRegistry.get(structEnum.getKey());

                if (bukkitStruct == null) continue;

                StructureSearchResult result = player.getWorld()
                        .locateNearestStructure(player.getLocation(), bukkitStruct, 10, false);

                if (result != null) {
                    Location structLoc = result.getLocation();

                    GeneratedStructure generatedStructure = structLoc.getChunk().getStructures(bukkitStruct).stream().findFirst().get();
                    Collection<StructurePiece> structurePieces = generatedStructure.getPieces();

                    Location playerLocation = player.getLocation();
                    double playerX = playerLocation.getX();
                    double playerY = playerLocation.getY();
                    double playerZ = playerLocation.getZ();

                    for (StructurePiece piece : structurePieces) {
                        BoundingBox box = piece.getBoundingBox();
                        if (playerX <= box.getMaxX() && playerX >= box.getMinX() &&
                                playerY <= box.getMaxY() && playerY >= box.getMinY() &&
                                playerZ <= box.getMaxZ() && playerZ >= box.getMinZ()) {

                            battlePlayer.sendMessage(Caption.of(
                                    "notification.objective_finished",
                                    TagResolver.resolver("objective", Tag.inserting(Component.text(structEnum.getName())))
                            ));
                            Toast.display(
                                    player,
                                    Material.STRUCTURE_BLOCK,
                                    ChatColor.BLUE + structEnum.getName(),
                                    Toast.Style.GOAL,
                                    ForceBattle.get()
                            );
                            battlePlayer.updateObjective(true, false);
                        }
                        break;
                    }
                }
            }
        }
    }

}
