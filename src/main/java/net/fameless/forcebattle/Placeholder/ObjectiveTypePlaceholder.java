package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Challenge;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObjectiveTypePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "type";
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
        Challenge challenge = ForceBattlePlugin.get().getObjectiveManager().getChallengeType(player);
        if (challenge == null) return "Nothing";
        return switch (challenge) {
            case FORCE_ITEM -> "Force Item";
            case FORCE_MOB -> "Force Mob";
            case FORCE_BIOME -> "Force Biome";
            case FORCE_ADVANCEMENT -> "Force Advancement";
            case FORCE_HEIGHT -> "Force Height";
        };
    }
}
