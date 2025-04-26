package net.fameless.spigot.inject;

import com.google.inject.AbstractModule;
import net.fameless.core.ForceBattlePlatform;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.core.gui.LanguageGUI;
import net.fameless.core.gui.ResultGUI;
import net.fameless.core.gui.SettingsGUI;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.game.BukkitObjectiveManager;
import net.fameless.spigot.gui.BukkitLanguageGUI;
import net.fameless.spigot.gui.BukkitResultGUI;
import net.fameless.spigot.gui.BukkitSettingsGUI;

public class BukkitModule extends AbstractModule {

    private final BukkitLanguageGUI languageGUI;
    private final BukkitResultGUI resultGUI;
    private final BukkitSettingsGUI settingsGUI;

    public BukkitModule(BukkitLanguageGUI languageGUI, BukkitResultGUI resultGUI, BukkitSettingsGUI settingsGUI) {
        this.languageGUI = languageGUI;
        this.resultGUI = resultGUI;
        this.settingsGUI = settingsGUI;
    }

    @Override
    protected void configure() {
        bind(ForceBattlePlatform.class).toInstance(BukkitPlatform.get());
        bind(ObjectiveManager.class).toInstance(new BukkitObjectiveManager());
        bind(ResultGUI.class).toInstance(resultGUI);
        bind(LanguageGUI.class).toInstance(languageGUI);
        bind(SettingsGUI.class).toInstance(settingsGUI);
    }

}
