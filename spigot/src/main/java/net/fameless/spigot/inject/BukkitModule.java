package net.fameless.spigot.inject;

import com.google.inject.AbstractModule;
import net.fameless.core.ForceBattlePlatform;
import net.fameless.core.game.ObjectiveManager;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.game.BukkitObjectiveManager;

public class BukkitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ForceBattlePlatform.class).toInstance(BukkitPlatform.get());
        bind(ObjectiveManager.class).toInstance(new BukkitObjectiveManager());
    }

}
