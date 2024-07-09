package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Format;
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
        return ForceBattlePlugin.get().getObjectiveManager().getObjective(player) != null ? Format.formatName(ForceBattlePlugin.get().getObjectiveManager().getObjective(player).toString()) :
                "Nothing";
    }
}
