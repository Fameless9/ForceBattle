package net.fameless.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import net.fameless.core.bossbar.BossbarManager;
import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;
import net.fameless.core.command.framework.Command;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.core.game.Timer;
import net.fameless.core.util.PluginUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceBattle {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + ForceBattle.class.getSimpleName());
    private static boolean initialized = false;
    private static ForceBattlePlatform platform;
    private static Timer timer;
    private static ObjectiveManager objectiveManager;

    private ForceBattle() {
        throw new UnsupportedOperationException("ForceBattle cannot be instantiated.");
    }

    public static synchronized void initCore(AbstractModule platformModule) {
        if (initialized) {
            throw new RuntimeException("You may not create another instance of ForceBattle Core.");
        }
        logger.info("Initializing ForceBattle Core...");
        PluginConfig.init();

        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                platformModule
        );

        platform = injector.getInstance(ForceBattlePlatform.class);

        Caption.loadDefaultLanguages();
        Caption.setCurrentLanguage(Language.ofIdentifier(PluginConfig.get().getString("lang", "en")));

        objectiveManager = injector.getInstance(ObjectiveManager.class);
        timer = new Timer(PluginConfig.get().getInt("settings.game-duration", 5400), false);

        Command.init();
        BossbarManager.runTask();
        PluginUpdater.runTask();

        PluginConfig.get().set("first-startup", false);

        initialized = true;
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
