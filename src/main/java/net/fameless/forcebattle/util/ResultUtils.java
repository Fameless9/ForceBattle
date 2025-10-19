package net.fameless.forcebattle.util;

import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ResultUtils {

    public static Component buildPlayerResultMessage(BattlePlayer player) {
        int place = BattlePlayer.getPlace(player);

        return Component.text("Results → ")
                .append(Component.text(player.getName()).color(NamedTextColor.GOLD))
                .append(Component.text(" | Place: " + place + " | Points: " + player.getPoints())
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(" [Open Results]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/result player " + player.getName()))
                );
    }

    public static Component buildTeamResultMessage(Team team) {
        int place = Team.getPlace(team);
        String players = team.getPlayers().stream()
                .sorted(Comparator.comparingInt(BattlePlayer::getPoints).reversed())
                .map(p -> p.getName() + " (" + p.getPoints() + ")")
                .collect(Collectors.joining(", "));

        return Component.text("Results → ")
                .append(Component.text("Team " + team.getId()).color(NamedTextColor.GOLD))
                .append(Component.text(" [" + players + "] ")).color(NamedTextColor.GRAY)
                .append(Component.text(" | Place: " + place + " | Points: " + team.getPoints())
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(" [Open Results]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/result team " + team.getId())));
    }
}
