package net.fameless.forcebattle.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StructureUtil {

    public static void loadAndPlaceStructure(Location loc, String name) {
        StructureManager manager = Bukkit.getStructureManager();
        NamespacedKey key = new NamespacedKey("forcebattle", name);

        if (manager.getStructure(key) == null) {
            try (InputStream in = StructureUtil.class.getResourceAsStream("/structures/" + name + ".nbt")) {
                if (in == null) {
                    Bukkit.getLogger().severe("Could not find structure file: " + name);
                    return;
                }
                Structure structure = manager.loadStructure(in);
                manager.registerStructure(key, structure);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Structure structure = manager.getStructure(key);
        if (structure != null) {
            structure.place(loc, true, StructureRotation.NONE, Mirror.NONE, 0, 1.0f, new Random());
        }
    }

    public static void createSpawn() {
        Location spawnLoc = new Location(Bukkit.getWorld("world"), -5, 200, -5);
        loadAndPlaceStructure(spawnLoc, "spawn");
    }

}
