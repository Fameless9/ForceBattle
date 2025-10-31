package net.fameless.forcebattle.gui.impl;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.game.data.BiomeSimplified;
import net.fameless.forcebattle.game.data.StructureSimplified;
import net.fameless.forcebattle.gui.ForceBattleGUI;
import net.fameless.forcebattle.gui.GUIClickableItem;
import net.fameless.forcebattle.gui.GUIItem;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ResultGUI extends ForceBattleGUI {

    private static final int SPACES_ON_PAGE = 45;

    private final ResultType resultType;
    private final int page;
    private final BattlePlayer targetPlayer;
    private final Team targetTeam;

    private final boolean animated;
    private final boolean skipLoad;

    private final Map<BattlePlayer, Integer> playerPageCache = new HashMap<>();
    private final Map<BattlePlayer, List<Objective>> objectiveList = new HashMap<>();
    private final Map<BattlePlayer, BukkitRunnable> activeAnimations = new HashMap<>();

    public ResultGUI(ResultType resultType, int page, BattlePlayer targetPlayer, Team targetTeam, boolean animated, boolean skipLoad) {
        super(generateTitle(resultType, targetPlayer, targetTeam, animated), 54);
        this.page = page;
        this.resultType = resultType;
        this.targetPlayer = targetPlayer;
        this.targetTeam = targetTeam;
        this.animated = animated;
        this.skipLoad = skipLoad;
    }

    public ResultGUI(ResultType resultType, int page, BattlePlayer targetPlayer, Team targetTeam, boolean animated) {
        this(resultType, page, targetPlayer, targetTeam, animated, false);
    }

    public ResultGUI(BattlePlayer targetPlayer, boolean animated) {
        this(ResultType.PLAYER, 1, targetPlayer, null, animated, false);
    }

    public ResultGUI(Team targetTeam, boolean animated) {
        this(ResultType.TEAM, 1, null, targetTeam, animated, false);
    }

    public ResultGUI() {
        this(ResultType.ALL, 1, null, null, false, false);
    }

    @Override
    public void open(BattlePlayer viewer) {
        if (skipLoad) {
            super.open(viewer);

            List<Objective> objectives = objectiveList.get(viewer);
            if (objectives == null) return;

            if (!animated) setItems(viewer);
            return;
        }

        Collection<Objective> collection = switch (resultType) {
            case PLAYER -> Objective.finishedBy(targetPlayer);
            case TEAM -> ResultCommandUtils.combineTeamObjectives(targetTeam);
            case ALL -> Objective.finished();
        };

        List<Objective> objectives = new ArrayList<>(collection);
        objectiveList.put(viewer, objectives);
        playerPageCache.put(viewer, page);

        super.open(viewer);

        if (animated) startAnimation(viewer, objectives, 0);
        else setItems(viewer);
    }

    private void startAnimation(BattlePlayer viewer, List<Objective> objectives, int startIndex) {
        fill(ItemStackCreator.fillerItem());

        BukkitRunnable task = new BukkitRunnable() {
            int index = startIndex;

            @Override
            public void run() {
                if (viewer.isOffline() || !viewer.getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) {
                    cancel();
                    activeAnimations.remove(viewer);
                    return;
                }

                if (index >= objectives.size()) {
                    cancel();
                    activeAnimations.remove(viewer);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            viewer.getPlayer().playSound(viewer.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1f);
                            viewer.getPlayer().closeInventory();
                            showResultSummary(viewer);
                        }
                    }.runTaskLater(ForceBattle.get(), 20L);
                    return;
                }

                int currentPage = (index / SPACES_ON_PAGE) + 1;
                int slot = 9 + (index % SPACES_ON_PAGE);

                Objective objective = objectives.get(index);

                if (currentPage != page) {
                    ResultGUI nextPageGUI = new ResultGUI(resultType, currentPage, targetPlayer, targetTeam, true, true);
                    nextPageGUI.objectiveList.put(viewer, objectives);
                    nextPageGUI.playerPageCache.put(viewer, currentPage);

                    nextPageGUI.open(viewer);
                    nextPageGUI.startAnimation(viewer, objectives, index);

                    cancel();
                    return;
                }

                set(new GUIItem(slot) {
                    @Override
                    public ItemStack getItem(BattlePlayer player) {
                        return ResultCommandUtils.buildObjectiveItem(objective, false);
                    }
                });

                updateInventory(viewer);
                index++;
            }
        };

        final int animationDelayTicks = 15;
        task.runTaskTimer(ForceBattle.get(), 0L, animationDelayTicks);
        activeAnimations.put(viewer, task);
    }

    @Override
    public void setItems(BattlePlayer viewer) {
        if (animated) return;

        int currentPage = playerPageCache.getOrDefault(viewer, 1);
        List<Objective> objectives = objectiveList.get(viewer);
        if (objectives == null) return;

        fill(ItemStackCreator.fillerItem());

        int totalPages = (int) Math.ceil((double) objectives.size() / SPACES_ON_PAGE);

        if (currentPage > 1) {
            set(GUIClickableItem.getGoBackItem(0, new ResultGUI(resultType, currentPage - 1, targetPlayer, targetTeam, false)));
        }

        if (currentPage < totalPages) {
            set(GUIClickableItem.getGoForthItem(8, new ResultGUI(resultType, currentPage + 1, targetPlayer, targetTeam, false)));
        }

        int startIndex = (currentPage - 1) * SPACES_ON_PAGE;
        for (int i = 0; i < SPACES_ON_PAGE; i++) {
            int index = startIndex + i;
            int slot = 9 + i;
            if (index >= objectives.size()) break;

            Objective objective = objectives.get(index);
            set(new GUIItem(slot) {
                @Override
                public ItemStack getItem(BattlePlayer player) {
                    return ResultCommandUtils.buildObjectiveItem(objective, true);
                }
            });
        }
    }

    private void showResultSummary(BattlePlayer viewer) {
        switch (resultType) {
            case PLAYER -> {
                if (targetPlayer != null) {
                    viewer.sendMessage(ResultUtils.buildPlayerResultMessage(targetPlayer));

                    viewer.getPlayer().sendTitle(
                            "§6" + targetPlayer.getName(),
                            "§eScore: §b" + targetPlayer.getPoints(),
                            10, 40, 10
                    );
                }
            }
            case TEAM -> {
                if (targetTeam != null) {
                    viewer.sendMessage(ResultUtils.buildTeamResultMessage(targetTeam));

                    viewer.getPlayer().sendTitle(
                            "§6Team " + targetTeam.getId(),
                            "§eScore: §b" + targetTeam.getPoints(),
                            10, 40, 10
                    );
                }
            }
            case ALL -> {
                viewer.getPlayer().sendTitle(
                        "§bAll Results",
                        "§7Animation complete",
                        10, 40, 10
                );
            }
        }
    }

    @Override
    public boolean allowItemMoving() {
        return false;
    }

    public void onClose(BattlePlayer player, InventoryCloseEvent event) {
        BukkitRunnable task = activeAnimations.remove(player);
        if (task != null) task.cancel();

        objectiveList.remove(player);
        playerPageCache.remove(player);
    }

    private static String generateTitle(ResultType type, BattlePlayer player, Team team, boolean animated) {
        if (animated) {
            return Caption.getAsLegacy("gui.result_animated");
        }

        TagResolver resolver = switch (type) {
            case PLAYER -> TagResolver.resolver(
                    Placeholder.parsed("player-name", player != null ? player.getName() : "Unknown")
            );
            case TEAM -> TagResolver.resolver(
                    Placeholder.parsed("team-id", team != null ? String.valueOf(team.getId()) : "N/A")
            );
            default -> TagResolver.empty();
        };
        return Caption.getAsLegacy(type.getTitleKey(), resolver);
    }

    @Getter
    public enum ResultType {
        ALL("gui.result_title_all"),
        TEAM("gui.result_title_team"),
        PLAYER("gui.result_title_player");

        private final String titleKey;

        ResultType(String titleKey) {
            this.titleKey = titleKey;
        }
    }

    public static class ResultCommandUtils {

        public static @NotNull List<Objective> combineTeamObjectives(@NotNull Team team) {
            List<Objective> objectives = new ArrayList<>();
            team.getPlayers().forEach(teamPlayer -> objectives.addAll(Objective.finishedBy(teamPlayer)));
            objectives.sort(Comparator.comparingInt(Objective::getTime).reversed());
            return objectives;
        }

        public static @NotNull ItemStack buildObjectiveItem(@NotNull Objective objective, boolean displayNames) {
            Material material = Material.STONE;
            Object obj = BukkitUtil.convertObjective(objective.getBattleType(), objective.getObjectiveString());

            if (obj instanceof Material m) material = m;
            else if (obj instanceof EntityType) material = Material.SPIDER_SPAWN_EGG;
            else if (obj instanceof Biome || obj instanceof BiomeSimplified) material = Material.GRASS_BLOCK;
            else if (obj instanceof FBAdvancement) material = Material.BREWING_STAND;
            else if (obj instanceof Integer) material = Material.SCAFFOLDING;
            else if (obj instanceof Location) material = Material.DIAMOND_BOOTS;
            else if (obj instanceof StructureSimplified) material = Material.STRUCTURE_BLOCK;

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Time: §9" + StringUtility.formatTime(objective.getTime()));

            if (displayNames)
                lore.add("§7Player: §9" + objective.getWhoFinished().getName());
            if (objective.hasBeenSkipped()) {
                lore.add("");
                lore.add("§cSKIPPED");
            }

            String name = "§7" + objective.getBattleType().getPrefix() + ": §9" + StringUtility.formatName(objective.getObjectiveString());

            return ItemStackCreator.getStack(name, material, lore);
        }
    }
}
