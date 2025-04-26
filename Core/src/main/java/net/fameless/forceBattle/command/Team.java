package net.fameless.forceBattle.command;

import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
import net.fameless.forceBattle.util.TabCompletions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Team extends Command {

    public Team() {
        super(
                "team",
                List.of(),
                CallerType.PLAYER,
                "/team <create|join|leave|list|accept|reject> <id>",
                "forcebattle.team",
                "Command to manage teams: create, join, etc."
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String[] args) {
        if (args.length < 1) {
            sendUsage(caller);
            return;
        }

        @NotNull Optional<BattlePlayer<?>> battlePlayerOpt = BattlePlayer.of(caller.getName());
        if (battlePlayerOpt.isEmpty()) {
            return;
        }
        BattlePlayer<?> battlePlayer = battlePlayerOpt.get();
        switch (args[0]) {
            case "create" -> {
                battlePlayer.leaveTeam();
                net.fameless.forceBattle.game.Team team = new net.fameless.forceBattle.game.Team(List.of(battlePlayer));
                caller.sendMessage(Caption.of(
                        "notification.team_registered",
                        TagResolver.resolver("team-id", Tag.inserting(Component.text(String.valueOf(team.getId()))))
                ));
            }
            case "join" -> {
                if (args.length < 2) {
                    caller.sendMessage(Caption.of("command.no_team_id"));
                    return;
                }

                int teamId;
                try {
                    teamId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    caller.sendMessage(Caption.of("command.no_team_id"));
                    return;
                }

                Optional<net.fameless.forceBattle.game.Team> teamOpt = net.fameless.forceBattle.game.Team.ofId(teamId);
                teamOpt.ifPresentOrElse(
                        team -> team.sendJoinRequest(battlePlayer),
                        () -> caller.sendMessage(Caption.of("command.no_team_id"))
                );
            }
            case "leave" -> battlePlayer.leaveTeam();
            case "accept" -> {
                net.fameless.forceBattle.game.Team team = battlePlayer.getTeam();
                if (team == null) {
                    caller.sendMessage(Caption.of("command.not_in_a_team"));
                    return;
                }
                if (team.getLastJoinRequest() != null) {
                    team.finishLastJoinRequest(true);
                }
            }
            case "reject" -> {
                net.fameless.forceBattle.game.Team team = battlePlayer.getTeam();
                if (team == null) {
                    caller.sendMessage(Caption.of("command.not_in_a_team"));
                    return;
                }
                if (team.getLastJoinRequest() != null) {
                    team.finishLastJoinRequest(false);
                }
            }
            case "list" -> {
                StringBuilder teamListString = new StringBuilder();
                teamListString.append("§9§lTeams:\n");
                for (net.fameless.forceBattle.game.Team team : net.fameless.forceBattle.game.Team.teams) {
                    teamListString.append("§e").append(team.getId()).append(":");
                    List<BattlePlayer<?>> teamPlayers = team.getPlayers();
                    for (int i = 0; i < teamPlayers.size(); i++) {
                        BattlePlayer<?> teamPlayer = teamPlayers.get(i);
                        if (teamPlayer == null || teamPlayer.isOffline()) {
                            continue;
                        }
                        teamListString.append("§9 ").append(teamPlayer.getName());
                        if (i < teamPlayers.size() - 1) {
                            teamListString.append(",");
                        }
                    }
                    teamListString.append("\n");
                }

                caller.sendMessage(Component.text(teamListString.toString()));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("create", "list", "join", "leave", "accept", "reject"), new ArrayList<>());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return StringUtil.copyPartialMatches(args[1], TabCompletions.getTeamIdTabCompletions(), new ArrayList<>());
        }
        return List.of();
    }

}
