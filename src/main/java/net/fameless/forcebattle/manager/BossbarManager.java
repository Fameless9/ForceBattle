package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BossbarManager {

    public static HashMap<UUID, BossBar> bossBarHashMap = new HashMap<>();

    public static void createBossbar(Player player) {
        if (BossbarManager.bossBarHashMap.get(player.getUniqueId()) != null) {
            BossbarManager.bossBarHashMap.get(player.getUniqueId()).addPlayer(player);
            return;
        }
        getBossbar(player);
    }

    public static void updateBossbar(Player player) {
        if (bossBarHashMap.containsKey(player.getUniqueId())) {
            BossbarManager.bossBarHashMap.get(player.getUniqueId()).removePlayer(player);
        }
        getBossbar(player);
    }

    private static void getBossbar(Player player) {
        BossBar bossBar;
        Object challenge = ItemManager.getChallenge(player);
        Challenge type = ItemManager.getChallengeType(player);

        if (ExcludeCommand.excludedPlayers.contains(player)) {
            bossBar = Bukkit.createBossBar(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You are excluded", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        }

        if (type == null) {
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "No challenge selected. /menu", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        }

        if (!Timer.isRunning()) {
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Waiting... /timer toggle", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        }

        else if (challenge instanceof Material) {
            Material playerMaterial = (Material) challenge;
            String itemName = formatItemName(playerMaterial.name().replace("_", " "));
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Item" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + itemName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof EntityType) {
            EntityType mob = (EntityType) challenge;
            String mobName = formatItemName(mob.name().replace("_", " "));
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Mob" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + mobName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Biome) {
            Biome biome = (Biome) ItemManager.getChallenge(player);
            String biomeName = formatItemName(biome.name().replace("_", " "));
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + biomeName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Advancement) {
            Advancement advancement = (Advancement) challenge;
            String biomeName = formatItemName(advancement.name().replace("_", " "));
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Advancement" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + biomeName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Integer) {
            int height = (Integer) challenge;
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Height" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + height + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else {
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "No objective...", BarColor.WHITE, BarStyle.SOLID);
        }
        bossBar.addPlayer(player);
        bossBarHashMap.put(player.getUniqueId(), bossBar);
    }

    public static String formatItemName(String input) {
        String[] words = input.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            formatted.append(formattedWord).append(" ");
        }
        return formatted.toString().trim();
    }
}