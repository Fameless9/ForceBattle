package net.fameless.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import net.fameless.core.bossbar.BossbarManager;
import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;
import net.fameless.core.command.framework.Command;
import net.fameless.core.configuration.PluginUpdater;
import net.fameless.core.configuration.SettingsFile;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.core.game.Timer;
import net.fameless.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ForceBattle {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + ForceBattle.class.getSimpleName());
    private static boolean initialized = false;
    private static ForceBattlePlatform platform;
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

        initLanguages();

        objectiveManager = injector.getInstance(ObjectiveManager.class);
        try {
            SettingsFile.loadSettingsFromFile(new File(platform.getDataDirectory(), "data/settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("settings.json File not found" + e);
        }
        timer = new Timer(platform.getGameDuration(), false);

        Command.createInstances();
        BossbarManager.runTask();
        PluginUpdater.checkForUpdate();

        initialized = true;
        logger.info("Successfully initialized ForceBattle-Core.");
    }

    private static void initLanguages() {
        Caption.loadLanguage(Language.ENGLISH, ResourceUtil.readJsonResource("lang_en.json"));
        Caption.loadLanguage(Language.CHINESE_SIMPLIFIED, ResourceUtil.readJsonResource("lang_zh_cn.json"));
        Caption.loadLanguage(Language.CHINESE_TRADITIONAL, ResourceUtil.readJsonResource("lang_zh_tw.json"));
        Caption.loadLanguage(Language.GERMAN, ResourceUtil.readJsonResource("lang_de.json"));
    }

    public static Injector injector() {
        return injector;
    }

    public static ForceBattlePlatform platform() {
        return platform;
    }

    public static Timer getTimer() {
        return timer;
    }

    public static ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

}
