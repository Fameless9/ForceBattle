package net.fameless.forcebattle.gui;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.Format;
import net.fameless.forcebattle.util.InventoryUtils;
import net.fameless.forcebattle.util.ItemData;
import net.fameless.forcebattle.util.ItemUtils;
import net.fameless.forcebattle.util.ResultUtils;
import net.fameless.forcebattle.util.Structure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.combineTeamObjectives;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.getGUITypeFromItemStack;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.getInvTemplate;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.getPageItems;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.getPageLeftItemStack;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.getPageRightItemStack;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.isPageLeftItemStack;
import static net.fameless.forcebattle.gui.ResultGUI.ResultCommandUtils.isPageRightItemStack;

public class ResultGUI implements Listener, InventoryHolder {

    public static ResultGUI holderInstance;

    private final int SPACES_ON_PAGE = 45;
    private boolean animatedGUIFinished = false;
    private final HashMap<BattlePlayer, Integer> playerPageCache = new HashMap<>();
    private final NamespacedKey DATA_KEY = new NamespacedKey(ForceBattle.get(), "resultData");

    public ResultGUI() {
        holderInstance = this;
    }

    @EventHandler(ignoreCancelled = true)
    public void handleInteraction(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ResultGUI)) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        event.setCancelled(true);
        if (!isPageLeftItemStack(event.getCurrentItem()) && !isPageRightItemStack(event.getCurrentItem())) {
            return;
        }

        boolean pageLeft = isPageLeftItemStack(event.getCurrentItem());

        BattlePlayer whoClicked = BattlePlayer.adapt(player);
        ResultType type = getGUITypeFromItemStack(event.getInventory().getItem(0));
        if (type == null) {
            event.getWhoClicked().closeInventory();
        }
        int page = playerPageCache.getOrDefault(whoClicked, 1);

        switch (type) {
            case ALL -> {
                if (pageLeft) {
                    event.getWhoClicked().openInventory(getResultGUI(whoClicked, page - 1));
                } else {
                    event.getWhoClicked().openInventory(getResultGUI(whoClicked, page + 1));
                }
            }
            case TEAM -> {
                Integer teamId = ItemUtils.getData(event.getInventory().getItem(0), DATA_KEY, PersistentDataType.INTEGER);
                if (teamId == null) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                Optional<Team> targetTeamOpt = Team.ofId(teamId);
                targetTeamOpt.ifPresentOrElse(
                        targetTeam -> {
                            if (pageLeft) {
                                event.getWhoClicked().openInventory(getResultGUI(whoClicked, targetTeam, page - 1));
                            } else {
                                event.getWhoClicked().openInventory(getResultGUI(whoClicked, targetTeam, page + 1));
                            }
                        }, () -> event.getWhoClicked().closeInventory()
                );

            }
            case PLAYER -> {
                String playerIdString = ItemUtils.getData(event.getInventory().getItem(0), DATA_KEY, PersistentDataType.STRING);
                if (playerIdString == null) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                UUID playerId;
                try {
                    playerId = UUID.fromString(playerIdString);
                } catch (IllegalArgumentException e) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                @NotNull Optional<BattlePlayer> targetOptional = BattlePlayer.adapt(playerId);
                targetOptional.ifPresentOrElse(
                        target -> {
                            if (pageLeft) {
                                event.getWhoClicked().openInventory(getResultGUI(whoClicked, target, page - 1));
                            } else {
                                event.getWhoClicked().openInventory(getResultGUI(whoClicked, target, page + 1));
                            }
                        }, () -> event.getWhoClicked().closeInventory()
                );
            }
        }
    }

    public @NotNull Inventory getResultGUI(BattlePlayer whoOpens, @NotNull Team team, int page) {
        playerPageCache.put(whoOpens, page);
        Inventory inventory = getInvTemplate(Caption.getAsLegacy(
                ResultType.TEAM.getTitleKey(),
                TagResolver.resolver("team-id", Tag.inserting(Component.text(team.getId())))
        ));

        List<Objective> objectives = combineTeamObjectives(team);
        if (InventoryUtils.isPageValid(objectives, page - 1, SPACES_ON_PAGE)) {
            inventory.setItem(0, getPageLeftItemStack());
        }
        if (InventoryUtils.isPageValid(objectives, page + 1, SPACES_ON_PAGE)) {
            inventory.setItem(8, getPageRightItemStack());
        }

        ItemUtils.addData(
                inventory.getItem(0),
                new ItemData<>(DATA_KEY, PersistentDataType.INTEGER, team.getId()),
                new ItemData<>(ResultType.TEAM.getKey(), PersistentDataType.INTEGER, 1)
        );

        getPageItems(objectives, page, SPACES_ON_PAGE, true).forEach(inventory::addItem);
        return inventory;
    }

    public @NotNull Inventory getResultGUI(BattlePlayer whoOpens, @NotNull BattlePlayer target, int page) {
        playerPageCache.put(whoOpens, page);
        Inventory inventory = getInvTemplate(Caption.getAsLegacy(
                ResultType.PLAYER.getTitleKey(),
                TagResolver.resolver("player-name", Tag.inserting(Component.text(target.getName())))
        ));

        List<Objective> objectives = new ArrayList<>(Objective.finishedBy(target));
        if (InventoryUtils.isPageValid(objectives, page - 1, SPACES_ON_PAGE)) {
            inventory.setItem(0, getPageLeftItemStack());
        }
        if (InventoryUtils.isPageValid(objectives, page + 1, SPACES_ON_PAGE)) {
            inventory.setItem(8, getPageRightItemStack());
        }

        ItemUtils.addData(
                inventory.getItem(0),
                new ItemData<>(DATA_KEY, PersistentDataType.STRING, target.getUuid().toString()),
                new ItemData<>(ResultType.PLAYER.key, PersistentDataType.STRING, "1")
        );

        getPageItems(objectives, page, SPACES_ON_PAGE, true).forEach(inventory::addItem);
        return inventory;
    }

    public @NotNull Inventory getResultGUI(BattlePlayer whoOpens, int page) {
        playerPageCache.put(whoOpens, page);
        Inventory inventory = getInvTemplate(Caption.getAsLegacy(ResultType.ALL.getTitleKey()));

        List<Objective> objectives = new ArrayList<>(Objective.finished());
        if (InventoryUtils.isPageValid(objectives, page - 1, SPACES_ON_PAGE)) {
            inventory.setItem(0, getPageLeftItemStack());
        }
        if (InventoryUtils.isPageValid(objectives, page + 1, SPACES_ON_PAGE)) {
            inventory.setItem(8, getPageRightItemStack());
        }

        ItemUtils.addData(
                inventory.getItem(0),
                new ItemData<>(ResultType.ALL.key, PersistentDataType.INTEGER, 1)
        );

        getPageItems(objectives, page, SPACES_ON_PAGE, true).forEach(inventory::addItem);
        return inventory;
    }

    public @NotNull Inventory getAnimatedPlayerGUI(final BattlePlayer whoOpens, @NotNull final BattlePlayer player) {
        List<Objective> objectives = new ArrayList<>(Objective.finishedBy(player));
        return getAnimatedGUI(
                whoOpens,
                player,
                null,
                objectives,
                ResultType.PLAYER,
                DATA_KEY,
                PersistentDataType.STRING,
                player.getUuid().toString()
        );
    }

    public @NotNull Inventory getAnimatedTeamGUI(final BattlePlayer whoOpens, @NotNull final Team team) {
        List<Objective> objectives = ResultCommandUtils.combineTeamObjectives(team);
        return getAnimatedGUI(
                whoOpens,
                null,
                team,
                objectives,
                ResultType.TEAM,
                DATA_KEY,
                PersistentDataType.INTEGER,
                team.getId()
        );
    }

    private <P, C> @NotNull Inventory getAnimatedGUI(
            final BattlePlayer whoOpens,
            final BattlePlayer player,
            final Team team,
            final List<Objective> objectives,
            final ResultType resultType,
            final NamespacedKey dataKey,
            final PersistentDataType<P, C> type,
            final C value
    ) {
        playerPageCache.put(whoOpens, 1);
        Inventory inventory = getInvTemplate("Results");

        ItemUtils.addData(
                inventory.getItem(0),
                new ItemData<>(dataKey, type, value),
                new ItemData<>(resultType.getKey(), PersistentDataType.INTEGER, 1)
        );

        if (objectives.isEmpty()) return inventory;

        final int[] index = {0};

        final long delayTicks = 10L;
        animatedGUIFinished = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (index[0] >= objectives.size()) {
                    int finalPage = (index[0] > 0) ? ((index[0] - 1) / SPACES_ON_PAGE) + 1 : 1;
                    updateNavButtons(inventory, objectives, finalPage, dataKey, type, value, resultType);

                    if (whoOpens instanceof BattlePlayer bp) {
                        animatedGUIFinished = true;
                        bp.getPlayer().closeInventory();
                    }
                    switch (resultType) {
                        case PLAYER -> Bukkit.getScheduler().runTask(
                                ForceBattle.get(), () -> {
                            Component message = ResultUtils.buildPlayerResultMessage(player);
                            whoOpens.sendMessage(message);
                            whoOpens.getPlayer().sendTitle(
                                    "§6" + player.getName(),
                                    "§8[§a" + player.getPoints() + "§8]",
                                    10, 70, 20
                            );
                        });
                        case TEAM -> Bukkit.getScheduler().runTask(
                                ForceBattle.get(), () -> {
                            Component message = ResultUtils.buildTeamResultMessage(team);
                            whoOpens.sendMessage(message);
                            whoOpens.getPlayer().sendTitle(
                                    "§6Team " + team.getId(),
                                    "§8[§a" + team.getPoints() + "§8]",
                                    10, 70, 20
                            );
                        });
                        default -> {}
                    }

                    this.cancel();
                    return;
                }

                int page = (index[0] / SPACES_ON_PAGE) + 1;
                playerPageCache.put(whoOpens, page);

                if ((index[0] % SPACES_ON_PAGE) == 0) {
                    for (int s = 9; s < 54; s++) inventory.setItem(s, null);
                }

                List<ItemStack> pageItems = ResultCommandUtils.getPageItems(objectives, page, SPACES_ON_PAGE, false);
                int localIndex = index[0] % SPACES_ON_PAGE;
                if (localIndex < pageItems.size()) {
                    inventory.setItem(9 + localIndex, pageItems.get(localIndex));
                }

                updateNavButtons(inventory, objectives, page, dataKey, type, value, resultType);
                index[0]++;
            }
        }.runTaskTimer(ForceBattle.get(), 0L, delayTicks);

        return inventory;
    }

    /**
     * Updates the left/right nav buttons with proper metadata.
     */
    private <P, C> void updateNavButtons(
            Inventory inventory,
            List<Objective> objectives,
            int page,
            NamespacedKey dataKey,
            PersistentDataType<P, C> type,
            C value,
            ResultType resultType
    ) {
        // Left button
        if (InventoryUtils.isPageValid(objectives, page - 1, SPACES_ON_PAGE)) {
            ItemStack left = ResultCommandUtils.getPageLeftItemStack();
            ItemUtils.addData(left,
                    new ItemData<>(dataKey, type, value),
                    new ItemData<>(resultType.getKey(), PersistentDataType.INTEGER, 1)
            );
            inventory.setItem(0, left);
        } else {
            inventory.setItem(0, ItemUtils.SpecialItems.getFillerItem());
        }

        // Right button
        if (InventoryUtils.isPageValid(objectives, page + 1, SPACES_ON_PAGE)) {
            ItemStack right = ResultCommandUtils.getPageRightItemStack();
            ItemUtils.addData(right,
                    new ItemData<>(dataKey, type, value),
                    new ItemData<>(resultType.getKey(), PersistentDataType.INTEGER, 1)
            );
            inventory.setItem(8, right);
        } else {
            inventory.setItem(8, ItemUtils.SpecialItems.getFillerItem());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getResultGUI(null, 1);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().equals("Results") && !animatedGUIFinished) {
            Bukkit.getScheduler().runTask(ForceBattle.get(), () -> {
                if (player.isOnline()) {
                    player.openInventory(event.getInventory());
                }
            });
        }

    }

    @Getter
    public enum ResultType {

        ALL(new NamespacedKey(ForceBattle.get(), "resultAll"), "gui.result_title_all"),
        TEAM(new NamespacedKey(ForceBattle.get(), "resultTeam"), "gui.result_title_team"),
        PLAYER(new NamespacedKey(ForceBattle.get(), "resultPlayer"), "gui.result_title_player");

        private final NamespacedKey key;
        private final String titleKey;

        ResultType(NamespacedKey key, String titleKey) {
            this.key = key;
            this.titleKey = titleKey;
        }

    }

    public static class ResultCommandUtils {

        public static final NamespacedKey PAGE_RIGHT_KEY = new NamespacedKey(ForceBattle.get(), "pageRight");
        public static final NamespacedKey PAGE_LEFT_KEY = new NamespacedKey(ForceBattle.get(), "pageLeft");

        public static boolean isPageLeftItemStack(@NotNull ItemStack stack) {
            return ItemUtils.hasData(stack, PAGE_LEFT_KEY);
        }

        public static @NotNull ItemStack getPageLeftItemStack() {
            return ItemUtils.addData(
                    new ItemUtils.ItemBuilder()
                            .type(Material.LIME_STAINED_GLASS_PANE)
                            .name(ChatColor.GREEN + "Go Left")
                            .build(),
                    new ItemData<>(PAGE_LEFT_KEY, PersistentDataType.INTEGER, 1)
            );
        }

        public static boolean isPageRightItemStack(@NotNull ItemStack stack) {
            return ItemUtils.hasData(stack, PAGE_RIGHT_KEY);
        }

        public static @NotNull ItemStack getPageRightItemStack() {
            return ItemUtils.addData(
                    new ItemUtils.ItemBuilder()
                            .type(Material.LIME_STAINED_GLASS_PANE)
                            .name(ChatColor.GREEN + "Go Right")
                            .build(),
                    new ItemData<>(PAGE_RIGHT_KEY, PersistentDataType.INTEGER, 1)
            );
        }

        public static @Nullable ResultType getGUITypeFromItemStack(ItemStack stack) {
            for (ResultType type : ResultType.values()) {
                if (ItemUtils.hasData(stack, type.getKey())) {
                    return type;
                }
            }
            return null;
        }

        public static @NotNull Inventory getInvTemplate(@NotNull String name) {
            Inventory inventory = Bukkit.createInventory(holderInstance, 54, name);
            for (int i = 0; i < 9; i++) {
                inventory.setItem(i, ItemUtils.SpecialItems.getFillerItem());
            }
            return inventory;
        }

        public static @NotNull List<Objective> combineTeamObjectives(@NotNull Team team) {
            List<Objective> objectives = new ArrayList<>();
            team.getPlayers().forEach(teamPlayer -> objectives.addAll(Objective.finishedBy(teamPlayer)));
            return objectives;
        }

        public static List<ItemStack> getPageItems(@NotNull List<Objective> objectives, int page, int spaces, boolean displayNames) {
            objectives.sort(Comparator.comparingLong(Objective::getTime).reversed());

            int startIndex = (page - 1) * spaces;
            int endIndex = Math.min(startIndex + spaces, objectives.size());

            List<ItemStack> pageItems = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                if (i < 0 || i >= objectives.size()) {
                    return pageItems;
                }
                Objective objective = objectives.get(i);
                Object convertedObjective = BukkitUtil.convertObjective(objective.getBattleType(), objective.getObjectiveString());

                Material itemType = getTypeFromObjective(objective);
                String itemName = ChatColor.GRAY + objective.getBattleType().getPrefix() + ": " + ChatColor.BLUE +
                        (convertedObjective instanceof Advancement advancement ? advancement.name : Format.formatName(objective.getObjectiveString()));

                ItemUtils.ItemBuilder builder = new ItemUtils.ItemBuilder();

                builder.type(itemType)
                        .name(itemName)
                        .addLore("")
                        .addLore(ChatColor.GRAY + "Time: " + ChatColor.BLUE + Format.formatTime(objective.getTime()));

                if (displayNames) {
                    builder.addLore(ChatColor.GRAY + "Player: " + ChatColor.BLUE + objective.getWhoFinished().getName());
                }

                builder.addLore("");

                if (objective.hasBeenSkipped()) {
                    builder.addLore(ChatColor.RED + "SKIPPED");
                    builder.addLore("");
                }

                pageItems.add(builder.build());
            }
            return pageItems;
        }

        public static Material getTypeFromObjective(@NotNull Objective objective) {
            Object objectiveObject = BukkitUtil.convertObjective(objective.getBattleType(), objective.getObjectiveString());
            if (objectiveObject instanceof Material material) {
                return material;
            }
            if (objectiveObject instanceof EntityType) {
                return Material.SPIDER_SPAWN_EGG;
            }
            if (objectiveObject instanceof Biome) {
                return Material.GRASS_BLOCK;
            }
            if (objectiveObject instanceof Advancement) {
                return Material.BREWING_STAND;
            }
            if (objectiveObject instanceof Integer) {
                return Material.SCAFFOLDING;
            }
            if (objectiveObject instanceof Location) {
                return Material.DIAMOND_BOOTS;
            }
            if (objectiveObject instanceof Structure) {
                return Material.STRUCTURE_BLOCK;
            }
            throw new RuntimeException("Corrupt/Invalid objective type: " + objective.getObjectiveString() + ". battleType: " + objective.getBattleType());
        }

    }

}
