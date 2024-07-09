package net.fameless.forcebattle;

import net.fameless.forcebattle.GUI.JokerUI;
import net.fameless.forcebattle.GUI.MenuUI;
import net.fameless.forcebattle.GUI.PointsUI;
import net.fameless.forcebattle.GUI.ResetUI;
import net.fameless.forcebattle.Placeholder.ObjectivePlaceholder;
import net.fameless.forcebattle.Placeholder.ObjectiveTypePlaceholder;
import net.fameless.forcebattle.Placeholder.PointsPlaceholder;
import net.fameless.forcebattle.command.*;
import net.fameless.forcebattle.command.tabcompleter.*;
import net.fameless.forcebattle.listener.GameListener;
import net.fameless.forcebattle.listener.JoinListener;
import net.fameless.forcebattle.manager.*;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.UpdateChecker;
import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public final class ForceBattlePlugin extends JavaPlugin {

    public static final String PREFIX = ChatColor.GOLD.toString() + ChatColor.BOLD + "ForceBattle" + ChatColor.RESET + ChatColor.DARK_GRAY + "» " + ChatColor.RESET;
    private static NamespacedKey pageKey;
    private static NamespacedKey playerKey;
    private static ForceBattlePlugin instance;

    private Timer timer;
    private MenuUI menuUI;
    private NewWorldCommand newWorldCommand;
    private ObjectiveLists objectiveLists;
    private ChainManager chainManager;
    private ObjectiveManager objectiveManager;
    private BossbarManager bossbarManager;
    private PointsManager pointsManager;
    private NametagManager nametagManager;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().contains("toDelete")) {
            List<String> worlds = getConfig().getStringList("toDelete");
            for (String s : worlds) {
                File world = new File(Bukkit.getWorldContainer(), s);
                File nether = new File(Bukkit.getWorldContainer(), s + "_nether");
                File end = new File(Bukkit.getWorldContainer(), s + "_the_end");
                try {
                    if (world.exists() && world.isDirectory()) {
                        FileUtils.deleteDirectory(world);
                    }
                    if (nether.exists() && nether.isDirectory()) {
                        FileUtils.deleteDirectory(nether);
                    }
                    if (end.exists() && end.isDirectory()) {
                        FileUtils.deleteDirectory(end);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            worlds.clear();
            saveConfig();
        }
    }

    @Override
    public void onEnable() {
        objectiveLists = new ObjectiveLists(this);
        objectiveManager = new ObjectiveManager(this);
        chainManager = new ChainManager();
        pointsManager = new PointsManager(this);
        bossbarManager = new BossbarManager();
        nametagManager = new NametagManager();

        objectiveManager.setChainManager(chainManager);

        pageKey = new NamespacedKey(this, "page");
        playerKey = new NamespacedKey(this, "player");

        timer = new Timer(this);
        ResultCommand resultCommand = new ResultCommand();
        PointsUI pointsUI = new PointsUI(this);
        JokerUI jokerUI = new JokerUI();
        ResetUI resetUI = new ResetUI(this);
        menuUI = new MenuUI(this);
        newWorldCommand = new NewWorldCommand();

        getCommand("team").setExecutor(new TeamCommand());
        getCommand("skip").setExecutor(new SkipObjectiveCommand());
        getCommand("exclude").setExecutor(new ExcludeCommand());
        getCommand("backpack").setExecutor(new BackpackCommand());
        getCommand("newworld").setExecutor(newWorldCommand);
        getCommand("menu").setExecutor(menuUI);
        getCommand("points").setExecutor(pointsUI);
        getCommand("timer").setExecutor(timer);
        getCommand("reset").setExecutor(resetUI);
        getCommand("joker").setExecutor(jokerUI);
        getCommand("result").setExecutor(resultCommand);

        getCommand("team").setTabCompleter(new TeamCmdTabCompleter());
        getCommand("skip").setTabCompleter(new SkipCommandTabCompleter());
        getCommand("exclude").setTabCompleter(new ExcludeCommandTabCompleter());
        getCommand("backpack").setTabCompleter(new BackpackCommandTabCompleter());
        getCommand("points").setTabCompleter(new PointsCommandTabCompleter());
        getCommand("timer").setTabCompleter(new TimerCommandTabCompleter());
        getCommand("reset").setTabCompleter(new ResetCommandTabCompleter());
        getCommand("joker").setTabCompleter(new JokerCommandTabCompleter());

        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(newWorldCommand, this);
        getServer().getPluginManager().registerEvents(timer.getTimerUI(), this);
        getServer().getPluginManager().registerEvents(menuUI, this);
        getServer().getPluginManager().registerEvents(pointsUI, this);
        getServer().getPluginManager().registerEvents(resetUI, this);
        getServer().getPluginManager().registerEvents(jokerUI, this);
        getServer().getPluginManager().registerEvents(resultCommand, this);

        if (getServer().getPluginManager().isPluginEnabled("placeholderapi")) {
            new ObjectivePlaceholder().register();
            new PointsPlaceholder().register();
            new ObjectiveTypePlaceholder().register();
        }

        new Metrics(this, 20754);
        UpdateChecker checker = new UpdateChecker(112328, Duration.ofHours(2L));
        checker.checkForUpdates();
    }

    @Override
    public void onDisable() {
        getConfig().set("toDelete", newWorldCommand.getToDelete());
        saveConfig();
    }

    public Timer getTimer() {
        return timer;
    }

    public MenuUI getMenuUI() {
        return menuUI;
    }

    public ObjectiveLists getObjectiveLists() {
        return objectiveLists;
    }

    public ChainManager getChainManager() {
        return chainManager;
    }

    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

    public BossbarManager getBossbarManager() {
        return bossbarManager;
    }

    public PointsManager getPointsManager() {
        return pointsManager;
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public static ForceBattlePlugin get() {
        return ForceBattlePlugin.instance;
    }

    public static NamespacedKey getPageKey() {
        return pageKey;
    }

    public static NamespacedKey getPlayerKey() {
        return playerKey;
    }
}
