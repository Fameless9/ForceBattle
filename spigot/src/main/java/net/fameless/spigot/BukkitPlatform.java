package net.fameless.spigot;

import net.fameless.core.ForceBattle;
import net.fameless.core.ForceBattlePlatform;
import net.fameless.core.command.framework.Command;
import net.fameless.core.config.PluginConfig;
import net.fameless.spigot.command.CommandHandler;
import net.fameless.spigot.command.PermissionManager;
import net.fameless.spigot.game.GameListener;
import net.fameless.spigot.game.NametagManager;
import net.fameless.spigot.gui.LanguageGUI;
import net.fameless.spigot.gui.PointsGUI;
import net.fameless.spigot.gui.ResultGUI;
import net.fameless.spigot.gui.SettingsGUI;
import net.fameless.spigot.inject.BukkitModule;
import net.fameless.spigot.util.BukkitUtil;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BukkitPlatform extends JavaPlugin implements ForceBattlePlatform {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + BukkitPlatform.class.getSimpleName());
    private static BukkitPlatform instance;

    public static BukkitPlatform get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        ForceBattle.initCore(new BukkitModule());

        NametagManager.runTask();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new LanguageGUI(), this);
        Bukkit.getPluginManager().registerEvents(new ResultGUI(), this);
        Bukkit.getPluginManager().registerEvents(new SettingsGUI(), this);
        Bukkit.getPluginManager().registerEvents(new PointsGUI(), this);

        CommandHandler.registerAll(Command.getCommands());
        PermissionManager.registerPermissions();

        final int SERVICE_ID = 20754;
        new Metrics(this, SERVICE_ID);

        logger.info("Successfully initialized ForceBattle-Spigot.");
    }

    @Override
    public void onDisable() {
        PluginConfig.shutdown();
    }

    @Override
    public void broadcast(Component message) {
        BukkitUtil.BUKKIT_AUDIENCES.all().sendMessage(message);
    }

}
