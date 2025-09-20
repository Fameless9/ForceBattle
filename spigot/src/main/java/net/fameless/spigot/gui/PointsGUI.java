package net.fameless.spigot.gui;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.Command;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.Format;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.util.ItemData;
import net.fameless.spigot.util.ItemUtils;
import net.fameless.spigot.util.Skull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class PointsGUI implements Listener, InventoryHolder {

    public PointsGUI() {
        Command.forId("points")
                .ifPresent(command -> command.onExecute(
                        (caller, args) -> {
                            if (args.length < 2) return;
                            if (!args[0].equalsIgnoreCase("gui")) return;
                            if (!(caller instanceof BattlePlayer<?> battlePlayer)) {
                                caller.sendMessage(Caption.of("command.not_a_player"));
                                return;
                            }

                            BattlePlayer<?> target = BattlePlayer.of(args[1]).orElse(null);
                            if (target == null) {
                                caller.sendMessage(Caption.of("command.no_such_player"));
                                return;
                            }

                            battlePlayer.openInventory(getPointsGUI(target));
                        }
                ));
    }

    public Inventory getPointsGUI(@NotNull BattlePlayer<?> target) {
        Inventory inventory = Bukkit.createInventory(
                this, 9, Caption.getAsLegacy(
                        "gui.points_title",
                        TagResolver.resolver("target", Tag.inserting(Component.text(target.getName())))
                )
        );

        inventory.setItem(
                2, ItemUtils.addData(
                        new ItemUtils.ItemBuilder()
                                .itemStack(Skull.MINUS_SIGN.asItemStack())
                                .name(Caption.getAsLegacy("gui.points_remove_item_name"))
                                .lore(Format.formatLineBreaks(Caption.getAsLegacy(
                                        "gui.points_remove_item_lore",
                                        TagResolver.resolver("target", Tag.inserting(Component.text(target.getName()))),
                                        TagResolver.resolver("points", Tag.inserting(Component.text(String.valueOf(target.getPoints()))))
                                )))
                                .build()
                        , new ItemData<>(new NamespacedKey(BukkitPlatform.get(), "target_uuid"), PersistentDataType.STRING, target.getUniqueId().toString())
                )
        );

        inventory.setItem(
                4, ItemUtils.buildItem(
                        Skull.PlayerSkulls.getSkullByUUID(target.getUniqueId()),
                        ChatColor.GRAY + target.getName() + " - " + target.getPoints(), new ArrayList<>(), null, null
                )
        );

        inventory.setItem(
                6, new ItemUtils.ItemBuilder()
                        .itemStack(Skull.PLUS_SIGN.asItemStack())
                        .name(Caption.getAsLegacy("gui.points_add_item_name"))
                        .lore(Format.formatLineBreaks(Caption.getAsLegacy(
                                "gui.points_add_item_lore",
                                TagResolver.resolver("target", Tag.inserting(Component.text(target.getName()))),
                                TagResolver.resolver("points", Tag.inserting(Component.text(String.valueOf(target.getPoints()))))
                        )))
                        .build()
        );

        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PointsGUI)) return;
        event.setCancelled(true);

        UUID targetUUID = UUID.fromString(
                event.getInventory().getItem(2).getItemMeta().getPersistentDataContainer()
                        .get(new NamespacedKey(BukkitPlatform.get(), "target_uuid"), PersistentDataType.STRING)
        );

        BattlePlayer<?> whoClicked = BattlePlayer.of(event.getWhoClicked().getUniqueId()).orElse(null);
        BattlePlayer<?> target = BattlePlayer.of(targetUUID).orElse(null);
        if (target == null || whoClicked == null) {
            event.getWhoClicked().closeInventory();
            return;
        }

        switch (event.getSlot()) {
            case 2 -> {
                if (target.getPoints() > 0) {
                    target.setPoints(target.getPoints() - 1);
                }
            }
            case 6 -> target.setPoints(target.getPoints() + 1);
        }
        whoClicked.openInventory(getPointsGUI(target));
    }


    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}
