package net.fameless.forcebattle.timer;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.*;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.FormatTime;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Timer implements CommandExecutor {
    private static int startTime;
    private static int time;
    private static boolean running;
    private final TimerUI timerUI = new TimerUI();

    public Timer() {
        run();
    }

    public static int getStartTime() {
        return startTime;
    }

    public static void setStartTime(int newStartTime) {
        startTime = newStartTime;
        ForceBattlePlugin.getInstance().getConfig().set("time", startTime);
        ForceBattlePlugin.getInstance().saveConfig();
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int newTime) {
        time = newTime;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean newRunning) {
        running = newRunning;
    }

    public static void run() {
        if (ForceBattlePlugin.getInstance().getConfig().get("time") != null) {
            setStartTime(ForceBattlePlugin.getInstance().getConfig().getInt("time"));
            setTime(startTime);
        } else {
            setStartTime(5400);
            setTime(startTime);
        }
        Bukkit.getScheduler().runTaskTimer(ForceBattlePlugin.getInstance(), () -> {
            sendActionbar();
            if (time == 0) {
                setRunning(false);
                setTime(startTime);
                LeaderboardManager.displayLeaderboard();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    PointsManager.setPoints(player, 0);
                    ObjectiveManager.updateObjective(player);
                }

                for (Team team : TeamManager.getTeams()) {
                    team.updatePoints();
                }
                return;
            }

            if (isRunning()) {
                setTime(getTime() - 1);
            }
        }, 0, 20);
    }

    public static void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isRunning()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + ChatColor.ITALIC + FormatTime.toFormatted(time)));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + FormatTime.toFormatted(time)));
            }
        }
    }

    public static void toggle(Player player) {
        if (ObjectiveManager.activeChallenges.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "You need to select at least 1 challenge to start the timer. /menu");
            return;
        }
        if (isRunning()) {
            setRunning(false);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.RED + "Timer paused.", "", 20, 40, 20);
                NametagManager.updateNametag(players);
                BossbarManager.updateBossbar(players);
            }
            Bukkit.broadcastMessage(ChatColor.RED + "Timer has been paused.");
        } else {
            setRunning(true);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.RED + "Timer started.", "", 20, 40, 20);
                if (ObjectiveManager.getChallenge(players) instanceof Advancement advancement) {
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                    player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                    player.sendMessage(ChatColor.GOLD + advancement.getDescription());
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                }
                NametagManager.updateNametag(players);
                BossbarManager.updateBossbar(players);
            }
            Bukkit.broadcastMessage(ChatColor.GREEN + "Timer has been started.");
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("forcebattle.timer")) {
                player.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.timer'");
                return false;
            }
            if (args.length >= 1) {
                String s2 = args[0];
                switch (s2) {
                    case "toggle": {
                        toggle(player);
                        break;
                    }
                    case "set": {
                        if (args.length == 2) {
                            try {
                                int time = Integer.parseInt(args[1]);
                                setTime(time);
                                setStartTime(time);
                                player.sendMessage(ChatColor.GOLD + "Timer has been set to " + time + " seconds.");
                                sendActionbar();
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Time value must be an integer.");
                            }
                            break;
                        }
                        player.sendMessage(ChatColor.RED + "Usage: /timer set <time>");
                        break;
                    }
                    default: {
                        sender.sendMessage(ChatColor.RED + "Invalid usage! Please use: /timer toggle");
                        break;
                    }
                }
            } else {
                player.openInventory(timerUI.getTimerUI());
            }
        }
        return false;
    }
}