package net.fameless.forcebattle.manager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.util.Advancement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.io.*;
import java.util.List;

public class ItemFile {

    private static final FileConfiguration configuration = ForceBattle.getInstance().getConfig();
    private static final File jsonFile = new File(ForceBattle.getInstance().getDataFolder() + "/data.json");

    private static final boolean excludeSpawnEggs = ForceBattle.getInstance().getConfig().getBoolean("exclude.exclude_spawn_eggs");
    private static final boolean excludeMusicDiscs = ForceBattle.getInstance().getConfig().getBoolean("exclude.exclude_music_discs");
    private static final boolean excludeBannerPatterns = ForceBattle.getInstance().getConfig().getBoolean("exclude.exclude_banner_patterns");
    private static final boolean excludeBanners = ForceBattle.getInstance().getConfig().getBoolean("exclude.exclude_banners");
    private static final boolean excludeArmorTemplates = ForceBattle.getInstance().getConfig().getBoolean("exclude.exclude_armor_templates");

    public static void initFiles() throws IOException {

        ForceBattle.getInstance().getDataFolder().mkdir();

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
            if (excludeSpawnEggs) {
                if (material.name().endsWith("SPAWN_EGG")) {
                    toExclude.add(material.name());
                    continue;
                }
            }
            if (excludeMusicDiscs) {
                if (material.name().contains("DISC")) {
                    toExclude.add(material.name());
                    continue;
                }
            }
            if (excludeBannerPatterns) {
                if (material.name().endsWith("BANNER_PATTERN")) {
                    toExclude.add(material.name());
                    continue;
                }
            }
            if (excludeBanners) {
                if (material.name().endsWith("BANNER")) {
                    toExclude.add(material.name());
                    continue;
                }
            }
            if (excludeArmorTemplates) {
                if (material.name().endsWith("TEMPLATE")) {
                    toExclude.add(material.name());
                    continue;
                }
            }
            if (material.name().endsWith("CANDLE_CAKE")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().startsWith("POTTED")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().endsWith("STEM")) {
                toExclude.add(material.name());
            }
        }

        for (Material material : Material.values()) {
            if (toExclude.contains(material.name())) continue;
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
            Bukkit.getPluginManager().disablePlugin(ForceBattle.getInstance());
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
            Bukkit.getPluginManager().disablePlugin(ForceBattle.getInstance());
        }
    }
}