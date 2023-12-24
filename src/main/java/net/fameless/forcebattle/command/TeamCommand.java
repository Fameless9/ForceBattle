package net.fameless.forcebattle.command;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TeamCommand implements CommandExecutor {

    public HashMap<Player, Team> inviteMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        final Player player = (Player) sender;
        if (args.length >= 1) {
            switch (args[0]) {
                case "join": {
                    if (args.length == 2) {
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Team ID must be a number.");
                            return false;
                        }
                        if (TeamManager.getTeam(teamId) == null) {
                            player.sendMessage(ChatColor.RED + "Team does not exist.");
                            return false;
                        }
                        Team newTeam = TeamManager.getTeam(teamId);
                        if (newTeam.isPrivate() && !player.isOp()) {
                            player.sendMessage(ChatColor.RED + "Team is private.");
                            return false;
                        }
                        if (TeamManager.getTeam(player) != null && TeamManager.getTeam(player).getId() == (newTeam.getId())) {
                            player.sendMessage(ChatColor.RED + "You are already part of that team.");
                            return false;
                        }
                        if (TeamManager.getTeam(player) != null) {
                            TeamManager.getTeam(player).removePlayer(player);
                        }
                        newTeam.addPlayer(player);
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                        player.sendMessage(ChatColor.GREEN + "Joined team " + newTeam.getId());
                    } else {
                        sendUsage(player);
                    }
                    break;
                }
                case "leave": {
                    if (TeamManager.getTeam(player) != null) {
                        Team team = TeamManager.getTeam(player);
                        team.removePlayer(player);
                        player.sendMessage(ChatColor.RED + "Left team " + team.getId());
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not in a team.");
                    }
                    break;
                }
                case "delete": {
                    if (args.length == 2) {
                        if (!player.isOp()) {
                            player.sendMessage(ChatColor.RED + "You need to be an operator to delete Teams you are not the leader of.");
                            return false;
                        }
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e){
                            player.sendMessage(ChatColor.RED + "ID must be number.");
                            return false;
                        }
                        if (TeamManager.getTeam(teamId) != null) {
                            TeamManager.removeTeam(teamId);
                            player.sendMessage(ChatColor.GREEN + "Successfully deleted Team " + teamId + ".");
                            NametagManager.updateNametag(player);
                            BossbarManager.updateBossbar(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Team " + teamId +  " couldn't be found.");
                        }
                    } else {
                        if (TeamManager.getTeam(player) != null) {
                            Team team = TeamManager.getTeam(player);
                            if (player.getUniqueId().equals(team.getLeader().getUniqueId()) || player.isOp()) {
                                TeamManager.removeTeam(team);
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not the team leader.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You are not in a team.");
                        }
                    }
                    break;
                }
                case "create": {
                    if (TeamManager.getTeam(player) == null) {
                        TeamManager.registerTeam(new Team(Collections.singletonList(player), player));
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You are already in a team. /team leave");
                    }
                    break;
                }
                case "invite": {
                    if (TeamManager.getTeam(player) != null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    Team team = TeamManager.getTeam(player);
                    if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && team.isPrivate()) {
                        player.sendMessage(ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    if (args.length != 2) {
                        player.sendMessage(ChatColor.RED + "Specify a player.");
                        return false;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        player.sendMessage(ChatColor.RED + "Player couldn't be found.");
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    inviteMap.put(target, team);
                    target.sendMessage(ChatColor.GOLD + player.getName() + " invited you to join their team. /team accept | decline");

                    break;
                }
                case "open": {
                    if (TeamManager.getTeam(player) == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    Team team = TeamManager.getTeam(player);
                    if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    team.setPrivate(false);
                    player.sendMessage(ChatColor.GREEN + "Team has been set to open.");
                    break;
                }
                case "close": {
                    if (TeamManager.getTeam(player) == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team.");
                        return false;
                    }
                    Team team = TeamManager.getTeam(player);
                    if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You are not the team leader.");
                        return false;
                    }
                    team.setPrivate(true);
                    player.sendMessage(ChatColor.RED + "Team has been set to closed.");
                    break;
                }
                case "decline": {
                    if (!inviteMap.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "You have not been invited to a team.");
                        return false;
                    }
                    inviteMap.get(player).getLeader().sendMessage(ChatColor.RED + player.getName() + " has declined your invite.");
                    inviteMap.remove(player);
                    break;
                }
                case "accept": {
                    if (!inviteMap.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "You have not been invited to a team.");
                        return false;
                    }
                    inviteMap.get(player).addPlayer(player);
                    inviteMap.remove(player);
                    NametagManager.updateNametag(player);
                    BossbarManager.updateBossbar(player);
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
                            player.sendMessage(ChatColor.RED + "You are not in a team.");
                            return false;
                        }
                        Team team = TeamManager.getTeam(player);
                        if (!player.getUniqueId().equals(team.getLeader().getUniqueId()) && !player.isOp()) {
                            player.sendMessage(ChatColor.RED + "You are not the team leader.");
                            return false;
                        }
                        if (Bukkit.getPlayer(args[1]) != null) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (!team.getPlayers().contains(target)) {
                                player.sendMessage(ChatColor.RED + "Player is not part of the team.");
                                return false;
                            }
                            team.removePlayer(target);
                            player.sendMessage(ChatColor.GREEN + "Kicked " + target.getName() + ".");
                            target.sendMessage(ChatColor.RED + "You have been kicked from your team.");
                            NametagManager.updateNametag(target);
                            BossbarManager.updateBossbar(target);
                        } else {
                            player.sendMessage(ChatColor.RED + "Player couldn't be found.");
                        }
                    } else if (args.length == 3) {
                        if (!player.isOp()) {
                            player.sendMessage(ChatColor.RED + "You need to be an operator to kick players outside of your team.");
                        }
                        int teamId;
                        try {
                            teamId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Team Id must be a number.");
                            return false;
                        }
                        if (Bukkit.getPlayer(args[2]) != null) {
                            if (TeamManager.getTeam(teamId) == null) {
                                player.sendMessage(ChatColor.RED + "Team couldn't be found.");
                                return false;
                            }
                            Team team = TeamManager.getTeam(teamId);
                            Player target = Bukkit.getPlayer(args[2]);
                            if (team.getPlayers().contains(target)) {
                                team.removePlayer(target);
                                target.sendMessage(ChatColor.RED + "An operator has kicked you from your team.");
                                player.sendMessage(ChatColor.GREEN + "Player has been kicked.");
                                NametagManager.updateNametag(target);
                                BossbarManager.updateBossbar(target);
                            } else {
                                player.sendMessage(ChatColor.RED + "Player is not part of the team.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Player couldn't been found.");
                            return false;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage of /team kick: /team kick <player> | OP: /team kick <team> <player>");
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
        player.sendMessage(ChatColor.GOLD + "Usage: /team <list, join, leave, delete, create, invite, open, close, accept, decline, kick> <id, player> <player>");
    }
}