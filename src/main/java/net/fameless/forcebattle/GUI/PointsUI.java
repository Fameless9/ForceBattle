package net.fameless.forcebattle.GUI;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.ItemProvider;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PointsUI implements CommandExecutor, Listener, InventoryHolder {

    private final ForceBattlePlugin forceBattlePlugin;
    
    private Inventory inventory;

    public PointsUI(ForceBattlePlugin forceBattlePlugin) {
        this.forceBattlePlugin = forceBattlePlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!sender.hasPermission("forcebattle.points")) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Lacking permission: 'forcebattle.points'");
            return false;
        }
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "You are not a player.");
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Specify a player. /points <player>.");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Player couldn't be found.");
            return false;
        }
        senderPlayer.openInventory(pointsGUI(target));

        return false;
    }

    private Inventory pointsGUI(Player target) {
        inventory = Bukkit.createInventory(this, 9, "Adjust points of " + target.getName());

        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), null, 0, null, "Adjust points",
                ChatColor.GOLD + "Current points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + forceBattlePlugin.getPointsManager().getPoints(target), "",
                ChatColor.GRAY + "Left click: +1", ChatColor.GRAY + "Right click: -1"));
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PointsUI)) return;
        if (event.getSlot() != 4) return;
        if (Bukkit.getPlayerExact(event.getView().getTitle().replace("Adjust points of ", "")) == null) return;
        event.setCancelled(true);
        Player target = Bukkit.getPlayer(event.getView().getTitle().replace("Adjust points of ", ""));
        if (target == null) return;
        if (event.getClick().equals(ClickType.RIGHT)) {
            forceBattlePlugin.getPointsManager().setPoints(target, forceBattlePlugin.getPointsManager().getPoints(target) - 1);
            forceBattlePlugin.getNametagManager().updateNametag(target);
            forceBattlePlugin.getBossbarManager().updateBossbar(target);
            target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Your points have been adjusted.");
            event.getWhoClicked().openInventory(pointsGUI(target));
            return;
        }
        if (event.getClick().equals(ClickType.LEFT)) {
            forceBattlePlugin.getPointsManager().setPoints(target, forceBattlePlugin.getPointsManager().getPoints(target) + 1);
            forceBattlePlugin.getNametagManager().updateNametag(target);
            forceBattlePlugin.getBossbarManager().updateBossbar(target);
            target.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GRAY + "Your points have been adjusted.");
            event.getWhoClicked().openInventory(pointsGUI(target));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
