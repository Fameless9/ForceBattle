package net.fameless.forcebattle.command;

import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.gui.impl.CraftingRecipeGUI;
import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeCommand extends Command {

    public RecipeCommand() {
        super(
                "recipe",
                List.of(),
                CallerType.PLAYER,
                "/recipe <item>",
                "forcebattle.recipe",
                "Opens the crafting recipe GUI for the specified item"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (!(caller instanceof BattlePlayer player)) return;

        if (args.length == 0) {
            player.getPlayer().sendMessage("§cUsage: /item <item>");
            return;
        }

        String input = args[0].toUpperCase();
        Material material = Material.matchMaterial(input);

        if (material == null) {
            player.getPlayer().sendMessage("§cInvalid item: " + args[0]);
            return;
        }

        ItemStack stack = new ItemStack(material);
        new CraftingRecipeGUI(stack).open(player);
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toUpperCase();
            List<String> suggestions = new ArrayList<>();
            for (Material mat : Material.values()) {
                if (mat.isItem() && mat.name().startsWith(prefix)) {
                    suggestions.add(mat.name().toLowerCase());
                }
            }
            return suggestions;
        }
        return List.of();
    }
}
