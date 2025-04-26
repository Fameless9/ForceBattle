package net.fameless.spigot.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public final class Toast {

    private final Plugin main;
    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final Style style;

    private Toast(Plugin main, String icon, String message, Style style) {
        this.main = main;
        this.key = new NamespacedKey(main, UUID.randomUUID().toString());
        this.icon = icon;
        this.message = message;
        this.style = style;
    }

    public static void display(Player player, String icon, String message, Style style, Plugin main) {
        new Toast.Builder()
                .plugin(main)
                .icon(icon)
                .message(message)
                .style(style)
                .buildAndDisplay(player);
    }

    public static void display(Collection<Player> players, String icon, String message, Style style, Plugin main) {
        new Toast.Builder()
                .plugin(main)
                .icon(icon)
                .message(message)
                .style(style)
                .buildAndDisplay(players);
    }

    public static void display(Player player, Material icon, String message, Style style, Plugin main) {
        new Toast.Builder()
                .plugin(main)
                .icon(icon)
                .message(message)
                .style(style)
                .buildAndDisplay(player);
    }

    public static void display(Collection<Player> players, Material icon, String message, Style style, Plugin main) {
        new Toast.Builder()
                .plugin(main)
                .icon(icon)
                .message(message)
                .style(style)
                .buildAndDisplay(players);
    }

    private void start(Player player) {
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLater(main, () -> revokeAdvancement(player), 10);
    }

    private void start(@NotNull Collection<Player> players) {
        createAdvancement();
        players.forEach(this::grantAdvancement);

        Bukkit.getScheduler().runTaskLater(main, () -> players.forEach(this::revokeAdvancement), 10);
    }

    private void createAdvancement() {
        Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"id\": \"minecraft:" + icon + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + message.replace("|", "\n") + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                "        \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}");
    }

    private void grantAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            throw new RuntimeException("Advancement is null! Make sure to create an advancement before granting it to players!");
        }
        player.getAdvancementProgress(advancement).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            throw new RuntimeException("Advancement is null! Make sure to create an advancement before revoking it!");
        }
        player.getAdvancementProgress(advancement).revokeCriteria("trigger");
    }

    public enum Style {
        GOAL,
        TASK,
        CHALLENGE
    }

    public static class Builder {

        private Plugin plugin;
        private String icon;
        private String message;
        private Style style;

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(@NotNull Material icon) {
            this.icon = icon.name().toLowerCase();
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Toast build() {
            return new Toast(plugin, icon, message, style);
        }

        public void buildAndDisplay(Player player) {
            new Toast(plugin, icon, message, style).start(player);
        }

        public void buildAndDisplay(Collection<Player> players) {
            new Toast(plugin, icon, message, style).start(players);
        }

    }

}
