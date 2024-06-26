package net.fameless.forcebattle.listener;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.BackpackCommand;
import net.fameless.forcebattle.manager.*;
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
    private final List<UUID> receivedSkip;
    private final List<UUID> receivedSwap;

    public JoinListener() {
        this.receivedSkip = new ArrayList<>();
        this.receivedSwap = new ArrayList<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        NametagManager.removeTag(event.getPlayer());
        Team team = TeamManager.getTeam(event.getPlayer());
        if (team != null) {
            team.removePlayer(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Allow resource packs to hide the bossbar!");
            return;
        }
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Failed to download resource pack!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        if (!PointsManager.pointsMap.containsKey(player.getUniqueId())) {
            PointsManager.pointsMap.put(player.getUniqueId(), 0);
        }
        if (!ObjectiveManager.finishedObjectives.containsKey(player.getUniqueId())) {
            ObjectiveManager.finishedObjectives.put(player.getUniqueId(), new ArrayList<>());
        }
        if (!ObjectiveManager.objectiveTimeMap.containsKey(player.getUniqueId())) {
            ObjectiveManager.objectiveTimeMap.put(player.getUniqueId(), new ArrayList<>());
        }
        if (ObjectiveManager.getChallenge(player) == null) {
            ObjectiveManager.updateObjective(player);
        }
        if (!BackpackCommand.backpackMap.containsKey(player.getUniqueId())) {
            BackpackCommand.backpackMap.put(player.getUniqueId(), Bukkit.createInventory(null, 27, ChatColor.GOLD + "Backpack"));
        }

        ChainManager.addPlayer(player);

        if (!receivedSkip.contains(player.getUniqueId())) {
            int skipAmount = ForceBattlePlugin.getInstance().getConfig().getInt("jokers");
            ObjectiveManager.giveSkipItem(player, skipAmount);
            receivedSkip.add(player.getUniqueId());
        }
        if (!receivedSwap.contains(player.getUniqueId())) {
            int swapAmount = ForceBattlePlugin.getInstance().getConfig().getInt("swappers");
            ObjectiveManager.giveSwapItem(player, swapAmount);
            receivedSwap.add(player.getUniqueId());
        }

        if (player.isOp() && !isUpdated) {
            player.sendMessage(ChatColor.GREEN + "There is an update available for ForceBattle. You can download it from the spigot website.");
        }

        BossbarManager.createBossbar(player);
        NametagManager.setupNametag(player);
        NametagManager.newTag(player);
    }
}
