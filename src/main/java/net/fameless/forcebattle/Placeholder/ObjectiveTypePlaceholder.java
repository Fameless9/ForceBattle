package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.manager.ItemManager;
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
        return "Fxmeless";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (ItemManager.getChallengeType(player) == null) return "Nothing";
        switch (ItemManager.getChallengeType(player)) {
            case FORCE_ITEM: {
                return "Force Item";
            }
            case FORCE_MOB: {
                return "Force Mob";
            }
            case FORCE_BIOME: {
                return "Force Biome";
            }
            case FORCE_ADVANCEMENT: {
                return "Force Advancement";
            }
            case FORCE_HEIGHT: {
                return "Force Height";
            }
            default: {
                return "Nothing";
            }
        }
    }
}
