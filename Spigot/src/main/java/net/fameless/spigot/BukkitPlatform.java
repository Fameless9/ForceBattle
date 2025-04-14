package net.fameless.spigot;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.ForceBattlePlatform;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.caption.Language;
import net.fameless.forceBattle.configuration.SettingsFile;
import net.fameless.spigot.command.CommandHandler;
import net.fameless.spigot.game.GameListener;
import net.fameless.spigot.game.NametagManager;
import net.fameless.spigot.gui.BukkitLanguageGUI;
import net.fameless.spigot.gui.BukkitResultGUI;
import net.fameless.spigot.gui.BukkitSettingsGUI;
import net.fameless.spigot.inject.BukkitModule;
import net.fameless.spigot.util.BukkitUtil;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class BukkitPlatform extends JavaPlugin implements ForceBattlePlatform {

    private static BukkitPlatform instance;

    public static BukkitPlatform get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("data/settings.json", false);

        Caption.setCurrentLanguage(Language.ofIdentifier(getConfig().getString("lang", "en")));

        ForceBattle.initCore(new BukkitModule());

        NametagManager.runTask();

        BukkitLanguageGUI bukkitLanguageGUI = new BukkitLanguageGUI();
        BukkitSettingsGUI bukkitSettingsGUI = new BukkitSettingsGUI();
        BukkitResultGUI bukkitResultGUI = new BukkitResultGUI();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(bukkitLanguageGUI, this);
        Bukkit.getPluginManager().registerEvents(bukkitSettingsGUI, this);
        Bukkit.getPluginManager().registerEvents(bukkitResultGUI, this);

        CommandHandler commandHandler = new CommandHandler();

        getCommand("lang").setExecutor(commandHandler);
        getCommand("settings").setExecutor(commandHandler);
        getCommand("result").setExecutor(commandHandler);
        getCommand("timer").setExecutor(commandHandler);
        getCommand("team").setExecutor(commandHandler);
        getCommand("exclude").setExecutor(commandHandler);
        getCommand("skip").setExecutor(commandHandler);
        getCommand("reset").setExecutor(commandHandler);
        getCommand("backpack").setExecutor(commandHandler);
        getCommand("displayresults").setExecutor(commandHandler);
        getCommand("joker").setExecutor(commandHandler);

        getCommand("lang").setTabCompleter(commandHandler);
        getCommand("result").setTabCompleter(commandHandler);
        getCommand("timer").setTabCompleter(commandHandler);
        getCommand("team").setTabCompleter(commandHandler);
        getCommand("exclude").setTabCompleter(commandHandler);
        getCommand("skip").setTabCompleter(commandHandler);
        getCommand("reset").setTabCompleter(commandHandler);
        getCommand("backpack").setTabCompleter(commandHandler);
        getCommand("joker").setTabCompleter(commandHandler);

        final int SERVICE_ID = 20754;
        new Metrics(this, SERVICE_ID);
    }

    @Override
    public void onDisable() {
        try {
            SettingsFile.saveToFile(new File(getDataDirectory(), "data/settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save settings to file." + e);
        }
        getConfig().set("first-startup", false);
        saveConfig();
    }

    @Override
    public @NotNull File getDataDirectory() {
        return this.getDataFolder();
    }

    @Override
    public @NotNull File getPluginFile() {
        return getFile();
    }

    @Override
    public @NotNull Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public int getGameDuration() {
        return getConfig().getInt("game-duration", 5400);
    }

    @Override
    public void shutDown() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @Override
    public String getPluginVersion() {
        return getDescription().getVersion();
    }

    @Override
    public void broadcast(Component message) {
        BukkitUtil.BUKKIT_AUDIENCES.all().sendMessage(message);
    }

}
