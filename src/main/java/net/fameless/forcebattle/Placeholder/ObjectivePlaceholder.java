package net.fameless.forcebattle.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.fameless.forcebattle.manager.BossbarManager;
import net.fameless.forcebattle.manager.ItemManager;
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
        return "Fxmeless";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return ItemManager.getChallenge(player) != null ? BossbarManager.formatItemName(ItemManager.getChallenge(player).toString().replace("_", " ")) :
                "Nothing";
    }
}
