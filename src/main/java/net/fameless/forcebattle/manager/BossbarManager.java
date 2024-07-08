package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.Format;
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
        Object challenge = ObjectiveManager.getObjective(player);
        Challenge type = ObjectiveManager.getChallengeType(player);

        if (ExcludeCommand.excludedPlayers.contains(player)) {
            bossBar = Bukkit.createBossBar(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You are excluded", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        }

        if (type == null) {
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "No challenge selected." + ChatColor.GOLD + " /menu", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        }

        if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) {
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Waiting..." + ChatColor.GOLD + " /timer toggle", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarHashMap.put(player.getUniqueId(), bossBar);
            return;
        } else if (challenge instanceof Material playerMaterial) {
            String itemName = Format.formatName(playerMaterial.name());
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Item" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + itemName + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof EntityType mob) {
            String mobName = Format.formatName(mob.name());
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Mob" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + mobName + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Biome biome) {
            String biomeName = Format.formatName(biome.name());
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Biome" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + biomeName + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Advancement advancement) {
            String biomeName = Format.formatName(advancement.name());
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Advancement" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + biomeName + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else if (challenge instanceof Integer) {
            int height = (Integer) challenge;
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Height" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + height + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
        } else {
            bossBar = Bukkit.createBossBar(ChatColor.GRAY + "No objective...", BarColor.WHITE, BarStyle.SOLID);
        }
        bossBar.addPlayer(player);
        bossBarHashMap.put(player.getUniqueId(), bossBar);
    }
}