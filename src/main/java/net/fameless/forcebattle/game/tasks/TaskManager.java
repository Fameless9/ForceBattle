package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private static final List<ForceTask> tasks = new ArrayList<>();

    public static void register(ForceTask task) {
        tasks.add(task);
    }

    public static void startAll() {
        Bukkit.getScheduler().runTaskTimer(ForceBattle.get(), () -> {
            for (ForceTask task : tasks) {
                task.runTick();
            }
        }, 0L, 3L);
    }
}
