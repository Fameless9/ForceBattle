package net.fameless.forcebattle.gui.impl;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.gui.GUIItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TeamBackpackGUI extends ForceBattleGUI {
    private final int index;
    private final Team team;

    public TeamBackpackGUI(int index, int size, Team team) {
        super(Caption.getAsLegacy("gui.team_backpack_title",
                TagResolver.resolver("page", Tag.inserting(Component.text(String.valueOf(index + 1))))), size);
        this.index = index;
        this.team = team;
    }

    @Override
    public void setItems(final BattlePlayer player) {
        int inventoryPages = team.getBACKPACK_INVENTORIES().size();

        if (index < 0 || index >= inventoryPages) return;

        int nextArrowSlot = size - 1;
        int prevArrowSlot = size - 9;

        synchronized (items) {
            items.removeIf(item -> {
                if (item.slot == nextArrowSlot || item.slot == prevArrowSlot) {
                    return item instanceof GUIClickableItem;
                }
                return false;
            });
        }

        GUIItem nextItem = get(nextArrowSlot);
        if (nextItem instanceof GUIClickableItem) {
            getInventory().setItem(nextArrowSlot, null);
        }

        GUIItem prevItem = get(prevArrowSlot);
        if (prevItem instanceof GUIClickableItem) {
            getInventory().setItem(prevArrowSlot, null);
        }

        if (index < inventoryPages - 1) set(GUIClickableItem.getGoForthItem(size - 1, team.getBACKPACK_INVENTORIES().get(index + 1)));
        if (index > 0) set(GUIClickableItem.getGoBackItem(size - 9, team.getBACKPACK_INVENTORIES().get(index - 1)));
    }

    @Override
    public boolean allowItemMoving() {
        return true;
    }
}
