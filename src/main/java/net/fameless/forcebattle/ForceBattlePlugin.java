package net.fameless.forcebattle;

import net.fameless.forcebattle.GUI.JokerUI;
import net.fameless.forcebattle.GUI.MenuUI;
import net.fameless.forcebattle.GUI.PointsUI;
import net.fameless.forcebattle.GUI.ResetUI;
import net.fameless.forcebattle.command.*;
import net.fameless.forcebattle.command.tabcompleter.*;
import net.fameless.forcebattle.listener.GameListener;
import net.fameless.forcebattle.listener.JoinListener;
import net.fameless.forcebattle.manager.ChainManager;
import net.fameless.forcebattle.manager.ItemFile;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.timer.TimerUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ForceBattlePlugin extends JavaPlugin {

    private static ForceBattlePlugin instance;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        try {
            ItemFile.initFiles();
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
    }

    public static ForceBattlePlugin getInstance() { return instance; }
}
