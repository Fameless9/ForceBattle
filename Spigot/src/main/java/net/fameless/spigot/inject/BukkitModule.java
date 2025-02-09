package net.fameless.spigot.inject;

import com.google.inject.AbstractModule;
import net.fameless.forceBattle.ForceBattlePlatform;
import net.fameless.forceBattle.game.ObjectiveManager;
import net.fameless.forceBattle.gui.LanguageGUI;
import net.fameless.forceBattle.gui.ResultGUI;
import net.fameless.forceBattle.gui.SettingsGUI;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.game.BukkitObjectiveManager;
import net.fameless.spigot.gui.BukkitLanguageGUI;
import net.fameless.spigot.gui.BukkitResultGUI;
import net.fameless.spigot.gui.BukkitSettingsGUI;

public class BukkitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ForceBattlePlatform.class).toInstance(BukkitPlatform.get());
        bind(ObjectiveManager.class).toInstance(new BukkitObjectiveManager());
        bind(ResultGUI.class).toInstance(new BukkitResultGUI());
        bind(LanguageGUI.class).toInstance(new BukkitLanguageGUI());
        bind(SettingsGUI.class).toInstance(new BukkitSettingsGUI());
    }

}
