package net.fameless.forcebattle.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemProvider {

    public static ItemStack buildItem(ItemStack item, List<Enchantment> enchantments, int level, List<ItemFlag> itemFlags, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (enchantments != null) {
            for (Enchantment enchantment : enchantments) {
                meta.addEnchant(enchantment, level, true);
            }
        }
        if (itemFlags != null) {
            for (ItemFlag flag : itemFlags) {
                meta.addItemFlags(flag);
            }
        }

        List<String> lores = new ArrayList<>();
        Collections.addAll(lores, lore);

        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    public static List<Enchantment> enchantments(Enchantment... enchantments) {
        List<Enchantment> list = new ArrayList<>();
        Collections.addAll(list, enchantments);
        return list;
    }

    public static List<ItemFlag> itemFlags(ItemFlag... itemFlags) {
        List<ItemFlag> list = new ArrayList<>();
        Collections.addAll(list, itemFlags);
        return list;
    }

    public static ItemStack getSkipItem(int amount) {
        return buildItem(new ItemStack(Material.BARRIER, amount), null, 0, null,
                ChatColor.GOLD + "Skip Item", ChatColor.BLUE + "Right click to skip item.");
    }

    public static ItemStack getSwapitem(int amount) {
        return buildItem(new ItemStack(Material.STRUCTURE_VOID, amount), null, 0, null,
                ChatColor.GOLD + "Swap Item", ChatColor.BLUE + "Right click to swap items with another player.");
    }
}