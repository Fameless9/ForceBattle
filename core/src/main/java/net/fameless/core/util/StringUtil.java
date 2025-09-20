package net.fameless.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class StringUtil {

    private StringUtil() {
    }

    @NotNull public static <T extends Collection<? super String>> T copyPartialMatches(
            @NotNull final String token,
            @NotNull final Iterable<String> originals,
            @NotNull final T collection
    ) throws UnsupportedOperationException, IllegalArgumentException {
        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }
        return collection;
    }

    public static boolean startsWithIgnoreCase(@NotNull final String string, @NotNull final String prefix) throws IllegalArgumentException, NullPointerException {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

}
