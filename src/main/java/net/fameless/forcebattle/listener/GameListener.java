package net.fameless.forcebattle.listener;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.manager.PointsManager;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.Format;
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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameListener implements Listener {

    private static final List<UUID> onSkipCooldown = new ArrayList<>();
    private static final List<UUID> onSwapCooldown = new ArrayList<>();

    public static void run() {
        Bukkit.getScheduler().runTaskTimer(ForceBattlePlugin.getInstance(), () -> {
            if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ExcludeCommand.excludedPlayers.contains(player)) continue;
                Challenge playerChallengeType = ObjectiveManager.getChallengeType(player);
                Object challengeObject = ObjectiveManager.getObjective(player);

                if (playerChallengeType == Challenge.FORCE_ITEM && challengeObject instanceof Material requiredMaterial) {

                    if (player.getInventory().contains(requiredMaterial)) {
                        Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " found item " + ChatColor.GOLD + Format.formatName(requiredMaterial.name()));
                        PointsManager.addPoint(player);
                        ObjectiveManager.updateObjective(player);
                    }
                    continue;
                }
                if (playerChallengeType == Challenge.FORCE_BIOME && challengeObject instanceof Biome requiredBiome) {

                    if (player.getWorld().getBiome(player.getLocation()) == requiredBiome) {
                        Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " found biome " + ChatColor.GOLD + Format.formatName(requiredBiome.name()));
                        PointsManager.addPoint(player);
                        ObjectiveManager.updateObjective(player);
                    }
                    continue;
                }
                if (playerChallengeType == Challenge.FORCE_ADVANCEMENT && challengeObject instanceof net.fameless.forcebattle.util.Advancement requiredAdvancement) {

                    if (hasAdvancement(player, requiredAdvancement.getKey().toString())) {
                        Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " completed advancement " + ChatColor.GOLD + Format.formatName(requiredAdvancement.name()));
                        PointsManager.addPoint(player);
                        ObjectiveManager.updateObjective(player);
                        resetAdvancements(player);
                    }
                    continue;
                }
                if (playerChallengeType == Challenge.FORCE_HEIGHT && challengeObject instanceof Integer) {
                    int requiredHeight = (Integer) challengeObject;

                    if (player.getLocation().getBlockY() == requiredHeight) {
                        Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " reached height " + ChatColor.GOLD + requiredHeight);
                        PointsManager.addPoint(player);
                        ObjectiveManager.updateObjective(player);
                    }
                }
            }
        }, 0, 2);
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
        if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (ExcludeCommand.excludedPlayers.contains(player)) return;
        if (!event.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getHealth() - event.getDamage() > 0) return;
        }

        if (!(ObjectiveManager.getObjective(player) instanceof EntityType requiredEntity)) return;

        if (event.getEntity().getType().equals(requiredEntity)) {
            if (!event.getEntity().isDead()) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                if (entity.getHealth() - event.getDamage() > 0) return;
            }
            Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " killed mob " + ChatColor.GOLD + Format.formatName(event.getEntity().getType().name()));
            PointsManager.addPoint(player);
            ObjectiveManager.updateObjective(player);
        }
    }

    @EventHandler
    public void onPlayerSkip(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer())) return;
        if (onSkipCooldown.contains(event.getPlayer().getUniqueId())) return;
        if (event.getItem() != null && event.getItem().isSimilar(ItemProvider.getSkipItem(1))) {
            event.setCancelled(true);

            if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) {
                event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Can't skip while timer is paused.");
                return;
            }

            if (ObjectiveManager.getObjective(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "No objective to skip.");
                return;
            }

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);
            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);
            event.getPlayer().setCooldown(stack.getType(), 20);

            Object objective = ObjectiveManager.getObjective(event.getPlayer());
            if (objective instanceof Material material) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(),
                        new ItemStack(material));
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " skipped item " + ChatColor.GOLD + Format.formatName(material.name()));
            } else if (objective instanceof EntityType mob) {
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " skipped mob " + ChatColor.GOLD + Format.formatName(mob.name()));
            } else if (objective instanceof Biome biome) {
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " skipped biome " + ChatColor.GOLD + Format.formatName(biome.name()));
            } else if (objective instanceof net.fameless.forcebattle.util.Advancement advancement) {
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " skipped advancement " + ChatColor.GOLD + advancement.getName());
            } else if (objective instanceof Integer height) {
                Bukkit.broadcastMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " skipped height " + ChatColor.GOLD + height);
            }

            ObjectiveManager.updateObjective(event.getPlayer());
            PointsManager.addPoint(event.getPlayer());
            NametagManager.updateNametag(event.getPlayer());
            BossbarManager.updateBossbar(event.getPlayer());
            onSkipCooldown.add(event.getPlayer().getUniqueId());

            Bukkit.getScheduler().runTaskLater(ForceBattlePlugin.getInstance(), () -> onSkipCooldown.remove(event.getPlayer().getUniqueId()), 20);
        }
    }

    @EventHandler
    public void onPlayerSwap(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer())) return;
        if (onSwapCooldown.contains(event.getPlayer().getUniqueId())) return;
        if (event.getItem() != null && event.getItem().isSimilar(ItemProvider.getSwapitem(1))) {
            event.setCancelled(true);

            if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) {
                event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "Can't swap while timer is paused.");
                return;
            }

            if (ObjectiveManager.getObjective(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "No objective to swap.");
                return;
            }

            List<Player> availablePlayers = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.getPlayer().equals(player)) continue;
                if (ObjectiveManager.getObjective(player) == null) continue;
                availablePlayers.add(player);
            }

            if (availablePlayers.isEmpty()) {
                event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.RED + "No players available.");
                return;
            }

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);

            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

            Challenge playerChallenge = ObjectiveManager.getChallengeType(event.getPlayer());
            Challenge targetChallenge = ObjectiveManager.getChallengeType(target);

            Object playerObjective = ObjectiveManager.getObjective(event.getPlayer());
            Object targetObjective = ObjectiveManager.getObjective(target);

            ObjectiveManager.setChallengeType(event.getPlayer(), targetChallenge);
            ObjectiveManager.setChallengeType(target, playerChallenge);

            ObjectiveManager.setChallenge(event.getPlayer(), targetChallenge, targetObjective);
            ObjectiveManager.setChallenge(target, playerChallenge, playerObjective);

            event.getPlayer().setCooldown(stack.getType(), 20);

            target.sendMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + " swapped their objective with yours.");
            event.getPlayer().sendMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + "Your objective was swapped with " + target.getName() + "'s.");

            for (Player player3 : Bukkit.getOnlinePlayers()) {
                if (player3 != target && player3 != event.getPlayer()) {
                    player3.sendMessage(ForceBattlePlugin.prefix + ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with " + target.getName() + ".");
                }
            }

            target.playSound(target, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 20);

            NametagManager.updateNametag(target);
            NametagManager.updateNametag(event.getPlayer());
            BossbarManager.updateBossbar(target);
            BossbarManager.updateBossbar(event.getPlayer());
            onSwapCooldown.add(event.getPlayer().getUniqueId());

            Bukkit.getScheduler().runTaskLater(ForceBattlePlugin.getInstance(), () -> onSwapCooldown.remove(event.getPlayer().getUniqueId()), 20);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(ItemProvider.getSkipItem(1)) ||
                event.getItemDrop().getItemStack().isSimilar(ItemProvider.getSwapitem(1))) {
            event.setCancelled(true);
        }
    }
}