package net.fameless.forcebattle.tablist;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.Format;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TablistManager {

    private static final ForceBattle plugin = ForceBattle.get();

    public static void startUpdating() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAllTablists();
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public static void updateAllTablists() {
        BattlePlayer.getOnlinePlayers().forEach(player -> {
            if (player.isInTeam()) {
                player.getPlayer().setPlayerListOrder(player.getTeam().getId());
            } else {
                player.getPlayer().setPlayerListOrder(999);
            }

            updateTablist(player.getPlayer());
        });
    }

    public static void updateTablist(Player player) {
        /*
        MiniMessage miniMessage = MiniMessage.miniMessage();

        String header = miniMessage.serialize(
                Component.text("⏳ Time Left: " + Format.formatTime(ForceBattle.getTimer().getTime()))
        );

        StringBuilder footerBuilder = new StringBuilder();

        // Show all teams first
        List<Team> teams = Team.teams;
        for (Team team : teams) {
            footerBuilder.append("Team ").append(team.getId()).append(":\n");
            for (BattlePlayer battlePlayer : team.getPlayers()) {
                footerBuilder.append(formatPlayerLineString(battlePlayer)).append("\n");
            }
        }

        // Show any players not in a team
        for (Player p : Bukkit.getOnlinePlayers()) {
            BattlePlayer bp = BattlePlayer.adapt(p);
            if (!bp.isInTeam()) {
                footerBuilder.append("Solo:\n");
                footerBuilder.append(formatPlayerLineString(bp)).append("\n");
            }
        }

        player.setPlayerListHeaderFooter(header, footerBuilder.toString());
         */
    }

    private static String formatPlayerLineString(BattlePlayer player) {
        String name = player.getName();
        String objective = player.getObjective() != null
                ? Format.formatName(player.getObjective().getObjectiveString())
                : "None";

        String color = "§f"; // white by default
        if (player.getObjective() != null) {
            BattleType type = player.getObjective().getBattleType();
            color = switch (type) {
                case FORCE_ITEM -> "§a";        // green
                case FORCE_MOB -> "§c";         // red
                case FORCE_BIOME -> "§e";       // yellow
                case FORCE_ADVANCEMENT -> "§d"; // light purple
                case FORCE_HEIGHT, FORCE_COORDS -> "§7"; // gray
                case FORCE_STRUCTURE -> "§b";   // aqua
            };
        }

        return color + " - " + name + " | " + objective;
    }
}
