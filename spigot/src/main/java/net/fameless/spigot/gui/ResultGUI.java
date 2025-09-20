package net.fameless.spigot.gui;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.Command;
import net.fameless.core.game.Objective;
import net.fameless.core.game.Team;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.Coords;
import net.fameless.core.util.Format;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.player.BukkitPlayer;
import net.fameless.spigot.util.Advancement;
import net.fameless.spigot.util.BukkitUtil;
import net.fameless.spigot.util.InventoryUtils;
import net.fameless.spigot.util.ItemData;
import net.fameless.spigot.util.ItemUtils;
import net.fameless.spigot.util.Structure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.combineTeamObjectives;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.getGUITypeFromItemStack;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.getInvTemplate;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.getPageItems;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.getPageLeftItemStack;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.getPageRightItemStack;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.isPageLeftItemStack;
import static net.fameless.spigot.gui.ResultGUI.ResultCommandUtils.isPageRightItemStack;

public class ResultGUI implements Listener, InventoryHolder {

    public static ResultGUI holderInstance;

    private final int SPACES_ON_PAGE = 45;
    private final HashMap<BattlePlayer<?>, Integer> playerPageCache = new HashMap<>();
    private final NamespacedKey DATA_KEY = new NamespacedKey(BukkitPlatform.get(), "resultData");

    public ResultGUI() {
        holderInstance = this;

        Command.forId("result")
                .ifPresent(command -> command.onExecute(
                        (caller, args) -> {
                            if (ForceBattle.getTimer().isRunning()) {
                                caller.sendMessage(Caption.of("command.timer_running"));
                                return;
                            }
                            if (!(caller instanceof BukkitPlayer whoOpens)) {
                                return;
                            }
                            if (args.length == 0) {
                                whoOpens.openInventory(getResultGUI(whoOpens, 1));
                                return;
                            }
                            if (args.length < 2) {
                                command.sendUsage(caller);
                                return;
                            }
                            switch (args[0]) {
                                case "player" -> {
                                    @NotNull Optional<BattlePlayer<?>> targetOpt = BattlePlayer.of(args[1]);
                                    targetOpt.ifPresentOrElse(
                                            target -> whoOpens.openInventory(getResultGUI(whoOpens, target, 1)),
                                            () -> caller.sendMessage(Caption.of("command.no_such_player"))
                                    );
                                }
                                case "team" -> {
                                    int teamId;
                                    try {
                                        teamId = Integer.parseInt(args[1]);
                                    } catch (NumberFormatException e) {
                                        caller.sendMessage(Caption.of("command.not_a_number"));
                                        return;
                                    }
                                    Optional<Team> targetTeamOpt = Team.ofId(teamId);
                                    targetTeamOpt.ifPresentOrElse(
                                            team -> whoOpens.openInventory(getResultGUI(whoOpens, team, 1)),
                                            () -> caller.sendMessage(Caption.of("command.no_such_team"))
                                    );
                                }
                            }
                        }
                ));
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

        BukkitPlayer whoClicked = BukkitPlayer.adapt(player);
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
                @NotNull Optional<BukkitPlayer> targetOptional = BukkitPlayer.adapt(playerId);
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

    public @NotNull Inventory getResultGUI(BattlePlayer<?> whoOpens, @NotNull Team team, int page) {
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

        getPageItems(objectives, page, SPACES_ON_PAGE).forEach(inventory::addItem);
        return inventory;
    }

    public @NotNull Inventory getResultGUI(BattlePlayer<?> whoOpens, @NotNull BattlePlayer<?> target, int page) {
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
                new ItemData<>(DATA_KEY, PersistentDataType.STRING, target.getUniqueId().toString()),
                new ItemData<>(ResultType.PLAYER.key, PersistentDataType.STRING, "1")
        );

        getPageItems(objectives, page, SPACES_ON_PAGE).forEach(inventory::addItem);
        return inventory;
    }

    public @NotNull Inventory getResultGUI(BattlePlayer<?> whoOpens, int page) {
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

        getPageItems(objectives, page, SPACES_ON_PAGE).forEach(inventory::addItem);
        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getResultGUI(null, 1);
    }

    public enum ResultType {

        ALL(new NamespacedKey(BukkitPlatform.get(), "resultAll"), "gui.result_title_all"),
        TEAM(new NamespacedKey(BukkitPlatform.get(), "resultTeam"), "gui.result_title_team"),
        PLAYER(new NamespacedKey(BukkitPlatform.get(), "resultPlayer"), "gui.result_title_player");

        private final NamespacedKey key;
        private final String titleKey;

        ResultType(NamespacedKey key, String titleKey) {
            this.key = key;
            this.titleKey = titleKey;
        }

        public NamespacedKey getKey() {
            return key;
        }

        public String getTitleKey() {
            return titleKey;
        }
    }

    public static class ResultCommandUtils {

        public static final NamespacedKey PAGE_RIGHT_KEY = new NamespacedKey(BukkitPlatform.get(), "pageRight");
        public static final NamespacedKey PAGE_LEFT_KEY = new NamespacedKey(BukkitPlatform.get(), "pageLeft");

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
            for (ResultGUI.ResultType type : ResultGUI.ResultType.values()) {
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

        public static List<ItemStack> getPageItems(@NotNull List<Objective> objectives, int page, int spaces) {
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

                pageItems.add(new ItemUtils.ItemBuilder()
                        .type(itemType)
                        .name(itemName)
                        .addLore("")
                        .addLore(ChatColor.GRAY + "Time: " + ChatColor.BLUE + Format.formatTime(objective.getTime()))
                        .addLore(ChatColor.GRAY + "Player: " + ChatColor.BLUE + objective.getWhoFinished().getName())
                        .build()
                );
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
            if (objectiveObject instanceof Coords) {
                return Material.DIAMOND_BOOTS;
            }
            if (objectiveObject instanceof Structure) {
                return Material.STRUCTURE_BLOCK;
            }
            throw new RuntimeException("Corrupt/Invalid objective type: " + objective.getObjectiveString() + ". battleType: " + objective.getBattleType());
        }

    }

}
