package net.fameless.core;

import net.kyori.adventure.text.Component;

import java.io.File;
import java.util.logging.Logger;

public interface ForceBattlePlatform {

    File getDataDirectory();

    File getPluginFile();

    Logger getLogger();

    int getGameDuration();

    void shutDown();

    String getPluginVersion();

    void broadcast(Component message);

}
