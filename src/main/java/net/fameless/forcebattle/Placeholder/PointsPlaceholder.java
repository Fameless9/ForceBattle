package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.manager.PointsManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PointsPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "points";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Fameless9";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return String.valueOf(PointsManager.getPoints(player));
    }
}
