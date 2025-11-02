package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTeamsCommand extends Command {

    public RandomTeamsCommand() {
        super(
                "randomteams",
                List.of(),
                CallerType.PLAYER,
                "/randomteams <teamsize> <mode>",
                "forcebattle.randomteams",
                "Command to create random teams."
        );
    }

    @Override
    protected void executeCommand(final CommandCaller caller, final String[] args) {
        if (ForceBattle.getTimer().isRunning()) {
            caller.sendMessage(Caption.of("error.game_already_started"));
            return;
        }

        if (args.length == 0 || args[0].isEmpty()) {
            sendUsage(caller);
            return;
        }

        int size;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            caller.sendMessage(Caption.of("command.not_a_number"));
            return;
        }

        List<BattlePlayer> players = new ArrayList<>(BattlePlayer.getOnlinePlayers());
        int playerAmount = players.size();

        if (playerAmount % size != 0) {
            caller.sendMessage(Caption.of("error.no_equal_teams"));
            return;
        }

        int amountOfTeams = playerAmount / size;
        Random random = new Random();

        if (args.length > 1 && args[1].equalsIgnoreCase("fillExistingTeams")) {
            List<Team> existingTeams = new ArrayList<>(Team.teams);

            for (Team team : existingTeams) {
                for (BattlePlayer p : team.getPlayers()) {
                    players.remove(p);
                }
            }

            for (Team team : existingTeams) {
                while (team.getPlayers().size() > size) {
                    BattlePlayer removed = team.getPlayers().get(random.nextInt(team.getPlayers().size()));
                    team.removePlayer(removed);
                    players.add(removed);
                }
            }

            for (Team team : existingTeams) {
                while (team.getPlayers().size() < size && !players.isEmpty()) {
                    BattlePlayer randomPlayer = players.remove(random.nextInt(players.size()));
                    team.addPlayer(randomPlayer);
                }
            }

            while (!players.isEmpty()) {
                BattlePlayer first = players.remove(random.nextInt(players.size()));
                Team team = new Team(List.of(first));

                while (team.getPlayers().size() < size && !players.isEmpty()) {
                    BattlePlayer next = players.remove(random.nextInt(players.size()));
                    team.addPlayer(next);
                }
            }

        } else {
            Team.deleteAll();

            for (int i = 0; i < amountOfTeams; i++) {
                BattlePlayer first = players.remove(random.nextInt(players.size()));
                Team team = new Team(List.of(first));

                while (team.getPlayers().size() < size && !players.isEmpty()) {
                    BattlePlayer next = players.remove(random.nextInt(players.size()));
                    team.addPlayer(next);
                }
            }
        }

        caller.sendMessage(Caption.of("notification.randomteams_successfully_created",
                TagResolver.resolver("amount", Tag.inserting(Component.text(String.valueOf(amountOfTeams))))));
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        if (args.length == 2) {
            return StringUtility.copyPartialMatches(args[1], List.of("fillExistingTeams"), new ArrayList<>());
        }
        return List.of();
    }
}
