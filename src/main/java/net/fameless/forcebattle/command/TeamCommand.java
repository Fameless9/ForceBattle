package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TeamCommand implements CommandExecutor {

    private final NametagManager nametagManager = ForceBattlePlugin.get().getNametagManager();
    private final BossbarManager bossbarManager = ForceBattlePlugin.get().getBossbarManager();
    public HashMap<Player, Team> inviteMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Only players can use this command.");
            return false;
        }
        if (args.length >= 1) {
            switch (args[0]) {
                case "join": {
                    if (args.length == 2) {
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team ID must be a number.");
                            return false;
                        }
                        if (TeamManager.getTeam(teamId) == null) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team does not exist.");
                            return false;
                        }
                        Team newTeam = TeamManager.getTeam(teamId);
                        if (newTeam != null && newTeam.isPrivate() && !player.isOp()) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team is private.");
                            return false;
                        }
                        Team playerTeam = TeamManager.getTeam(player);
                        if (playerTeam != null && newTeam != null && playerTeam.getId() == newTeam.getId()) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are already part of that team.");
                            return false;
                        }
                        if (playerTeam != null && newTeam != null) {
                            playerTeam.removePlayer(player);
                            newTeam.addPlayer(player);
                            nametagManager.updateNametag(player);
                            bossbarManager.updateBossbar(player);
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "Joined team " + newTeam.getId());
                        } else {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team does not exist.");
                        }
                    } else {
                        sendUsage(player);
                    }
                    break;
                }
                case "leave": {
                    Team team = TeamManager.getTeam(player);
                    if (team != null) {
                        team.removePlayer(player);
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Left team " + team.getId());
                        nametagManager.updateNametag(player);
                        bossbarManager.updateBossbar(player);
                    } else {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                    }
                    break;
                }
                case "delete": {
                    if (args.length == 2) {
                        if (!player.isOp()) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You need to be an operator to delete Teams you are not the leader of.");
                            return false;
                        }
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "ID must be number.");
                            return false;
                        }
                        if (TeamManager.getTeam(teamId) != null) {
                            TeamManager.removeTeam(teamId);
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "Successfully deleted Team " + teamId + ".");
                            nametagManager.updateNametag(player);
                            bossbarManager.updateBossbar(player);
                        } else {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team " + teamId + " couldn't be found.");
                        }
                    } else {
                        Team team = TeamManager.getTeam(player);
                        if (team != null) {
                            if (player.getUniqueId().equals(team.getLeader().getUniqueId()) || player.isOp()) {
                                TeamManager.removeTeam(team);
                            } else {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not the team leader.");
                            }
                        } else {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                        }
                    }
                    break;
                }
                case "create": {
                    if (TeamManager.getTeam(player) == null) {
                        TeamManager.registerTeam(new Team(Collections.singletonList(player), player));
                        nametagManager.updateNametag(player);
                        bossbarManager.updateBossbar(player);
                    } else {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are already in a team. /team leave");
                    }
                    break;
                }
                case "invite": {
                    if (TeamManager.getTeam(player) != null) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    Team team = TeamManager.getTeam(player);
                    if (team != null && !player.getUniqueId().equals(team.getLeader().getUniqueId()) && team.isPrivate()) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    if (args.length != 2) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Specify a player.");
                        return false;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player couldn't be found.");
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        inviteMap.put(target, team);
                        target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + player.getName() + " invited you to join their team. /team accept | decline");
                    }

                    break;
                }
                case "open": {
                    Team team = TeamManager.getTeam(player);
                    if (team == null) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    team.setPrivate(false);
                    player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "Team has been set to open.");
                    break;
                }
                case "close": {
                    Team team = TeamManager.getTeam(player);
                    if (team == null) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    team.setPrivate(true);
                    player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team has been set to closed.");
                    break;
                }
                case "decline": {
                    if (!inviteMap.containsKey(player)) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You have not been invited to a team.");
                        return false;
                    }
                    inviteMap.get(player).getLeader().sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + player.getName() + " has declined your invite.");
                    inviteMap.remove(player);
                    break;
                }
                case "accept": {
                    if (!inviteMap.containsKey(player)) {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You have not been invited to a team.");
                        return false;
                    }
                    inviteMap.get(player).addPlayer(player);
                    inviteMap.remove(player);
                    nametagManager.updateNametag(player);
                    bossbarManager.updateBossbar(player);
                    break;
                }
                case "list": {
                    player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Teams:");
                    for (Team team : TeamManager.getTeams()) {
                        List<String> playerNames = new ArrayList<>();
                        for (Player player1 : team.getPlayers()) {
                            playerNames.add(player1.getName());
                        }
                        player.sendMessage(ChatColor.GOLD + "ID: " + team.getId() + " | Players: " + playerNames);
                    }
                    break;
                }
                case "kick": {
                    if (args.length == 2) {
                        if (TeamManager.getTeam(player) == null) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not in a team.");
                            return false;
                        }
                        Team team = TeamManager.getTeam(player);
                        if (team != null && !player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not the team leader.");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if (team != null && target != null) {
                            if (!team.getPlayers().contains(target)) {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player is not part of the team.");
                                return false;
                            }
                            team.removePlayer(target);
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "Kicked " + target.getName() + ".");
                            target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You have been kicked from your team.");
                            nametagManager.updateNametag(target);
                            bossbarManager.updateBossbar(target);
                        } else {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player couldn't be found.");
                        }
                    } else if (args.length == 3) {
                        if (!player.isOp()) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You need to be an operator to kick players outside of your team.");
                        }
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team Id must be a number.");
                            return false;
                        }
                        if (Bukkit.getPlayer(args[2]) != null) {
                            if (TeamManager.getTeam(teamId) == null) {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Team does not exist.");
                                return false;
                            }
                            Team team = TeamManager.getTeam(teamId);
                            Player target = Bukkit.getPlayer(args[2]);
                            if (target != null && team != null && team.getPlayers().contains(target)) {
                                team.removePlayer(target);
                                target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "An operator has kicked you from your team.");
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "Player has been kicked.");
                                nametagManager.updateNametag(target);
                                bossbarManager.updateBossbar(target);
                            } else {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player is not part of the team.");
                            }
                        } else {
                            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player couldn't be found.");
                            return false;
                        }
                    } else {
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Usage: /team kick <player> | OP: /team kick <team> <player>");
                    }
                    break;
                }
                default: {
                    sendUsage(player);
                    break;
                }
            }
        } else {
            sendUsage(player);
        }
        return false;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Usage: /team <list, join, leave, delete, create, invite, open, close, accept, decline, kick> <id, player> <player>");
    }
}