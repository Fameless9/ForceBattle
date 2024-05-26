package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.ObjectiveManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObjectivePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "objective";
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
        return ObjectiveManager.getChallenge(player) != null ? BossbarManager.formatItemName(ObjectiveManager.getChallenge(player).toString().replace("_", " ")) :
                "Nothing";
    }
}
