package net.fameless.forcebattle.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for creating {@link ItemStack} objects with various customizations,
 * including item materials, names, lore, and player skins.
 */
public class ItemStackCreator {

    /**
     * Creates an {@link ItemStack} with a specified material and custom name.
     *
     * @param material the material of the item stack
     * @param name     the custom name of the item stack
     * @return a new {@link ItemStack} with the specified properties
     */
    public static ItemStack createNamedItemStack(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack fillerItem() {
        return createNamedItemStack(Material.GRAY_STAINED_GLASS_PANE, " ");
    }

    /**
     * Creates an {@link ItemStack} with a custom name and lore list.
     *
     * @param name     the display name
     * @param material the item material
     * @param amount   the stack amount
     * @param lore     a list of lore lines
     * @return a new {@link ItemStack} with the specified name and lore
     */
    public static ItemStack getStack(String name, Material material, int amount, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.setLore(lore.stream().map(ItemStackCreator::color).collect(Collectors.toList()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates an {@link ItemStack} with a custom name and lore list.
     *
     * @param name     the display name
     * @param material the item material
     * @param lore     a list of lore lines
     * @return a new {@link ItemStack} with the specified name and lore
     */
    public static ItemStack getStack(String name, Material material, List<String> lore) {
        return getStack(name, material, 1, lore);
    }

    /**
     * Creates an {@link ItemStack} with a name and multiple lore lines.
     *
     * @param name     the display name
     * @param material the item material
     * @param amount   the stack amount
     * @param lore     one or more lore lines
     * @return a new {@link ItemStack} with the specified name and lore
     */
    public static ItemStack getStack(String name, Material material, int amount, String... lore) {
        return getStack(name, material, amount, Arrays.asList(lore));
    }

    /**
     * Applies an enchantment glint to the given {@link ItemStack}.
     *
     * @param item the {@link ItemStack} to modifiy
     * @return the same {@link ItemStack} with a glint effect
     */
    public static ItemStack enchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates a textured skull item from a predefined {@link Skull} enum constant.
     *
     * @param skull   the {@link Skull} enum value
     * @param name    the display name
     * @param amount  the stack amount
     * @param lore    the lore lines
     * @return a new {@link ItemStack} representing the skull
     */
    public static ItemStack getSkull(Skull skull, String name, int amount, List<String> lore) {
        ItemStack item = skull.asItemStack().clone();
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.setLore(lore.stream().map(ItemStackCreator::color).collect(Collectors.toList()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates a skull item from a {@link Skull} enum with varargs lore.
     *
     * @param skull   the skull type
     * @param name    the display name
     * @param amount  the stack amount
     * @param lore    one or more lore lines
     * @return a new {@link ItemStack} representing the skull
     */
    public static ItemStack getSkull(Skull skull, String name, int amount, String... lore) {
        return getSkull(skull, name, amount, Arrays.asList(lore));
    }

    /**
     * Creates a player head from a player's {@link UUID}.
     *
     * @param uuid    the player's unique ID
     * @param name    the display name
     * @param amount  the stack amount
     * @param lore    one or more lore lines
     * @return a new {@link ItemStack} representing the player's head
     */
    public static ItemStack getPlayerSkull(UUID uuid, String name, int amount, String... lore) {
        ItemStack skull = Skull.PlayerSkulls.getSkullByUUID(uuid);
        skull.setAmount(amount);

        ItemMeta meta = skull.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.setLore(Arrays.stream(lore).map(ItemStackCreator::color).collect(Collectors.toList()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    /**
     * Creates an item with a single wrapped lore line split at word boundaries (max 30 characters per line).
     *
     * @param name     the display name
     * @param color    the color prefix for each lore line
     * @param material the item material
     * @param amount   the stack amount
     * @param lore     the long lore text to wrap
     * @return a new {@link ItemStack} with formatted lore
     */
    public static ItemStack getSingleLoreStack(String name, String color, Material material, int amount, String lore) {
        List<String> loreLines = StringUtility.splitByWordAndLength(lore, 30).stream()
                .map(line -> color + line)
                .collect(Collectors.toList());
        return getStack(name, material, amount, loreLines);
    }

    /**
     * Replaces color codes in the given string with Minecraft color codes.
     *
     * @param input the input string with color codes
     * @return the string with color codes replaced
     */
    public static String color(String input) {
        return input == null ? "" : input.replace("&", "§");
    }

}
