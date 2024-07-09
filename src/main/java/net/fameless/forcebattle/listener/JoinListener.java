package net.fameless.forcebattle.listener;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.BackpackCommand;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {

    public static boolean isUpdated = true;
    private final List<UUID> receivedSkip = new ArrayList<>();
    private final List<UUID> receivedSwap = new ArrayList<>();
    private final ForceBattlePlugin forceBattlePlugin;
    private final ObjectiveManager objectiveManager;
    private final NametagManager nametagManager;

    public JoinListener(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
        this.objectiveManager = forceBattlePlugin.getObjectiveManager();
        this.nametagManager = forceBattlePlugin.getNametagManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        nametagManager.removeTag(event.getPlayer());
        Team team = TeamManager.getTeam(event.getPlayer());
        if (team != null) {
            team.removePlayer(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Allow resource packs to hide the bossbar!");
            return;
        }
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            event.getPlayer().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Failed to download resource pack!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        if (!forceBattlePlugin.getPointsManager().getPointsMap().containsKey(player.getUniqueId())) {
            forceBattlePlugin.getPointsManager().getPointsMap().put(player.getUniqueId(), 0);
        }
        if (!objectiveManager.getFinishedObjectives().containsKey(player.getUniqueId())) {
            objectiveManager.getFinishedObjectives().put(player.getUniqueId(), new ArrayList<>());
        }
        if (!objectiveManager.getObjectiveTimeMap().containsKey(player.getUniqueId())) {
            objectiveManager.getObjectiveTimeMap().put(player.getUniqueId(), new ArrayList<>());
        }
        if (objectiveManager.getObjective(player) == null) {
            objectiveManager.updateObjective(player);
        }
        if (!BackpackCommand.backpackMap.containsKey(player.getUniqueId())) {
            BackpackCommand.backpackMap.put(player.getUniqueId(), Bukkit.createInventory(null, 27, "Backpack"));
        }

        forceBattlePlugin.getChainManager().addPlayer(player);

        if (!receivedSkip.contains(player.getUniqueId())) {
            int skipAmount = forceBattlePlugin.getConfig().getInt("jokers");
            objectiveManager.giveSkipItem(player, skipAmount);
            receivedSkip.add(player.getUniqueId());
        }
        if (!receivedSwap.contains(player.getUniqueId())) {
            int swapAmount = forceBattlePlugin.getConfig().getInt("swappers");
            objectiveManager.giveSwapItem(player, swapAmount);
            receivedSwap.add(player.getUniqueId());
        }

        if (player.isOp() && !isUpdated) {
            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "ForceBattle is not running on the latest version. " +
                    "Please keep ForceBattle up to date to avoid bugs: " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://www.spigotmc.org/resources/112328/");
        }

        forceBattlePlugin.getBossbarManager().createBossbar(player);
        nametagManager.newTag(player);
        nametagManager.setupNametag(player);
    }
}
