package net.fameless.forcebattle.listener;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.ItemManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.manager.PointsManager;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameListener implements Listener {

    public static void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Timer.isRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ExcludeCommand.excludedPlayers.contains(player)) continue;
                    Challenge playerChallengeType = ItemManager.getChallengeType(player);
                    Object challengeObject = ItemManager.getChallenge(player);

                    if (playerChallengeType == Challenge.FORCE_ITEM && challengeObject instanceof Material) {
                        Material requiredMaterial = (Material) challengeObject;

                        if (player.getInventory().contains(requiredMaterial)) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " found item " + BossbarManager.formatItemName(requiredMaterial.name().replace("_", " ")));
                            PointsManager.addPoint(player);
                            ItemManager.updateObjective(player);
                        }
                        continue;
                    }
                    if (playerChallengeType == Challenge.FORCE_BIOME && challengeObject instanceof Biome) {
                        Biome requiredBiome = (Biome) challengeObject;

                        if (player.getWorld().getBiome(player.getLocation()) == requiredBiome) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " found biome " + BossbarManager.formatItemName(requiredBiome.name().replace("_", " ")));
                            PointsManager.addPoint(player);
                            ItemManager.updateObjective(player);
                        }
                        continue;
                    }
                    if (playerChallengeType == Challenge.FORCE_ADVANCEMENT && challengeObject instanceof net.fameless.forcebattle.util.Advancement) {
                        net.fameless.forcebattle.util.Advancement requiredAdvancement = (net.fameless.forcebattle.util.Advancement) challengeObject;

                        if (hasAdvancement(player, requiredAdvancement.getKey().toString())) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " completed advancement " + BossbarManager.formatItemName(requiredAdvancement.name().replace("_", " ")));
                            PointsManager.addPoint(player);
                            ItemManager.updateObjective(player);
                            resetAdvancements(player);
                        }
                        continue;
                    }
                    if (playerChallengeType == Challenge.FORCE_HEIGHT && challengeObject instanceof Integer) {
                        int requiredHeight = (Integer) challengeObject;

                        if (player.getLocation().getBlockY() == requiredHeight) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " reached height " + requiredHeight);
                            PointsManager.addPoint(player);
                            ItemManager.updateObjective(player);
                        }
                    }
                }
            }
        }.runTaskTimer(ForceBattlePlugin.getInstance(), 0, 2);
    }

    private static boolean hasAdvancement(Player player, String name) {
        Advancement advancement = getAdvancement(name);
        if (advancement == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }

    private static Advancement getAdvancement(String name) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            Advancement a = it.next();
            if (a.getKey().toString().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    public static void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria())
                progress.revokeCriteria(criteria);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Timer.isRunning()) return;
        if (!(event.getDamager() instanceof  Player)) return;
        Player player = (Player) event.getDamager();
        if (ExcludeCommand.excludedPlayers.contains(player)) return;
        if (!event.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getHealth() - event.getDamage() > 0) return;
        }

        if (!(ItemManager.getChallenge(player) instanceof EntityType)) return;
        EntityType requiredEntity = (EntityType) ItemManager.getChallenge(player);

        if (event.getEntity().getType().equals(requiredEntity)) {
            if (!event.getEntity().isDead()) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                if (entity.getHealth() - event.getDamage() > 0) return;
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " killed mob " + BossbarManager.formatItemName(event.getEntity().getType().name().replace("_", " ")));
            PointsManager.addPoint(player);
            ItemManager.updateObjective(player);
        }
    }

    @EventHandler
    public void onPlayerSkip(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer())) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(ItemProvider.getSkipItem(1).getItemMeta())) {
            event.setCancelled(true);

            if (!Timer.isRunning()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't skip while timer is paused.");
                return;
            }

            if (ItemManager.getChallenge(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "No objective to skip.");
                return;
            }

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);
            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);

            if (ItemManager.getChallenge(event.getPlayer()) instanceof Material) {
                Bukkit.getWorld(event.getPlayer().getWorld().getName()).dropItemNaturally(event.getPlayer().getLocation(),
                        new ItemStack((Material) ItemManager.getChallenge(event.getPlayer())));
            }

            ItemManager.updateObjective(event.getPlayer());
            PointsManager.addPoint(event.getPlayer());
            NametagManager.updateNametag(event.getPlayer());
            BossbarManager.updateBossbar(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerSwap(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer())) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(ItemProvider.getSwapitem(1).getItemMeta())) {
            event.setCancelled(true);

            if (!Timer.isRunning()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't swap while timer is paused.");
                return;
            }

            if (ItemManager.getChallenge(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "No objective to swap.");
                return;
            }

            List<Player> availablePlayers = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.getPlayer().equals(player)) continue;
                if (ItemManager.getChallenge(player) == null) continue;
                availablePlayers.add(player);
            }

            if (availablePlayers.isEmpty()) {
                event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                return;
            }

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);

            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

            Challenge playerChallenge = ItemManager.getChallengeType(event.getPlayer());
            Challenge targetChallenge = ItemManager.getChallengeType(target);

            Object playerObjective = ItemManager.getChallenge(event.getPlayer());
            Object targetObjective = ItemManager.getChallenge(target);

            ItemManager.setChallengeType(event.getPlayer(), targetChallenge);
            ItemManager.setChallengeType(target, playerChallenge);

            ItemManager.setChallenge(event.getPlayer(), targetChallenge, targetObjective);
            ItemManager.setChallenge(target, playerChallenge, playerObjective);

            target.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their objective with yours.");
            event.getPlayer().sendMessage(ChatColor.GOLD + "Your objective was swapped with " + target.getName() + "'s.");

            for (Player player3 : Bukkit.getOnlinePlayers()) {
                if (player3 != target && player3 != event.getPlayer()) {
                    player3.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with " + target.getName() + ".");
                }
            }

            target.playSound(target, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 20);

            NametagManager.updateNametag(target);
            NametagManager.updateNametag(event.getPlayer());
            BossbarManager.updateBossbar(target);
            BossbarManager.updateBossbar(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().equals(ItemProvider.getSkipItem(1).getItemMeta()) ||
                event.getItemDrop().getItemStack().getItemMeta().equals(ItemProvider.getSwapitem(1).getItemMeta())) {
            event.setCancelled(true);
        }
    }
}