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
import net.fameless.forcebattle.manager.ChainManager;
import net.fameless.forcebattle.manager.ObjectiveLists;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.timer.TimerUI;
import net.fameless.forcebattle.util.UpdateChecker;
import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public final class ForceBattlePlugin extends JavaPlugin {

    private static ForceBattlePlugin instance;

    public static ForceBattlePlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("is_reset")) {
            deleteWorld("world");
            deleteWorld("world_nether");
            deleteWorld("world_the_end");
            getConfig().set("is_reset", false);
            saveConfig();
        }
    }

    @Override
    public void onEnable() {
        try {
            ObjectiveLists.initLists();
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to create files. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        GameListener.run();
        ChainManager.updateLists();

        ResultCommand resultCommand = new ResultCommand();
        PointsUI pointsGUI = new PointsUI();
        JokerUI jokerUI = new JokerUI();
        ResetUI resetUI = new ResetUI();
        MenuUI menuGUI = new MenuUI();
        Timer timer = new Timer();

        getCommand("team").setExecutor(new TeamCommand());
        getCommand("skip").setExecutor(new SkipObjectiveCommand());
        getCommand("exclude").setExecutor(new ExcludeCommand());
        getCommand("backpack").setExecutor(new BackpackCommand());
        getCommand("resetworld").setExecutor(new ResetWorldCommand());
        getCommand("menu").setExecutor(menuGUI);
        getCommand("points").setExecutor(pointsGUI);
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

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new TimerUI(), this);
        Bukkit.getPluginManager().registerEvents(menuGUI, this);
        Bukkit.getPluginManager().registerEvents(pointsGUI, this);
        Bukkit.getPluginManager().registerEvents(resetUI, this);
        Bukkit.getPluginManager().registerEvents(jokerUI, this);
        Bukkit.getPluginManager().registerEvents(resultCommand, this);

        if (getServer().getPluginManager().isPluginEnabled("placeholderapi")) {
            new ObjectivePlaceholder().register();
            new PointsPlaceholder().register();
            new ObjectiveTypePlaceholder().register();
        }

        new Metrics(this, 20754);
        UpdateChecker checker = new UpdateChecker(112328, Duration.ofHours(2L));
        checker.checkForUpdates();
    }

    private void deleteWorld(String worldName) {
        try {
            File worldFile = new File(Bukkit.getWorldContainer(), worldName);
            FileUtils.deleteDirectory(worldFile);

            worldFile.mkdirs();
            new File(worldFile, "data").mkdirs();
            new File(worldFile, "datapacks").mkdirs();
            new File(worldFile, "playerdata").mkdirs();
            new File(worldFile, "poi").mkdirs();
            new File(worldFile, "region").mkdirs();
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to delete world: " + worldName);
        }
    }
}
