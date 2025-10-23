package net.fameless.forcebattle.util;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling ItemStack operations.
 */
public final class ItemUtils {

    private static final NamespacedKey PLAYER_SKIP_KEY = new NamespacedKey(ForceBattle.get(), "playerSkipItem");
    private static final NamespacedKey TEAM_SKIP_KEY = new NamespacedKey(ForceBattle.get(), "teamSkipItem");
    private static final NamespacedKey SWAP_KEY = new NamespacedKey(ForceBattle.get(), "swapItem");

    private ItemUtils() {
    }

    /**
     * Checks if the given ItemStack is a swap item.
     *
     * @param stack the ItemStack to check
     * @return true if the ItemStack is a swap item, false otherwise
     */
    public static boolean isSwapItem(@NotNull ItemStack stack) {
        return hasData(stack, SWAP_KEY);
    }

    /**
     * Checks if the given ItemStack is a playerSkip item.
     *
     * @param stack the ItemStack to check
     * @return true if the ItemStack is a playerSkip item, false otherwise
     */
    public static boolean isPlayerSkipItem(@NotNull ItemStack stack) {
        return hasData(stack, PLAYER_SKIP_KEY);
    }

    /**
     * Checks if the given ItemStack is a teamSkip item.
     *
     * @param stack the ItemStack to check
     * @return true if the ItemStack is a teamSkip item, false otherwise
     */
    public static boolean isTeamSkipItem(@NotNull ItemStack stack) {
        return hasData(stack, TEAM_SKIP_KEY);
    }

    public static boolean hasData(@NotNull ItemStack stack, NamespacedKey key) {
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(key);
    }

    /**
     * Adds custom data to the given ItemStack.
     */
    @SafeVarargs
    public static @NotNull ItemStack addData(
            @NotNull ItemStack stack,
            ItemData<?, ?>... itemData
    ) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            for (ItemData<?, ?> data : itemData) {
                apply(meta, data);
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static <P, C> void apply(ItemMeta meta, ItemData<P, C> data) {
        meta.getPersistentDataContainer().set(
                data.namespacedKey(),
                data.persistentDataType(),
                data.data()
        );
    }

    /**
     * Retrieves custom data from the given ItemStack.
     *
     * @param stack              the ItemStack to retrieve data from
     * @param key                the key of the data
     * @param persistentDataType the type of the data
     * @param <P>                the primitive type of the data
     * @param <C>                the complex type of the data
     * @return the retrieved data, or null if not present
     */
    @Nullable public static <P, C> C getData(
            @NotNull ItemStack stack,
            NamespacedKey key,
            PersistentDataType<P, C> persistentDataType
    ) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(key, persistentDataType);
        }
        return null;
    }

    /**
     * Builds an ItemStack with the specified properties.
     *
     * @param stack               the ItemStack to modify
     * @param name                the display name of the item
     * @param lore                the lore of the item
     * @param enchantmentLevelMap a map of enchantments and their levels
     * @param itemFlags           a list of item flags
     * @return the modified ItemStack
     */
    @Contract("_, _, _, _, _ -> param1")
    @NotNull public static ItemStack buildItem(
            @NotNull ItemStack stack,
            String name,
            List<String> lore,
            @Nullable Map<Enchantment, Integer> enchantmentLevelMap,
            @Nullable List<ItemFlag> itemFlags
    ) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        if (name != null) {
            meta.setDisplayName(name);
        }
        if (lore != null) {
            meta.setLore(lore);
        }

        if (itemFlags != null) {
            itemFlags.forEach(meta::addItemFlags);
        }

        if (enchantmentLevelMap != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantmentLevelMap.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Builder class for creating ItemStack with a fluent API.
     */
    public static class ItemBuilder {

        private ItemStack stack;
        private Material type = Material.BARRIER;
        private int amount = 1;
        private String name = "";
        private List<String> lore = new ArrayList<>();
        private HashMap<Enchantment, Integer> enchantmentLevelMap = new HashMap<>();
        private List<ItemFlag> flags = new ArrayList<>();

        public ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder addLore(String loreLine) {
            this.lore.add(loreLine);
            return this;
        }

        public ItemBuilder enchantments(HashMap<Enchantment, Integer> enchantmentLevelMap) {
            this.enchantmentLevelMap = enchantmentLevelMap;
            return this;
        }

        public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
            this.enchantmentLevelMap.put(enchantment, level);
            return this;
        }

        public ItemBuilder flags(List<ItemFlag> flags) {
            this.flags = flags;
            return this;
        }

        public ItemBuilder addFlag(ItemFlag flag) {
            this.flags.add(flag);
            return this;
        }

        public ItemBuilder addFlags(ItemFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public ItemBuilder itemStack(ItemStack itemStack) {
            this.stack = itemStack;
            return this;
        }

        public ItemStack build() {
            if (stack == null) {
                stack = new ItemStack(type, amount);
            }
            return ItemUtils.buildItem(stack, name, lore, enchantmentLevelMap, flags);
        }

    }

    /**
     * SpecialItems nested class for handling distinct item types like skip and swap items.
     */
    public static class SpecialItems {

        public static @NotNull ItemStack getSwapItem(int amount) {
            return createSpecialItem(
                    Material.STRUCTURE_VOID,
                    Caption.getAsLegacy("item.swap_item_name"),
                    Caption.getAsLegacy("item.swap_item_lore"),
                    SWAP_KEY,
                    amount
            );
        }

        public static @NotNull ItemStack getPlayerSkipItem(int amount) {
            return createSpecialItem(
                    Material.BARRIER,
                    Caption.getAsLegacy("item.player_skip_item_name"),
                    Caption.getAsLegacy("item.player_skip_item_lore"),
                    PLAYER_SKIP_KEY,
                    amount
            );
        }

        public static @NotNull ItemStack getTeamSkipItem(int amount) {
            return createSpecialItem(
                    Material.KNOWLEDGE_BOOK,
                    Caption.getAsLegacy("item.team_skip_item_name"),
                    Caption.getAsLegacy("item.team_skip_item_lore"),
                    TEAM_SKIP_KEY,
                    amount
            );
        }

        public static @NotNull ItemStack getSwapItem() {
            return getSwapItem(1);
        }

        public static @NotNull ItemStack getPlayerSkipItem() {
            return getPlayerSkipItem(1);
        }

        public static @NotNull ItemStack getTeamSkipItem() { return getTeamSkipItem(1); }

        public static @NotNull ItemStack getFillerItem() {
            return new ItemBuilder()
                    .type(Material.GRAY_STAINED_GLASS_PANE)
                    .name("") // No name for filler items
                    .lore(List.of()) // No lore for filler items
                    .build();
        }

        private static @NotNull ItemStack createSpecialItem(
                Material material,
                String name,
                String lore,
                NamespacedKey key,
                int amount
        ) {
            return ItemUtils.addData(
                    new ItemBuilder()
                            .type(material)
                            .amount(amount)
                            .name(name)
                            .lore(StringUtility.formatLineBreaks(lore))
                            .build(),
                    new ItemData<>(key, PersistentDataType.INTEGER, 1)
            );
        }

    }

}
