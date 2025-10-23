package net.fameless.forcebattle.gui.impl;

import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.player.BattlePlayer;

public class TeamBackpackGUI extends ForceBattleGUI {

    private final int index;

    public TeamBackpackGUI(int index, int size) {
        super("Team Backpack Page: " + (index + 1), size);
        this.index = index;
    }

    @Override
    public void setItems(final BattlePlayer player) {
        Team team = player.getTeam();
        int inventoryPages = team.getBACKPACK_INVENTORIES().size();

        if (index < 0 || index >= inventoryPages) return;

        if (index < inventoryPages - 1) {
            set(GUIClickableItem.getGoForthItem(size - 1, team.getBACKPACK_INVENTORIES().get(index + 1)));
        }

        if (index > 0) {
            set(GUIClickableItem.getGoBackItem(size - 9, team.getBACKPACK_INVENTORIES().get(index - 1)));
        }
    }

    @Override
    public boolean allowItemMoving() {
        return true;
    }
}
