package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
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
                "/randomteams <teamsize>",
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

        if (args.length > 0 && !args[0].isEmpty()) {
            Team.deleteAll();

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

            Random random = new Random();
            int amountOfTeams = playerAmount / size;

            for (int i = 0; i < amountOfTeams; i++) {
                BattlePlayer player = players.get(random.nextInt(players.size()));
                players.remove(player);
                Team team = new Team(List.of(player));

                for (int j = 0; j < size; j++) {
                    if (team.getPlayers().size() == size) break;
                    BattlePlayer newPlayer = players.get(random.nextInt(players.size()));
                    team.addPlayer(newPlayer);
                    players.remove(newPlayer);
                }
            }

            caller.sendMessage(Caption.of("notification.randomteams_successfully_created",
                    TagResolver.resolver("amount", Tag.inserting(Component.text(String.valueOf(amountOfTeams))))));
        }
    }

    @Override
    protected List<String> tabComplete(final CommandCaller caller, final String[] args) {
        return List.of();
    }
}
