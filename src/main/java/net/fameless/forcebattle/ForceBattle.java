package net.fameless.forcebattle;

import lombok.Getter;
import net.fameless.forcebattle.bossbar.BossbarManager;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.caption.Language;
import net.fameless.forcebattle.command.CommandHandler;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.PermissionManager;
import net.fameless.forcebattle.configuration.PluginUpdater;
import net.fameless.forcebattle.configuration.SettingsFile;
import net.fameless.forcebattle.game.GameListener;
import net.fameless.forcebattle.game.NametagManager;
import net.fameless.forcebattle.game.ObjectiveManager;
import net.fameless.forcebattle.game.Timer;
import net.fameless.forcebattle.gui.LanguageGUI;
import net.fameless.forcebattle.gui.ResultGUI;
import net.fameless.forcebattle.gui.SettingsGUI;
import net.fameless.forcebattle.tablist.TablistManager;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.ResourceUtil;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class ForceBattle extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle");
    private static ForceBattle instance;

    @Getter
    private static Timer timer;
    @Getter
    private static ObjectiveManager objectiveManager;
    @Getter
    private LanguageGUI languageGUI;
    @Getter
    private ResultGUI resultGUI;
    @Getter
    private SettingsGUI settingsGUI;

    public static ForceBattle get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("data/settings.json", false);

        Caption.setCurrentLanguage(Language.ofIdentifier(getConfig().getString("lang", "en")));

        this.languageGUI = new LanguageGUI();
        this.resultGUI = new ResultGUI();
        this.settingsGUI = new SettingsGUI();

        initCore();

        NametagManager.runTask();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(languageGUI, this);
        Bukkit.getPluginManager().registerEvents(resultGUI, this);
        Bukkit.getPluginManager().registerEvents(settingsGUI, this);

        CommandHandler.registerAll(Command.COMMANDS);
        PermissionManager.registerPermissions();

        final int SERVICE_ID = 20754;
        new Metrics(this, SERVICE_ID);

        logger.info("Successfully initialized ForceBattle.");
    }

    @Override
    public void onDisable() {
        try {
            SettingsFile.saveToFile(new File(getDataDirectory(), "data/settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save settings to file", e);
        }
        getConfig().set("first-startup", false);
        saveConfig();
    }

    private void initCore() {
        loadLanguages();

        objectiveManager = new ObjectiveManager();
        try {
            SettingsFile.loadSettingsFromFile(new File(getDataDirectory(), "data/settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("settings.json File not found", e);
        }

        timer = new Timer(getGameDuration(), false);

        Command.createInstances();
        BossbarManager.runTask();
        TablistManager.startUpdating();
        PluginUpdater.checkForUpdate();
    }

    private void loadLanguages() {
        Caption.loadLanguage(Language.ENGLISH, ResourceUtil.readJsonResource("languages/en_US.json"));
        Caption.loadLanguage(Language.CHINESE_SIMPLIFIED, ResourceUtil.readJsonResource("languages/zh_CN.json"));
        Caption.loadLanguage(Language.CHINESE_TRADITIONAL, ResourceUtil.readJsonResource("languages/zh_TW.json"));
        Caption.loadLanguage(Language.GERMAN, ResourceUtil.readJsonResource("languages/de_DE.json"));
    }

    public @NotNull File getDataDirectory() {
        return getDataFolder();
    }

    public @NotNull File getPluginFile() {
        return getFile();
    }

    public int getGameDuration() {
        return getConfig().getInt("game-duration", 5400);
    }

    public void shutDown() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public String getPluginVersion() {
        return getDescription().getVersion();
    }

    public static void broadcast(Component message) {
        BukkitUtil.BUKKIT_AUDIENCES.all().sendMessage(message);
    }
}
