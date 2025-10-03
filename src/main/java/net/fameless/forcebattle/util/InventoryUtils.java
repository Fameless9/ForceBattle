package net.fameless.forcebattle.util;

import net.fameless.forcebattle.ForceBattle;
import org.bukkit.NamespacedKey;

import java.util.Collection;

public final class InventoryUtils {

    private static final NamespacedKey PAGE_KEY = new NamespacedKey(ForceBattle.get(), "pageKey");
    private static final NamespacedKey PLAYER_ID_KEY = new NamespacedKey(ForceBattle.get(), "playerIdKey");

    private InventoryUtils() {
    }

    /**
     * Determines if a given page number is valid for a collection of items.
     *
     * @param <T>        the type of elements in the collection
     * @param collection the collection of items to check
     * @param page       the page number to check
     * @param spaces     the number of items per page
     * @return true if the page number is valid, false otherwise
     * @throws IllegalArgumentException if the page number is negative
     */
    public static <T> boolean isPageValid(Collection<T> collection, int page, final int spaces) throws IllegalArgumentException {
        if (page <= 0) {
            return false;
        }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return collection.size() > lowerBound;
    }

}
