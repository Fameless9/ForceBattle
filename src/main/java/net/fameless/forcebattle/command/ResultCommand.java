package net.fameless.forcebattle.command;

import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.FormatTime;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResultCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/result <player>");
            return false;
        }
        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Player couldn't be found.");
            return false;
        }
        if (sender instanceof Player player) {
            if (Timer.isRunning()) {
                sender.sendMessage(ChatColor.AQUA + "Only available when timer is not running.");
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            new GUI(player, target, 1);
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUI)) return;
        if (event.getCurrentItem() == null) return;

        Player target = Bukkit.getPlayer(event.getInventory().getItem(8).getItemMeta().getLocalizedName());
        int page = Integer.parseInt(event.getInventory().getItem(0).getItemMeta().getLocalizedName());

        if (event.getRawSlot() == 0 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), target, page - 1);
        } else if (event.getRawSlot() == 8 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), target, page + 1);
        }
        event.setCancelled(true);
    }

    static class GUI implements InventoryHolder {
        private Inventory inventory;

        public GUI(Player player, Player target, int page) {
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Target player is not available anymore!");
                player.closeInventory();
                return;
            }

            inventory = Bukkit.createInventory(this, 54, "Results " + target.getName() + " | Page " + page);
            List<Object> allObjectives = ObjectiveManager.finishedObjectives.get(target.getUniqueId());

            ItemStack left;
            ItemMeta leftMeta;
            if (PageUtil.isPageValid(allObjectives, page - 1, 52)) {
                left = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                leftMeta = left.getItemMeta();
                if (leftMeta != null) {
                    leftMeta.setDisplayName(ChatColor.GREEN + "Go page left!");
                }
            } else {
                left = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                leftMeta = left.getItemMeta();
                if (leftMeta != null) {
                    leftMeta.setDisplayName(ChatColor.RED + "Can't go left!");
                }
            }

            if (leftMeta != null) {
                leftMeta.setLocalizedName(page + "");
                left.setItemMeta(leftMeta);
            }

            ItemStack right;
            ItemMeta rightMeta;

            if (PageUtil.isPageValid(allObjectives, page + 1, 52)) {
                right = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                if (rightMeta != null) {
                    rightMeta.setDisplayName(ChatColor.GREEN + "Go page right!");
                }
            } else {
                right = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                if (rightMeta != null) {
                    rightMeta.setDisplayName(ChatColor.RED + "Can't go right!");
                }
            }

            if (rightMeta != null) {
                rightMeta.setLocalizedName(target.getName());
                right.setItemMeta(rightMeta);
            }

            inventory.setItem(0, left);
            inventory.setItem(8, right);

            for (ItemStack itemStack : PageUtil.getPageItems(allObjectives, ObjectiveManager.objectiveTimeMap.get(player.getUniqueId()), page, 52)) {
                inventory.addItem(itemStack);
            }
            player.openInventory(inventory);
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    static class PageUtil {
        public static List<ItemStack> getPageItems(List<Object> objectives, List<Integer> timeList, int page, int spaces) {
            int startIndex = (page - 1) * spaces;
            int endIndex = Math.min(startIndex + spaces, objectives.size());

            List<ItemStack> newObjectives = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                if (i >= 0 && i < objectives.size()) {
                    Object object = objectives.get(i);
                    Integer time = timeList.get(i);

                    if (object instanceof Material material) {
                        newObjectives.add(ItemProvider.buildItem(new ItemStack(material), null, 0, null,
                                ChatColor.GOLD + "Item: " + BossbarManager.formatItemName(material.name().replace("_", " ")),
                                "", ChatColor.GRAY + "Time: " + FormatTime.toFormatted(time)));
                        continue;
                    }
                    if (object instanceof EntityType entityType) {
                        newObjectives.add(ItemProvider.buildItem(new ItemStack(Material.SPIDER_SPAWN_EGG), null, 0, null,
                                ChatColor.GOLD + "Mob: " + BossbarManager.formatItemName(entityType.name().replace("_", " ")),
                                "", ChatColor.GRAY + "Time: " + FormatTime.toFormatted(time)));
                        continue;
                    }
                    if (object instanceof Biome biome) {
                        newObjectives.add(ItemProvider.buildItem(new ItemStack(Material.GRASS_BLOCK), null, 0, null,
                                ChatColor.GOLD + "Biome: " + BossbarManager.formatItemName(biome.name().replace("_", " ")),
                                "", ChatColor.GRAY + "Time: " + FormatTime.toFormatted(time)));
                        continue;
                    }
                    if (object instanceof Advancement advancement) {
                        newObjectives.add(ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), null, 0, null,
                                ChatColor.GOLD + "Advancement: " + BossbarManager.formatItemName(advancement.name().replace("_", " ")),
                                "", ChatColor.GRAY + "Time: " + FormatTime.toFormatted(time)));
                        continue;
                    }
                    if (object instanceof Integer height) {
                        newObjectives.add(ItemProvider.buildItem(new ItemStack(Material.SCAFFOLDING), null, 0, null,
                                ChatColor.GOLD + "Height: " + height,
                                "", ChatColor.GRAY + "Time: " + FormatTime.toFormatted(time)));
                    }
                }
            }
            return newObjectives;
        }

        public static boolean isPageValid(List<Object> objectives, int page, int spaces) {
            if (page <= 0) return false;

            int upperBound = page * spaces;
            int lowerBound = upperBound - spaces;

            return objectives.size() > lowerBound;
        }
    }
}
