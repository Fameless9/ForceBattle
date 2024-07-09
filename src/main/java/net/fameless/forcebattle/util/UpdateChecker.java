package net.fameless.forcebattle.util;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;

public class UpdateChecker {
    private final int resourceId;
    private final Duration checkInterval;
    private Instant lastCheckTime;

    public UpdateChecker(int resourceId, Duration checkInterval) {
        this.resourceId = resourceId;
        this.checkInterval = checkInterval;
        this.lastCheckTime = Instant.MIN;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(ForceBattlePlugin.get(), () -> {
            Instant currentTime = Instant.now();
            if (Duration.between(lastCheckTime, currentTime).compareTo(checkInterval) < 0) {
                return;
            }

            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                String latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                String currentVersion = ForceBattlePlugin.get().getDescription().getVersion();
                if (latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion)) {
                    Bukkit.getLogger().info("[Force Battle Plugin] A new update is available! Version " + latestVersion + " can be downloaded from the SpigotMC website: https://www.spigotmc.org/resources/1-20-x-24-7-support-force-item-battle-force-block-battle.112328/");
                    Bukkit.getScheduler().runTaskTimer(ForceBattlePlugin.get(), () -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "ForceBattle is not running on the latest version. " +
                                        "Please keep ForceBattle up to date to avoid bugs: " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://www.spigotmc.org/resources/112328/");
                            }
                        }
                    }, 2700 * 20, 2700 * 20);
                    JoinListener.isUpdated = false;
                }
                lastCheckTime = currentTime;
            } catch (IOException e) {
                ForceBattlePlugin.get().getLogger().log(Level.WARNING, "Failed to check for updates: " + e.getMessage(), e);
            }
        });
    }
}
