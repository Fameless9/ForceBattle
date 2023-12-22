package net.fameless.forcebattle.timer;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.ItemManager;
import net.fameless.forcebattle.manager.LeaderboardManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.util.FormatTime;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer implements CommandExecutor {
    private static int startTime;
    private static int time;
    private static boolean running;

    public Timer() {
        run();
    }

    public static int getStartTime() {
        return startTime;
    }

    public static void setStartTime(int newStartTime) {
        startTime = newStartTime;
        ForceBattle.getInstance().getConfig().set("challenge_duration", startTime);
        ForceBattle.getInstance().saveConfig();
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
        if (ForceBattle.getInstance().getConfig().get("time") != null) {
            setStartTime(ForceBattle.getInstance().getConfig().getInt("time"));
            setTime(getStartTime());
        } else {
            setStartTime(5400);
            setTime(getStartTime());
        }
        new BukkitRunnable() {
            public void run() {
                sendActionbar();
                if (time == 0) {
                    setRunning(false);
                    setTime(startTime);
                    LeaderboardManager.displayLeaderboard();
                    return;
                }

                if (isRunning()) {
                    setTime(getTime() - 1);
                }
            }
        }.runTaskTimer(ForceBattle.getInstance(), 0L, 20L);
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
        if (ItemManager.activeChallenges.isEmpty()) {
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
                NametagManager.updateNametag(players);
                BossbarManager.updateBossbar(players);
            }
            Bukkit.broadcastMessage(ChatColor.GREEN + "Timer has been started.");
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
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
                player.openInventory(TimerUI.getTimerUI());
            }
        }
        return false;
    }
}