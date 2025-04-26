package net.fameless.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Map;

public class PermissionManager {

    private static final Map<String, PermissionDefault> PERMISSIONS = Map.ofEntries(
            Map.entry("forcebattle.lang", PermissionDefault.OP),
            Map.entry("forcebattle.timer", PermissionDefault.OP),
            Map.entry("forcebattle.settings", PermissionDefault.OP),
            Map.entry("forcebattle.team", PermissionDefault.TRUE),
            Map.entry("forcebattle.exclude", PermissionDefault.OP),
            Map.entry("forcebattle.skip", PermissionDefault.OP),
            Map.entry("forcebattle.reset", PermissionDefault.OP),
            Map.entry("forcebattle.result", PermissionDefault.TRUE),
            Map.entry("forcebattle.backpack", PermissionDefault.TRUE),
            Map.entry("forcebattle.displayresults", PermissionDefault.OP),
            Map.entry("forcebattle.joker", PermissionDefault.OP),
            Map.entry("forcebattle.points", PermissionDefault.OP),
            Map.entry("forcebattle.help", PermissionDefault.TRUE)
    );

    public static void registerPermissions() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        PERMISSIONS.forEach((permission, permissionDefault) -> {
            Permission perm = new Permission(permission);
            perm.setDefault(permissionDefault);
            pluginManager.addPermission(perm);
        });
    }

}
