package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CoordsTask implements ForceTask {

    private static final double COMPLETION_RADIUS = 2.0;

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_COORDS)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            checkPlayerObjective(battlePlayer, player);
            checkTeamObjective(battlePlayer, player);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_COORDS) return;

        String objective = battlePlayer.getObjective().getObjectiveString();
        if (isAtCoordinates(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_COORDS) return;

        String objective = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isAtCoordinates(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isAtCoordinates(Player player, String objectiveString) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_COORDS, objectiveString);
        if (!(obj instanceof Location target)) return false;

        Location playerLoc = player.getLocation();
        Location targetLoc = new Location(player.getWorld(), target.getX(), playerLoc.getY(), target.getZ());
        double distance = playerLoc.distance(targetLoc);

        return distance <= COMPLETION_RADIUS;
    }

    private void completeObjective(BattlePlayer player, String objective) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_COORDS, objective);
        String coordsDisplay;

        if (obj instanceof Location loc) {
            coordsDisplay = String.format("%.0f, %.0f", loc.getX(), loc.getZ());
        } else {
            coordsDisplay = StringUtility.formatName(objective);
        }

        Material toastIcon = Material.COMPASS;
        ForceBattle plugin = ForceBattle.get();

        if (player.isInTeam() && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
            Component teammateMsg = Caption.of(
                    "notification.objective_finished_by_teammate",
                    TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(coordsDisplay)))
            );

            player.getTeam().getPlayers().stream()
                    .filter(teammate -> teammate != player)
                    .forEach(teammate -> {
                        teammate.sendMessage(teammateMsg);
                        Toast.display(teammate.getPlayer(), toastIcon, ChatColor.BLUE + coordsDisplay, Toast.Style.GOAL, plugin);
                    });
        }

        player.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(coordsDisplay)))
        ));
        Toast.display(player.getPlayer(), toastIcon, ChatColor.BLUE + coordsDisplay, Toast.Style.GOAL, plugin);
    }
}
