package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.ItemProvider;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class NewWorldCommand implements CommandExecutor, Listener, InventoryHolder {

    private final NamespacedKey seedKey = new NamespacedKey(ForceBattlePlugin.get(), "seed");
    private final List<String> toDelete = new ArrayList<>();
    private final Random random = new Random();
    private final Properties properties = new Properties();
    private final File serverProperties = new File(Bukkit.getWorldContainer(), "server.properties");

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("forcebattle.newworld")) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Lacking permission: forcebattle.newworld.");
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "Only players may reset worlds.");
            return false;
        }
        long seed = random.nextLong();
        if (args.length > 0) {
            try {
                seed = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Seed must be a number between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE);
                return false;
            }
        }
        player.openInventory(getConfirmInv(seed));

        return false;
    }

    private Inventory getConfirmInv(Long seed) {
        Inventory inventory = Bukkit.createInventory(this, 9, "CONFIRM RESET");
        ItemStack item = ItemProvider.buildItem(new ItemStack(Material.LIME_DYE), null, 0, null, ChatColor.GREEN.toString() + ChatColor.BOLD + "CONFIRM",
                ChatColor.GOLD + "Click to create world and teleport everyone there.",
                "",
                ChatColor.RED + "Old worlds will be deleted on server reload, ",
                ChatColor.RED + "unless manually removed from config.yml!");
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(seedKey, PersistentDataType.LONG, seed);
        item.setItemMeta(meta);
        inventory.setItem(4, item);
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof NewWorldCommand)) return;
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            long seed = event.getInventory().getItem(4).getItemMeta().getPersistentDataContainer().get(seedKey, PersistentDataType.LONG);
            player.closeInventory();
            resetWorld(player, seed);
        }
    }

    private void resetWorld(Player initiator, Long seed) {
        Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Creating a new world...");
        toDelete.add(initiator.getWorld().getName());
        String worldName = UUID.randomUUID().toString();

        World world = new WorldCreator(worldName)
                .seed(seed)
                .createWorld();
        if (world == null) {
            initiator.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "An error occurred in world generation. Report this issue on GitHub: https://github.com/Fameless9/ForceBattle/issues");
            Bukkit.broadcastMessage(ForceBattlePlugin.PREFIX + ChatColor.RED + "World creation cancelled.");
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(world.getSpawnLocation());
            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "A new world has been created and you have been teleported.");
        }

        properties.setProperty("level-name", worldName);
        try {
            properties.store(new FileWriter(serverProperties), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return getConfirmInv(random.nextLong());
    }

    public List<String> getToDelete() {
        return toDelete;
    }
}