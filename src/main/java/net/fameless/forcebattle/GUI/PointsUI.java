package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.NametagManager;
import net.fameless.forcebattle.manager.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static net.fameless.forcebattle.util.ItemProvider.buildItem;

public class PointsUI implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not a player.");
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Specify a player. /points <player>.");
            return false;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        ((Player) sender).openInventory(pointsGUI(player.getName()));

        return false;
    }

    private Inventory pointsGUI(String targetName) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Adjust points of " + targetName);

        inventory.setItem(4, buildItem(new ItemStack(Material.GOLD_NUGGET), null, 0, null, "Adjust points",
                ChatColor.GOLD + "Current points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(Bukkit.getPlayer(targetName)), "",
                        ChatColor.GRAY + "Left click: +1", ChatColor.GRAY + "Right click: -1"));
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Adjust points of ")) return;
        if (event.getSlot() != 4) return;
        if (Bukkit.getPlayerExact(event.getView().getTitle().replace("Adjust points of ", "")) == null) return;
        event.setCancelled(true);
        if (event.getClick().equals(ClickType.RIGHT)) {
            Player target = Bukkit.getPlayer(event.getView().getTitle().replace("Adjust points of ", ""));
            PointsManager.setPoints(target, PointsManager.getPoints(target) - 1);
            NametagManager.updateNametag(target);
            BossbarManager.updateBossbar(target);
            target.sendMessage(ChatColor.GOLD + "Your points have been adjusted.");
            event.getWhoClicked().openInventory(pointsGUI(target.getName()));
            return;
        }
        if (event.getClick().equals(ClickType.LEFT)) {
            Player target = Bukkit.getPlayer(event.getView().getTitle().replace("Adjust points of ", ""));
            PointsManager.setPoints(target, PointsManager.getPoints(target) + 1);
            NametagManager.updateNametag(target);
            BossbarManager.updateBossbar(target);
            target.sendMessage(ChatColor.GOLD + "Your points have been adjusted.");
            event.getWhoClicked().openInventory(pointsGUI(target.getName()));
        }
    }
}
