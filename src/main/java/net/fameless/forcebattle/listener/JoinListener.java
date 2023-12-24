package net.fameless.forcebattle.listener;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.command.BackpackCommand;
import net.fameless.forcebattle.manager.*;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.util.ItemProvider;
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

public class JoinListener implements Listener {

    List<Player> receivedSkip = new ArrayList<>();
    List<Player> receivedSwap = new ArrayList<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        NametagManager.removeTag(event.getPlayer());
        if (TeamManager.getTeam(event.getPlayer()) != null) {
            TeamManager.getTeam(event.getPlayer()).removePlayer(event.getPlayer());
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
        event.getPlayer().setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        if (!PointsManager.pointsMap.containsKey(event.getPlayer())) {
            PointsManager.pointsMap.put(event.getPlayer(), 0);
        }
        if (!ItemManager.finishedObjectives.containsKey(event.getPlayer())) {
            ItemManager.finishedObjectives.put(event.getPlayer(), new ArrayList<>());
        }
        if (!ItemManager.objectiveTimeMap.containsKey(event.getPlayer())) {
            ItemManager.objectiveTimeMap.put(event.getPlayer(), new ArrayList<>());
        }
        if (ItemManager.getChallenge(event.getPlayer()) == null) {
            ItemManager.updateObjective(event.getPlayer());
        }
        if (!BackpackCommand.backpackMap.containsKey(event.getPlayer())) {
            BackpackCommand.backpackMap.put(event.getPlayer(), Bukkit.createInventory(null, 27, ChatColor.GOLD + "Backpack"));
        }

        ChainManager.addPlayer(event.getPlayer());

        if (!receivedSkip.contains(event.getPlayer())) {
            int skipAmount = ForceBattle.getInstance().getConfig().getInt("jokers");
            event.getPlayer().getInventory().addItem(ItemProvider.getSkipItem(skipAmount));
            receivedSkip.add(event.getPlayer());
        }
        if (!receivedSwap.contains(event.getPlayer())) {
            int skipAmount = ForceBattle.getInstance().getConfig().getInt("swappers");
            event.getPlayer().getInventory().addItem(ItemProvider.getSwapitem(skipAmount));
            receivedSwap.add(event.getPlayer());
        }

        BossbarManager.createBossbar(event.getPlayer());
        NametagManager.setupNametag(event.getPlayer());
        NametagManager.newTag(event.getPlayer());
    }
}
