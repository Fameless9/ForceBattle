package net.fameless.forcebattle.timer;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.manager.LeaderboardManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Format;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Timer implements CommandExecutor {

    private final List<ChatColor> colorList = new ArrayList<>();
    private final List<ChatColor> decorationList = new ArrayList<>();
    private final TimerUI timerUI = new TimerUI(this);
    private final ForceBattlePlugin forceBattlePlugin;
    private final ObjectiveManager objectiveManager;
    
    private int startTime;
    private int time;
    private int colorProgress = 0;
    private int decorationProgress = 0;
    private boolean running;
    private ChatColor color = ChatColor.GOLD;
    private ChatColor decoration = null;
    
    public Timer(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
        this.objectiveManager = forceBattlePlugin.getObjectiveManager();

        colorList.add(ChatColor.BLUE);
        colorList.add(ChatColor.DARK_BLUE);
        colorList.add(ChatColor.GREEN);
        colorList.add(ChatColor.DARK_GREEN);
        colorList.add(ChatColor.RED);
        colorList.add(ChatColor.DARK_RED);
        colorList.add(ChatColor.WHITE);
        colorList.add(ChatColor.GRAY);
        colorList.add(ChatColor.DARK_GRAY);
        colorList.add(ChatColor.LIGHT_PURPLE);
        colorList.add(ChatColor.DARK_PURPLE);
        colorList.add(ChatColor.YELLOW);
        colorList.add(ChatColor.AQUA);
        colorList.add(ChatColor.DARK_AQUA);
        colorList.add(ChatColor.BLACK);
        colorList.add(ChatColor.GOLD);

        decorationList.add(ChatColor.BOLD);
        decorationList.add(ChatColor.ITALIC);
        decorationList.add(ChatColor.UNDERLINE);
        decorationList.add(ChatColor.STRIKETHROUGH);
        decorationList.add(null);

        run();
    }

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getDecoration() {
        return decoration;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int newStartTime) {
        startTime = newStartTime;
        forceBattlePlugin.getConfig().set("time", startTime);
        forceBattlePlugin.saveConfig();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int newTime) {
        time = newTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean newRunning) {
        running = newRunning;
    }

    public void cycleColors() {
        colorProgress += 1;
        if (colorProgress > colorList.size() - 1) {
            colorProgress = 0;
        }
        color = colorList.get(colorProgress);
        sendActionbar();
    }

    public void cycleDecorations() {
        decorationProgress += 1;
        if (decorationProgress > decorationList.size() - 1) {
            decorationProgress = 0;
        }
        decoration = decorationList.get(decorationProgress);
        sendActionbar();
    }

    private void run() {
        if (forceBattlePlugin.getConfig().get("time") != null) {
            setStartTime(forceBattlePlugin.getConfig().getInt("time"));
            setTime(startTime);
        } else {
            setStartTime(5400);
            setTime(startTime);
        }
        Bukkit.getScheduler().runTaskTimer(forceBattlePlugin, () -> {
            sendActionbar();
            if (time == 0) {
                setRunning(false);
                setTime(startTime);
                LeaderboardManager.displayLeaderboard();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    forceBattlePlugin.getPointsManager().setPoints(player, 0);
                    objectiveManager.updateObjective(player);
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

    public void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isRunning()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color.toString() + (decoration != null ? decoration : "") + Format.formatTime(time)));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color.toString() + (decoration != null ? decoration : "") + "Timer paused."));
            }
        }
    }

    public void toggle(Player player) {
        if (objectiveManager.getActiveChallenges().isEmpty()) {
            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Please select a challenge first. " + ChatColor.GOLD + "/menu");
            return;
        }
        if (isRunning()) {
            setRunning(false);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.GOLD + "TIMER PAUSED", "", 20, 30, 20);
                forceBattlePlugin.getNametagManager().updateNametag(players);
                forceBattlePlugin.getBossbarManager().updateBossbar(players);
            }
            Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Timer has been paused.");
        } else {
            setRunning(true);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.GOLD + "TIMER STARTED", "", 20, 30, 20);
                if (objectiveManager.getObjective(players) instanceof Advancement advancement) {
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                    player.sendMessage(ChatColor.GOLD + "Advancement Description:");
                    player.sendMessage(ChatColor.GOLD + advancement.getDescription());
                    player.sendMessage(ChatColor.GRAY + "-----------------------");
                }
                forceBattlePlugin.getNametagManager().updateNametag(players);
                forceBattlePlugin.getBossbarManager().updateBossbar(players);
            }
            Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Timer has been started.");
        }
        sendActionbar();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("forcebattle.timer")) {
                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Lacking permission: 'forcebattle.timer'");
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
                                if (time < 1) {
                                    player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Timer can't be set below 1 second.");
                                }
                                setTime(time);
                                setStartTime(time);
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Timer has been set to " + time + " seconds.");
                                sendActionbar();
                            } catch (NumberFormatException e) {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Time value must be an integer.");
                            }
                            break;
                        }
                        player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Usage: /timer set <time>");
                        break;
                    }
                    default: {
                        sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Usage: /timer <toggle | set> <time>");
                        break;
                    }
                }
            } else {
                player.openInventory(timerUI.getTimerUI());
            }
        }
        return false;
    }

    public TimerUI getTimerUI() {
        return timerUI;
    }
}