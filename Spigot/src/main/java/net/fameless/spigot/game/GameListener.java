package net.fameless.spigot.game;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.configuration.PluginUpdater;
import net.fameless.forceBattle.configuration.SettingsManager;
import net.fameless.forceBattle.game.Objective;
import net.fameless.forceBattle.util.BattleType;
import net.fameless.forceBattle.util.Format;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.player.BukkitPlayer;
import net.fameless.spigot.util.BukkitUtil;
import net.fameless.spigot.util.ItemUtils;
import net.fameless.spigot.util.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GameListener implements Listener {

    private final List<BukkitPlayer> skipCooldown = new ArrayList<>();
    private final List<BukkitPlayer> swapCooldown = new ArrayList<>();
    private boolean sentUpdateMessage = false;

    public GameListener() {
        runItemTask();
        runBiomeTask();
        runAdvancementTask();
        runHeightTask();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(event.getPlayer());
        if (bukkitPlayer.getObjective() == null) {
            bukkitPlayer.updateObjective(false);
        }

        if (!bukkitPlayer.hasReceivedSkip()) {
            bukkitPlayer.getPlatformPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSkipItem(BukkitPlatform.get().getConfig().getInt("skips", 3)));
            bukkitPlayer.setReceivedSkip(true);
        }
        if (!bukkitPlayer.hasReceivedSwap()) {
            bukkitPlayer.getPlatformPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSwapItem(BukkitPlatform.get().getConfig().getInt("swaps", 1)));
            bukkitPlayer.setReceivedSwap(true);
        }

        if (!PluginUpdater.isUpdated && !sentUpdateMessage && event.getPlayer().isOp()) {
            bukkitPlayer.sendMessage(Caption.of("notification.plugin_outdated"));
            sentUpdateMessage = true;
        }

        boolean firstStartup = BukkitPlatform.get().getConfig().getBoolean("first-startup", true);
        if (firstStartup && (event.getPlayer().hasPermission("forcebattle.timer") || event.getPlayer().isOp() ||
                event.getPlayer().hasPermission("forcebattle.settings"))) {
            bukkitPlayer.sendMessage(Caption.of("notification.first_startup"));
        }
    }

    private void runItemTask() {
        Bukkit.getScheduler().runTaskTimer(
                BukkitPlatform.get(), () -> {
                    if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) return;
                    if (!ForceBattle.getTimer().isRunning()) return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(player);
                        if (bukkitPlayer.isExcluded()) continue;

                        String objectiveString = bukkitPlayer.getObjective().getObjectiveString();
                        if (BukkitUtil.convertObjective(BattleType.FORCE_ITEM, objectiveString) instanceof Material objective) {
                            if (player.getInventory().contains(objective)) {
                                bukkitPlayer.sendMessage(Caption.of(
                                        "notification.objective_finished",
                                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(objectiveString))))
                                ));
                                Toast.display(
                                        player,
                                        objective,
                                        ChatColor.BLUE + Format.formatName(objectiveString),
                                        Toast.Style.GOAL,
                                        BukkitPlatform.get()
                                );
                                bukkitPlayer.updateObjective(true);
                            }
                        }
                    }
                }, 0, 3
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent damageEvent) {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) return;
        if (!ForceBattle.getTimer().isRunning()) return;
        if (!(damageEvent.getDamager() instanceof Player player)) return;

        if (!damageEvent.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) damageEvent.getEntity();
            if (entity.getHealth() - damageEvent.getDamage() > 0) {
                return;
            }
        }

        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(player);
        if (bukkitPlayer.isExcluded()) return;

        String objectiveString = bukkitPlayer.getObjective().getObjectiveString();
        if (!(BukkitUtil.convertObjective(BattleType.FORCE_MOB, objectiveString) instanceof EntityType requiredEntity)) return;

        if (damageEvent.getEntity().getType().equals(requiredEntity)) {
            if (!damageEvent.getEntity().isDead()) {
                LivingEntity entity = (LivingEntity) damageEvent.getEntity();
                if (entity.getHealth() - damageEvent.getDamage() > 0) {
                    return;
                }
            }
            bukkitPlayer.sendMessage(Caption.of(
                    "notification.objective_finished",
                    TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(objectiveString))))
            ));
            Toast.display(
                    player,
                    Material.DIAMOND_SWORD,
                    ChatColor.BLUE + Format.formatName(objectiveString),
                    Toast.Style.GOAL,
                    BukkitPlatform.get()
            );
            bukkitPlayer.updateObjective(true);
        }
    }

    private void runBiomeTask() {
        Bukkit.getScheduler().runTaskTimer(
                BukkitPlatform.get(), () -> {
                    if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) return;
                    if (!ForceBattle.getTimer().isRunning()) return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(player);
                        if (bukkitPlayer.isExcluded()) continue;

                        String objectiveString = bukkitPlayer.getObjective().getObjectiveString();
                        if (BukkitUtil.convertObjective(BattleType.FORCE_BIOME, objectiveString) instanceof Biome biome) {
                            if (player.getWorld().getBiome(player.getLocation()).equals(biome)) {
                                bukkitPlayer.sendMessage(Caption.of(
                                        "notification.objective_finished",
                                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(objectiveString))))
                                ));
                                Toast.display(
                                        player,
                                        Material.GRASS_BLOCK,
                                        ChatColor.BLUE + Format.formatName(objectiveString),
                                        Toast.Style.GOAL,
                                        BukkitPlatform.get()
                                );
                                bukkitPlayer.updateObjective(true);
                            }
                        }
                    }
                }, 0, 3
        );
    }

    private void runAdvancementTask() {
        Bukkit.getScheduler().runTaskTimer(
                BukkitPlatform.get(), () -> {
                    if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) return;
                    if (!ForceBattle.getTimer().isRunning()) return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(player);
                        if (bukkitPlayer.isExcluded()) continue;

                        String objectiveString = bukkitPlayer.getObjective().getObjectiveString();
                        if (BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objectiveString) instanceof net.fameless.spigot.util.Advancement advancement) {
                            if (hasAdvancement(player, advancement.getKey())) {
                                bukkitPlayer.sendMessage(Caption.of(
                                        "notification.objective_finished",
                                        TagResolver.resolver("objective", Tag.inserting(Component.text(advancement.getName())))
                                ));
                                if (BukkitPlatform.get().getConfig().getBoolean("reset-advancements-on-finish", true)) {resetAdvancements(player);}

                                Toast.display(
                                        player,
                                        Material.BREWING_STAND,
                                        ChatColor.BLUE + advancement.getName(),
                                        Toast.Style.GOAL,
                                        BukkitPlatform.get()
                                );
                                bukkitPlayer.updateObjective(true);
                            }
                        }
                    }
                }, 0, 3
        );
    }

    private void runHeightTask() {
        Bukkit.getScheduler().runTaskTimer(
                BukkitPlatform.get(), () -> {
                    if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) return;
                    if (!ForceBattle.getTimer().isRunning()) return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(player);
                        if (bukkitPlayer.isExcluded()) continue;

                        String objectiveString = bukkitPlayer.getObjective().getObjectiveString();
                        if (BukkitUtil.convertObjective(BattleType.FORCE_HEIGHT, objectiveString) instanceof Integer height) {
                            if (player.getLocation().getBlockY() == height) {
                                bukkitPlayer.sendMessage(Caption.of(
                                        "notification.objective_finished",
                                        TagResolver.resolver("objective", Tag.inserting(Component.text(height)))
                                ));
                                Toast.display(
                                        player,
                                        Material.SCAFFOLDING,
                                        ChatColor.BLUE + Format.formatName(objectiveString),
                                        Toast.Style.GOAL,
                                        BukkitPlatform.get()
                                );
                                bukkitPlayer.updateObjective(true);
                            }
                        }
                    }
                }, 0, 3
        );
    }

    private boolean hasAdvancement(Player player, NamespacedKey key) {
        @NotNull Optional<Advancement> advancementOptional = getAdvancement(key);
        if (advancementOptional.isPresent()) {
            AdvancementProgress progress = player.getAdvancementProgress(advancementOptional.get());
            return progress.isDone();
        }
        return false;
    }

    private @NotNull Optional<Advancement> getAdvancement(NamespacedKey key) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            Advancement a = it.next();
            if (a.getKey().equals(key)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    public void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    @EventHandler
    public void onPlayerSkip(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {return;}
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {return;}
        if (event.getItem() == null || !ItemUtils.isSkipItem(event.getItem())) {return;}

        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(event.getPlayer());
        if (bukkitPlayer.isExcluded()) return;
        if (skipCooldown.contains(bukkitPlayer)) return;

        event.setCancelled(true);

        if (!ForceBattle.getTimer().isRunning()) {
            bukkitPlayer.sendMessage(Caption.of("notification.skip_timer_paused"));
            return;
        }
        if (bukkitPlayer.getObjective() == null) {
            bukkitPlayer.sendMessage(Caption.of("notification.skip_no_objective"));
            return;
        }

        Objective oldObjective = bukkitPlayer.getObjective();

        bukkitPlayer.sendMessage(Caption.of(
                "notification.objective_skipped",
                TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(oldObjective.getObjectiveString()))))
        ));
        bukkitPlayer.updateObjective(true);

        decreaseItemAmount(event);

        if (oldObjective.getBattleType() == BattleType.FORCE_ITEM) {
            ItemStack itemStack = new ItemStack(Material.valueOf(oldObjective.getObjectiveString()));
            bukkitPlayer.getPlatformPlayer().getWorld().dropItemNaturally(bukkitPlayer.getPlatformPlayer().getLocation(), itemStack);
        }

        skipCooldown.add(bukkitPlayer);
        Bukkit.getScheduler().runTaskLater(BukkitPlatform.get(), () -> skipCooldown.remove(bukkitPlayer), 20);
    }

    @EventHandler
    public void onPlayerSwap(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null || !ItemUtils.isSwapItem(event.getItem())) return;

        BukkitPlayer bukkitPlayer = BukkitPlayer.adapt(event.getPlayer());
        if (bukkitPlayer.isExcluded()) return;
        if (swapCooldown.contains(bukkitPlayer)) return;
        event.setCancelled(true);

        if (!ForceBattle.getTimer().isRunning()) {
            bukkitPlayer.sendMessage(Caption.of("notification.swap_timer_paused"));
            return;
        }

        if (bukkitPlayer.getObjective() == null) {
            bukkitPlayer.sendMessage(Caption.of("notification.swap_no_objective"));
            return;
        }

        List<BukkitPlayer> availablePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getPlayer().equals(player)) {continue;}
            BukkitPlayer toSwap = BukkitPlayer.adapt(player);
            if (toSwap.getObjective() == null) {continue;}
            availablePlayers.add(toSwap);
        }

        if (availablePlayers.isEmpty()) {
            bukkitPlayer.sendMessage(Caption.of("notification.swap_no_player"));
            return;
        }

        decreaseItemAmount(event);
        Random random = new Random();
        BukkitPlayer targetPlayer = availablePlayers.get(random.nextInt(availablePlayers.size()));

        Objective playerObjective = bukkitPlayer.getObjective();
        Objective targetObjective = targetPlayer.getObjective();

        bukkitPlayer.setCurrentObjective(targetObjective, false);
        targetPlayer.setCurrentObjective(playerObjective, false);

        targetPlayer.sendMessage(Caption.of(
                "notification.swap_success_target",
                TagResolver.resolver("player", Tag.inserting(Component.text(bukkitPlayer.getName())))
        ));

        bukkitPlayer.sendMessage(Caption.of(
                "notification.swap_success_player",
                TagResolver.resolver("target", Tag.inserting(Component.text(targetPlayer.getName())))
        ));

        targetPlayer.getPlatformPlayer().playSound(targetPlayer.getPlatformPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 20);

        swapCooldown.add(bukkitPlayer);
        Bukkit.getScheduler().runTaskLater(BukkitPlatform.get(), () -> swapCooldown.remove(bukkitPlayer), 20);
    }

    private void decreaseItemAmount(@NotNull PlayerInteractEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        ItemStack stack = inventory.getItem(slot);
        if (stack != null) {
            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);
            event.getPlayer().setCooldown(stack.getType(), 20);
        }
    }

}
