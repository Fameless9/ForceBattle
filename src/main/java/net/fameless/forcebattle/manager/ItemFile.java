package net.fameless.forcebattle.manager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.util.Advancement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.io.*;
import java.util.List;

public class ItemFile {

    private static final FileConfiguration configuration = ForceBattlePlugin.getInstance().getConfig();
    private static final File jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder() + "/data.json");

    private static final boolean excludeSpawnEggs = ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude.exclude_spawn_eggs");
    private static final boolean excludeMusicDiscs = ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude.exclude_music_discs");
    private static final boolean excludeBannerPatterns = ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude.exclude_banner_patterns");
    private static final boolean excludeBanners = ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude.exclude_banners");
    private static final boolean excludeArmorTemplates = ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude.exclude_armor_templates");

    public static void initFiles() throws IOException {

        ForceBattlePlugin.getInstance().getDataFolder().mkdir();

        JsonObject finalObject = new JsonObject();

        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject initialData = new JsonObject();
            initialData.addProperty("Author", "Fameless9 (https://github.com/Fameless9)");
            saveJsonFile(initialData);
        }

// Items
        List<String> toExclude = configuration.getStringList("exclude.items");

        JsonObject itemObject = new JsonObject();

        for (Material material : Material.values()) {
            if (toExclude.contains(material.name())) continue;
            if (excludeSpawnEggs && material.name().endsWith("SPAWN_EGG")) continue;
            if (excludeMusicDiscs && material.name().contains("DISC")) continue;
            if (excludeBannerPatterns && material.name().endsWith("BANNER_PATTERN")) continue;
            if (excludeBanners && material.name().endsWith("BANNER")) continue;
            if (excludeArmorTemplates && material.name().endsWith("TEMPLATE")) continue;
            if (material.name().endsWith("CANDLE_CAKE")) continue;
            if (material.name().startsWith("POTTED")) continue;
            if (material.name().contains("WALL") && material.name().contains("TORCH")) continue;
            if (material.name().contains("WALL") && material.name().contains("SIGN")) continue;
            if (material.name().contains("WALL") && material.name().contains("HEAD")) continue;
            if (material.name().contains("WALL") && material.name().contains("CORAL")) continue;
            if (material.name().contains("WALL") && material.name().contains("BANNER")) continue;
            if (material.name().contains("WALL") && material.name().contains("SKULL")) continue;
            if (material.name().endsWith("STEM")) continue;
            itemObject.addProperty(material.name(), false);
        }

        finalObject.add("items", itemObject);

// Mobs
        List<String> mobSection = configuration.getStringList("exclude.mobs");

        JsonObject mobObject = new JsonObject();

        for (EntityType entity : EntityType.values()) {
            if (mobSection.isEmpty()) {
                mobObject.addProperty(entity.name(), false);
                continue;
            }
            if (mobSection.contains(entity.name())) continue;
            mobObject.addProperty(entity.name(), false);
        }

        finalObject.add("mobs", mobObject);

// Biomes
        List<String> biomeSection = configuration.getStringList("exclude.biomes");

        JsonObject biomeObject = new JsonObject();

        for (Biome biome : Biome.values()) {
            if (biomeSection.isEmpty()) {
                biomeObject.addProperty(biome.name(), false);
                continue;
            }
            if (biomeSection.contains(biome.name())) continue;
            biomeObject.addProperty(biome.name(), false);
        }

        finalObject.add("biomes", biomeObject);

// Advancements
        List<String> advancementSection = configuration.getStringList("exclude.advancements");

        JsonObject advancementObject = new JsonObject();

        for (Advancement advancement : Advancement.values()) {
            if (advancementSection.isEmpty()) {
                advancementObject.addProperty(advancement.name(), false);
                continue;
            }
            if (advancementSection.contains(advancement.name())) continue;
            advancementObject.addProperty(advancement.name(), false);
        }

        finalObject.add("advancements", advancementObject);

// Heights
        List<String> heightSection = configuration.getStringList("exclude.heights");

        JsonObject heightObject = new JsonObject();

        for (int i = -63; i < 321; i++) {
            if (heightSection.isEmpty()) {
                heightObject.addProperty(String.valueOf(i), false);
                continue;

            }
            if (heightSection.contains(String.valueOf(i))) continue;
            heightObject.addProperty(String.valueOf(i), false);
        }

        finalObject.add("heights", heightObject);

        saveJsonFile(finalObject);
    }

    private static JsonObject getRootObject() {
        JsonParser parser = new JsonParser();
        try {
            return parser.parse(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("Failed to create file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(ForceBattlePlugin.getInstance());
        }
        return new JsonObject();
    }

    public static JsonObject getItemObject() {
        if (getRootObject().has("items")) {
            return getRootObject().getAsJsonObject("items");
        }
        return new JsonObject();
    }

    public static JsonObject getMobObject() {
        if (getRootObject().has("mobs")) {
            return getRootObject().getAsJsonObject("mobs");
        }
        return new JsonObject();
    }

    public static JsonObject getBiomeObject() {
        if (getRootObject().has("biomes")) {
            return getRootObject().getAsJsonObject("biomes");
        }
        return new JsonObject();
    }

    public static JsonObject getAdvancementObject() {
        if (getRootObject().has("advancements")) {
            return getRootObject().getAsJsonObject("advancements");
        }
        return new JsonObject();
    }

    public static JsonObject getHeightObject() {
        if (getRootObject().has("heights")) {
            return getRootObject().getAsJsonObject("heights");
        }
        return new JsonObject();
    }

    public static void saveJsonFile(JsonObject finalObject) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(finalObject, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to create file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(ForceBattlePlugin.getInstance());
        }
    }
}