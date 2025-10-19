package net.fameless.forcebattle.game;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.PluginUpdater;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.event.TimerEndEvent;
import net.fameless.forcebattle.event.TimerStartEvent;
import net.fameless.forcebattle.game.tasks.AdvancementTask;
import net.fameless.forcebattle.game.tasks.BiomeTask;
import net.fameless.forcebattle.game.tasks.CoordsTask;
import net.fameless.forcebattle.game.tasks.HeightTask;
import net.fameless.forcebattle.game.tasks.ItemTask;
import net.fameless.forcebattle.game.tasks.StructureTask;
import net.fameless.forcebattle.game.tasks.TaskManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.ItemUtils;
import net.fameless.forcebattle.util.StructureUtil;
import net.fameless.forcebattle.util.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameListener implements Listener {

    private final List<BattlePlayer> playerSkipCooldown = new ArrayList<>();
    private final List<BattlePlayer> teamSkipCooldown = new ArrayList<>();
    private final List<BattlePlayer> swapCooldown = new ArrayList<>();
    private boolean sentUpdateMessage = false;
    private boolean startPhase = true;
    public static boolean spawnCreated = false;
    public static Location spawn = new Location(Bukkit.getWorld("world"), 0.5, 201, 0.5);

    public GameListener() {
        TaskManager.register(new ItemTask());
        TaskManager.register(new BiomeTask());
        TaskManager.register(new AdvancementTask());
        TaskManager.register(new HeightTask());
        TaskManager.register(new CoordsTask());
        TaskManager.register(new StructureTask());

        TaskManager.startAll();
    }

    //TODO better tablist
    //TODO evtl /item => zeigt rezept an => wenn gui system done is
    //TODO commands von cords ausführen (for spawn)

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (ForceBattle.getTimer().isRunning()) {
            UUID uuid = event.getUniqueId();
            if (!BattlePlayer.getAllUUIDs().contains(uuid)) {

                StringBuilder contestants = new StringBuilder();

                if (!Team.teams.isEmpty()) {
                    for (Team team : Team.teams) {
                        contestants.append("§b• ").append(team.getId()).append(": §f");

                        List<BattlePlayer> players = team.getPlayers();
                        for (BattlePlayer player : players) {
                            contestants.append(player.getName()).append(", ");
                        }
                        if (!players.isEmpty()) {
                            contestants.setLength(contestants.length() - 2);
                        }
                        contestants.append("\n");
                    }
                } else {
                    for (BattlePlayer player : BattlePlayer.BATTLE_PLAYERS) {
                        contestants.append("§b• §f").append(player.getName()).append("\n");
                    }
                }

                String formattedTime = StringUtility.formatTime(ForceBattle.getTimer().getTime());

                final String kickMessage = "§c§l✖ A game is currently running! ✖\n\n" +
                        "§7⏳ Time remaining: §6" + formattedTime + "\n\n" +
                        "§b👥 §lCurrent Teams / Players:\n" +
                        "§f" + contestants;

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        BattlePlayer battlePlayer = BattlePlayer.adapt(event.getPlayer());

        if (!ForceBattle.getTimer().isRunning() && startPhase) {
            if (!spawnCreated) {
                StructureUtil.createSpawn();
                spawnCreated = true;
            }
            battlePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
            battlePlayer.getPlayer().teleport(spawn);
        }

        if (!PluginUpdater.isUpdated && !sentUpdateMessage && event.getPlayer().isOp()) {
            battlePlayer.sendMessage(Caption.of("notification.plugin_outdated"));
            sentUpdateMessage = true;
        }

        boolean firstStartup = ForceBattle.get().getConfig().getBoolean("first-startup", true);
        if (firstStartup && (event.getPlayer().hasPermission("forcebattle.timer") || event.getPlayer().isOp() ||
                event.getPlayer().hasPermission("forcebattle.settings"))) {
            battlePlayer.sendMessage(Caption.of("notification.first_startup"));
        }
    }

    @EventHandler
    public void onGameStart(TimerStartEvent event) throws IOException {
        startPhase = false;
        spawnCreated = false;
        World world = Bukkit.getWorld("world");
        ForceBattle.broadcast(Caption.of("notification.battle_started"));
        world.setTime(0);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(0);

        Bukkit.getStructureManager().deleteStructure(new NamespacedKey("forcebattle", "spawn"), true);

        Location spawnCorner = new Location(world, -5, 200, -5);
        int width = 11, height = 8, length = 11;

        for (int x1 = 0; x1 < width; x1++) {
            for (int y1 = 0; y1 < height; y1++) {
                for (int z1 = 0; z1 < length; z1++) {
                    spawnCorner.clone().add(x1, y1, z1).getBlock().setType(Material.AIR, false);
                }
            }
        }

        for (BattlePlayer player : BattlePlayer.getOnlinePlayers()) {
            if (player.getPlayer() == null) return;

            if (player.getObjective() == null) {
                player.updateObjective(false, false);
            }
            if (player.isInTeam() && player.getTeam().getObjective() == null) {
                player.getTeam().updateObjective(player, false, false);
            }
            player.getPlayer().playSound(player.getPlayer(), Sound.EVENT_RAID_HORN, SoundCategory.MASTER, 100, 1);
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.getPlayer().setHealth(20);
            player.getPlayer().setSaturation(20);
            player.getPlayer().setFoodLevel(20);
            player.getPlayer().setFireTicks(0);

            player.getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getPlayerSkipItem(ForceBattle.get().getConfig().getInt("playerSkips", 3)));
            player.getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getSwapItem(ForceBattle.get().getConfig().getInt("swaps", 1)));

            if (SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE) && player.isInTeam()) {
                player.getPlayer().getInventory().addItem(ItemUtils.SpecialItems.getTeamSkipItem(ForceBattle.get().getConfig().getInt("teamSkips", 1)));
            }

            player.getPlayer().teleport(world.getSpawnLocation());
        }
    }

    @EventHandler
    public void onGameEnd(TimerEndEvent event) {
        ForceBattle.broadcast(Caption.of("notification.battle_over"));

        for (BattlePlayer player : BattlePlayer.getOnlinePlayers()) {
            if (player.getPlayer() == null) return;

            player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
            player.getPlayer().setHealth(20);
            player.getPlayer().setSaturation(20);
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!ForceBattle.getTimer().isRunning()) {
            event.setCancelled(true);

            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!ForceBattle.getTimer().isRunning() && event.getEntity() instanceof Player) {
            event.setCancelled(true);
            return;
        }

        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB)) return;

        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        BattlePlayer battlePlayer = BattlePlayer.adapt(damager);
        if (battlePlayer.isExcluded()) return;

        if (target.getHealth() - event.getDamage() > 0) return;

        checkPlayerObjective(battlePlayer, target);
        checkTeamObjective(battlePlayer, target);
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, LivingEntity target) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_MOB) return;

        String objective = battlePlayer.getObjective().getObjectiveString();
        if (isTargetMatch(target, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, LivingEntity target) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_MOB) return;

        String objective = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isTargetMatch(target, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isTargetMatch(LivingEntity target, String objectiveString) {
        Object converted = BukkitUtil.convertObjective(BattleType.FORCE_MOB, objectiveString);
        return converted instanceof EntityType required && target.getType() == required;
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = StringUtility.formatName(objective);
        Material toastIcon = Material.DIAMOND_SWORD;
        ForceBattle plugin = ForceBattle.get();

        if (player.isInTeam() && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
            Component teammateMsg = Caption.of(
                    "notification.objective_finished_by_teammate",
                    TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
            );

            player.getTeam().getPlayers().stream()
                    .filter(teammate -> teammate != player)
                    .forEach(teammate -> {
                        teammate.sendMessage(teammateMsg);
                        Toast.display(teammate.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
                    });
        }

        player.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
        ));
        Toast.display(player.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
    }

    @EventHandler
    public void onPlayerSkip(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {return;}
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {return;}
        if (event.getItem() == null || !ItemUtils.isPlayerSkipItem(event.getItem())) {return;}

        BattlePlayer battlePlayer = BattlePlayer.adapt(event.getPlayer());
        if (battlePlayer.isExcluded()) return;
        if (playerSkipCooldown.contains(battlePlayer)) return;

        event.setCancelled(true);

        if (!ForceBattle.getTimer().isRunning()) {
            battlePlayer.sendMessage(Caption.of("notification.skip_timer_paused"));
            return;
        }
        if (battlePlayer.getObjective() == null) {
            battlePlayer.sendMessage(Caption.of("notification.skip_no_objective"));
            return;
        }

        Objective oldObjective = battlePlayer.getObjective();

        battlePlayer.sendMessage(Caption.of(
                "notification.objective_skipped",
                TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(oldObjective.getObjectiveString()))))
        ));
        battlePlayer.updateObjective(true, true);

        decreaseItemAmount(event);

        if (oldObjective.getBattleType() == BattleType.FORCE_ITEM) {
            ItemStack itemStack = new ItemStack(Material.valueOf(oldObjective.getObjectiveString()));
            battlePlayer.getPlayer().getWorld().dropItemNaturally(battlePlayer.getPlayer().getLocation(), itemStack);
        }
        playerSkipCooldown.add(battlePlayer);
        Bukkit.getScheduler().runTaskLater(ForceBattle.get(), () -> playerSkipCooldown.remove(battlePlayer), 20);
    }

    @EventHandler
    public void onTeamSkip(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {return;}
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {return;}
        if (event.getItem() == null || !ItemUtils.isTeamSkipItem(event.getItem())) {return;}

        BattlePlayer battlePlayer = BattlePlayer.adapt(event.getPlayer());
        Team team = battlePlayer.getTeam();
        if (battlePlayer.isExcluded()) return;
        if (teamSkipCooldown.contains(battlePlayer)) return;

        event.setCancelled(true);

        if (!ForceBattle.getTimer().isRunning()) {
            battlePlayer.sendMessage(Caption.of("notification.skip_timer_paused"));
            return;
        }
        if (team.getObjective() == null) {
            battlePlayer.sendMessage(Caption.of("notification.skip_no_objective"));
            return;
        }

        Objective oldObjective = team.getObjective();

        team.getPlayers().forEach(player -> {
            if (player != battlePlayer) {
                player.sendMessage(Caption.of(
                        "notification.objective_skipped_by_teammate",
                        TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                        TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(oldObjective.getObjectiveString()))))
                ));
            }
        });

        battlePlayer.sendMessage(Caption.of(
                "notification.objective_skipped",
                TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(oldObjective.getObjectiveString()))))
        ));

        team.updateObjective(battlePlayer, true, true);

        decreaseItemAmount(event);

        if (oldObjective.getBattleType() == BattleType.FORCE_ITEM) {
            ItemStack itemStack = new ItemStack(Material.valueOf(oldObjective.getObjectiveString()));
            battlePlayer.getPlayer().getWorld().dropItemNaturally(battlePlayer.getPlayer().getLocation(), itemStack);
        }
        teamSkipCooldown.add(battlePlayer);
        Bukkit.getScheduler().runTaskLater(ForceBattle.get(), () -> teamSkipCooldown.remove(battlePlayer), 20);
    }

    @EventHandler
    public void onPlayerSwap(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null || !ItemUtils.isSwapItem(event.getItem())) return;

        BattlePlayer battlePlayer = BattlePlayer.adapt(event.getPlayer());
        if (battlePlayer.isExcluded()) return;
        if (swapCooldown.contains(battlePlayer)) return;
        event.setCancelled(true);

        if (!ForceBattle.getTimer().isRunning()) {
            battlePlayer.sendMessage(Caption.of("notification.swap_timer_paused"));
            return;
        }

        if (battlePlayer.getObjective() == null) {
            battlePlayer.sendMessage(Caption.of("notification.swap_no_objective"));
            return;
        }

        List<BattlePlayer> availablePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getPlayer().equals(player)) continue;
            BattlePlayer toSwap = BattlePlayer.adapt(player);
            BattlePlayer sender = BattlePlayer.adapt(event.getPlayer());
            if (toSwap.getObjective() == null) continue;
            if (toSwap.getTeam() == sender.getTeam() && toSwap.isInTeam() && sender.isInTeam()) continue;
            availablePlayers.add(toSwap);
        }

        if (availablePlayers.isEmpty()) {
            battlePlayer.sendMessage(Caption.of("notification.swap_no_player"));
            return;
        }

        decreaseItemAmount(event);
        Random random = new Random();
        BattlePlayer targetPlayer = availablePlayers.get(random.nextInt(availablePlayers.size()));

        Objective playerObjective = battlePlayer.getObjective();
        Objective targetObjective = targetPlayer.getObjective();

        battlePlayer.setCurrentObjective(targetObjective, false, false);
        targetPlayer.setCurrentObjective(playerObjective, false, false);

        targetPlayer.sendMessage(Caption.of(
                "notification.swap_success_target",
                TagResolver.resolver("player", Tag.inserting(Component.text(battlePlayer.getName())))
        ));

        battlePlayer.sendMessage(Caption.of(
                "notification.swap_success_player",
                TagResolver.resolver("target", Tag.inserting(Component.text(targetPlayer.getName())))
        ));

        targetPlayer.getPlayer().playSound(targetPlayer.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 20);

        swapCooldown.add(battlePlayer);
        Bukkit.getScheduler().runTaskLater(ForceBattle.get(), () -> swapCooldown.remove(battlePlayer), 20);
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
