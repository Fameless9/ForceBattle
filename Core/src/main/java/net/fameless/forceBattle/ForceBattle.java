package net.fameless.forceBattle;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import net.fameless.forceBattle.bossbar.BossbarManager;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.caption.Language;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.configuration.PluginUpdater;
import net.fameless.forceBattle.configuration.SettingsFile;
import net.fameless.forceBattle.game.ObjectiveManager;
import net.fameless.forceBattle.game.Timer;
import net.fameless.forceBattle.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ForceBattle {

    private static boolean initialized = false;
    private static ForceBattlePlatform platform;
    private static Logger logger;
    private static Timer timer;
    private static ObjectiveManager objectiveManager;

    private static Injector injector;

    private ForceBattle() {
    }

    public static synchronized void initCore(AbstractModule platformModule) {
        if (initialized) {
            throw new RuntimeException("You may not create another instance of ForceBattle Core.");
        }

        injector = Guice.createInjector(
                Stage.PRODUCTION,
                platformModule
        );

        platform = injector.getInstance(ForceBattlePlatform.class);
        logger = platform.getLogger();

        initLanguages();

        objectiveManager = injector.getInstance(ObjectiveManager.class);
        try {
            SettingsFile.loadSettingsFromFile(new File(platform.getDataDirectory(), "data/settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("settings.json File not found" + e);
        }
        timer = new Timer(platform.getGameDuration(), false);

        Command.init();
        BossbarManager.runTask();
        PluginUpdater.checkForUpdate();

        initialized = true;
        logger.info("Successfully initialized ForceBattle.");
    }

    private static void initLanguages() {
        Caption.loadLanguage(Language.ENGLISH, ResourceUtil.readJsonResource("lang_en.json"));
        Caption.loadLanguage(Language.GERMAN, ResourceUtil.readJsonResource("lang_de.json"));
    }

    public static Injector injector() {
        return injector;
    }

    public static ForceBattlePlatform platform() {
        return platform;
    }

    public static Logger logger() {
        return logger;
    }

    public static Timer getTimer() {
        return timer;
    }

    public static ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

}
