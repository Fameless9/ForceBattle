package net.fameless.core;

import net.kyori.adventure.text.Component;

import java.io.File;

public interface ForceBattlePlatform {

    File getDataDirectory();

    File getPluginFile();

    int getGameDuration();

    void shutDown();

    String getPluginVersion();

    void broadcast(Component message);

}
