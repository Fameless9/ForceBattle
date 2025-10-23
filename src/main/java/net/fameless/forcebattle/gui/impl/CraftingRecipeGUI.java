package net.fameless.forcebattle.gui.impl;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.gui.GUIItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.ItemStackCreator;
import net.fameless.forcebattle.util.Skull;
import net.fameless.forcebattle.util.StringUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
    private static final int TRANSFORM_SLOT = 5;

    public CraftingRecipeGUI(ItemStack result) {
        this(result, 0);
    }

    private CraftingRecipeGUI(ItemStack result, int page) {
        super(Caption.getAsLegacy("gui.recipe_title",
                TagResolver.resolver("material", Tag.inserting(Component.text(StringUtility.formatName(result.getType().name())))),
                TagResolver.resolver("page", Tag.inserting(Component.text(String.valueOf(page + 1))))), 27);
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
            if (page > 0)
                set(GUIClickableItem.getGoBackItem(0, new CraftingRecipeGUI(result, page - 1)));
            if (page < recipes.size() - 1)
                set(GUIClickableItem.getGoForthItem(8, new CraftingRecipeGUI(result, page + 1)));
        }

        if (recipes.isEmpty()) {
            set(new GUIItem(11) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    if (meta != null) meta.setDisplayName("§c" + Caption.getAsLegacy("gui.no_recipe_found"));
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
                    grid[row * 3 + col] = getChoiceItem(choice);
                }
            }

        } else if (recipe instanceof ShapelessRecipe shapeless) {
            List<RecipeChoice> ingredients = shapeless.getChoiceList();
            for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
                grid[i] = getChoiceItem(ingredients.get(i));
            }

        } else if (recipe instanceof CookingRecipe<?> cooking) {
            ItemStack input = getChoiceItem(cooking.getInputChoice());
            Material icon = switch (recipe) {
                case BlastingRecipe ignored -> Material.BLAST_FURNACE;
                case SmokingRecipe ignored -> Material.SMOKER;
                case CampfireRecipe ignored -> Material.CAMPFIRE;
                default -> Material.FURNACE;
            };
            setSimpleProcess(input, icon, "§7" + StringUtility.formatName(icon.name()));

        } else if (recipe instanceof StonecuttingRecipe stonecutting) {
            ItemStack input = getChoiceItem(stonecutting.getInputChoice());
            setSimpleProcess(input, Material.STONECUTTER, "§7" + Caption.getAsLegacy("gui.stonecutting_name"));

        } else if (recipe instanceof SmithingTransformRecipe smithing) {
            try {
                RecipeChoice base = smithing.getBase();
                RecipeChoice addition = smithing.getAddition();
                RecipeChoice template = smithing.getTemplate();

                ItemStack baseItem = getChoiceItem(base);
                ItemStack additionItem = getChoiceItem(addition);
                ItemStack templateItem = getChoiceItem(template);

                setItemSafe(2, templateItem);
                setItemSafe(10, baseItem);
                setItemSafe(12, additionItem);

                set(new GUIItem(TRANSFORM_SLOT) {
                    @Override
                    public ItemStack getItem(BattlePlayer player) {
                        ItemStack table = new ItemStack(Material.SMITHING_TABLE);
                        ItemMeta meta = table.getItemMeta();
                        if (meta != null) meta.setDisplayName("§7" + Caption.getAsLegacy("gui.smithing_name"));
                        table.setItemMeta(meta);
                        return table;
                    }
                });
            } catch (Exception ignored) {}

        }

        centerAndPlaceGrid(grid);
    }

    private void setSimpleProcess(ItemStack input, Material icon, String name) {
        final ItemStack inputFinal = input;
        set(new GUIItem(11) {
            @Override
            public ItemStack getItem(BattlePlayer player) {
                return inputFinal != null ? inputFinal : ItemStackCreator.getStack(" ", Material.AIR, 1);
            }
        });
        set(new GUIItem(TRANSFORM_SLOT) {
            @Override
            public ItemStack getItem(BattlePlayer player) {
                ItemStack item = new ItemStack(icon);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) meta.setDisplayName(name);
                item.setItemMeta(meta);
                return item;
            }
        });
    }

    private void setItemSafe(int slot, ItemStack stack) {
        if (stack == null) return;
        final ItemStack s = stack;
        set(new GUIItem(GRID_SLOTS[Math.min(slot, GRID_SLOTS.length - 1)]) {
            @Override
            public ItemStack getItem(BattlePlayer player) {
                return s;
            }
        });
    }

    private ItemStack getChoiceItem(RecipeChoice choice) {
        return switch (choice) {
            case RecipeChoice.MaterialChoice mat when !mat.getChoices().isEmpty() -> new ItemStack(mat.getChoices().getFirst());
            case RecipeChoice.ExactChoice exact when !exact.getChoices().isEmpty() -> exact.getChoices().getFirst().clone();
            case null, default -> null;
        };
    }

    private void centerAndPlaceGrid(ItemStack[] grid) {
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

        int usedWidth = (maxCol - minCol + 1);
        int usedHeight = (maxRow - minRow + 1);
        int rowOffset = (usedHeight < 3) ? (1 - (minRow + usedHeight / 2)) : 0;
        int colOffset = (usedWidth < 3) ? (1 - (minCol + usedWidth / 2)) : 0;

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
