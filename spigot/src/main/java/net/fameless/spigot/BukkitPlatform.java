package net.fameless.spigot;

import net.fameless.core.ForceBattle;
import net.fameless.core.ForceBattlePlatform;
import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;
import net.fameless.core.command.framework.Command;
import net.fameless.core.configuration.SettingsFile;
import net.fameless.spigot.command.CommandManager;
import net.fameless.spigot.command.PermissionManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class BukkitPlatform extends JavaPlugin implements ForceBattlePlatform {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + BukkitPlatform.class.getSimpleName());
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

        BukkitLanguageGUI languageGUI = new BukkitLanguageGUI();
        BukkitResultGUI resultGUI = new BukkitResultGUI();
        BukkitSettingsGUI settingsGUI = new BukkitSettingsGUI();

        ForceBattle.initCore(new BukkitModule(languageGUI, resultGUI, settingsGUI));

        NametagManager.runTask();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(languageGUI, this);
        Bukkit.getPluginManager().registerEvents(resultGUI, this);
        Bukkit.getPluginManager().registerEvents(settingsGUI, this);

        CommandManager.registerAll(Command.COMMANDS);
        PermissionManager.registerPermissions();

        final int SERVICE_ID = 20754;
        new Metrics(this, SERVICE_ID);

        logger.info("Successfully initialized ForceBattle-Spigot.");
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
