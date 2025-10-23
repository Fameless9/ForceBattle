package net.fameless.forcebattle.gui.impl;

import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.gui.GUIItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.ItemStackCreator;
import net.fameless.forcebattle.util.Skull;
import net.fameless.forcebattle.util.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class CraftingRecipeGUI extends ForceBattleGUI {

    private final ItemStack result;
    private final List<Recipe> recipes;
    private final int page;
    private static final int[] GRID_SLOTS = {1, 2, 3, 10, 11, 12, 19, 20, 21};

    public CraftingRecipeGUI(ItemStack result) {
        this(result, 0);
    }

    private CraftingRecipeGUI(ItemStack result, int page) {
        super("Recipe for " + StringUtility.formatName(result.getType().name()) + " §7(" + (page + 1) + ")", 27);
        this.result = result;
        this.recipes = Bukkit.getRecipesFor(result);
        this.page = page;
    }

    @Override
    public void setItems(BattlePlayer player) {
        fill(ItemStackCreator.fillerItem());

        set(new GUIItem(16) {
            @Override
            public ItemStack getItem(BattlePlayer player) {
                return result;
            }
        });

        set(new GUIItem(14) {
            @Override
            public ItemStack getItem(BattlePlayer player) {
                return ItemStackCreator.getSkull(Skull.ARROW_RIGHT, " ", 1);
            }
        });

        for (int slot : GRID_SLOTS) {
            set(new GUIItem(slot) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    return ItemStackCreator.getStack(" ", Material.AIR, 1);
                }
            });
        }

        if (recipes.size() > 1) {
            if (page > 0) {
                set(GUIClickableItem.getGoBackItem(0, new CraftingRecipeGUI(result, page - 1)));
            }
            if (page < recipes.size() - 1) {
                set(GUIClickableItem.getGoForthItem(8, new CraftingRecipeGUI(result, page + 1)));
            }
        }

        if (recipes.isEmpty()) {
            set(new GUIItem(11) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    if (meta != null) meta.setDisplayName("§cNo recipe found");
                    barrier.setItemMeta(meta);
                    return barrier;
                }
            });
            return;
        }

        Recipe recipe = recipes.get(page);
        ItemStack[] grid = new ItemStack[9];

        if (recipe instanceof ShapedRecipe shaped) {
            Map<Character, RecipeChoice> choiceMap = shaped.getChoiceMap();
            String[] shape = shaped.getShape();
            for (int row = 0; row < Math.min(shape.length, 3); row++) {
                String line = shape[row];
                for (int col = 0; col < Math.min(line.length(), 3); col++) {
                    char c = line.charAt(col);
                    RecipeChoice choice = choiceMap.get(c);
                    if (choice instanceof RecipeChoice.MaterialChoice matChoice && !matChoice.getChoices().isEmpty()) {
                        grid[row * 3 + col] = new ItemStack(matChoice.getChoices().getFirst());
                    } else if (choice instanceof RecipeChoice.ExactChoice exact && !exact.getChoices().isEmpty()) {
                        grid[row * 3 + col] = exact.getChoices().getFirst().clone();
                    }
                }
            }

        } else if (recipe instanceof ShapelessRecipe shapeless) {
            List<RecipeChoice> ingredients = shapeless.getChoiceList();
            for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
                RecipeChoice choice = ingredients.get(i);
                if (choice instanceof RecipeChoice.MaterialChoice matChoice && !matChoice.getChoices().isEmpty()) {
                    grid[i] = new ItemStack(matChoice.getChoices().getFirst());
                } else if (choice instanceof RecipeChoice.ExactChoice exact && !exact.getChoices().isEmpty()) {
                    grid[i] = exact.getChoices().getFirst().clone();
                }
            }
        }

        int minRow = 3, maxRow = -1, minCol = 3, maxCol = -1;
        for (int i = 0; i < 9; i++) {
            if (grid[i] != null) {
                int row = i / 3, col = i % 3;
                minRow = Math.min(minRow, row);
                maxRow = Math.max(maxRow, row);
                minCol = Math.min(minCol, col);
                maxCol = Math.max(maxCol, col);
            }
        }

        int height = (maxRow - minRow + 1);
        int width = (maxCol - minCol + 1);
        int rowOffset = 0, colOffset = 0;

        if (height == 1) rowOffset = 1 - minRow;
        if (width == 1) colOffset = 1 - minCol;
        if (height == 1 && width == 1) {
            rowOffset = 1 - minRow;
            colOffset = 1 - minCol;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = grid[i];
            if (ingredient == null) continue;

            int row = i / 3 + rowOffset;
            int col = i % 3 + colOffset;
            if (row < 0 || row > 2 || col < 0 || col > 2) continue;

            int guiSlot = GRID_SLOTS[row * 3 + col];
            final ItemStack finalIngredient = ingredient;
            set(new GUIItem(guiSlot) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    return finalIngredient;
                }
            });
        }
    }

    @Override
    public boolean allowItemMoving() {
        return false;
    }
}
