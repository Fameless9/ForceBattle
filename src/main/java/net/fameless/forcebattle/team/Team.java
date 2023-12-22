package net.fameless.forcebattle.team;

import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final int id;
    private final List<Player> players;
    private boolean isPrivate = true;
    private Player leader;
    private int points;
    public final Inventory backpack;

    public Team(List<Player> players, Player leader) {
        this.id = TeamManager.getTeams().size() + 1;
        this.players = new ArrayList<>(players);
        this.leader = leader;
        this.points = 0;
        this.backpack = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Team backpack | " + id);
    }

    public Player getLeader() {
        return leader;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void addPlayer(Player player) {
        for (Player player1 : players) {
            player1.sendMessage(ChatColor.GOLD + player.getName() + " joined your team.");
        }
        if (players.isEmpty()) {
            this.leader = player;
            player.sendMessage(ChatColor.GOLD + "Joined team as a leader.");
        }
        players.add(player);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.removeIf(player1 -> player1.getUniqueId().equals(player.getUniqueId()));
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
            for (Player player1 : players) {
                player1.sendMessage(ChatColor.GOLD + player.getName() + " left your team.");
            }
            if (getLeader().equals(player)) {
                if (!getPlayers().isEmpty()) {
                    Player newLeader = players.get(0);
                    this.leader = newLeader;

                    for (Player player1 : players) {
                        player1.sendMessage(ChatColor.GOLD + newLeader.getName() + " is now the leader of your team.");
                    }
                }
            }
        }
    }

    public void setPrivate(boolean b) {
        this.isPrivate = b;

        if (b) {
            for (Player player : players) {
                player.sendMessage(ChatColor.GOLD + "Your party is now open.");
            }
        } else {
            for (Player player : players) {
                player.sendMessage(ChatColor.GOLD + "Your party is now private.");
            }
        }
    }

    public boolean isPrivate() { return isPrivate; }

    public List<Player> getPlayers() { return players; }

    public int getId() { return id; }
}