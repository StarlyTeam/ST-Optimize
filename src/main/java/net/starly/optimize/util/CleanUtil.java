package net.starly.optimize.util;

import net.starly.optimize.OptimizeMain;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CleanUtil {

    private static List<Material> exceptMaterial;
    private static OptimizeMain plugin;

    public static void init() {

        plugin = OptimizeMain.getInstance();

        exceptMaterial = new ArrayList<>();
        MessageContent.getInstance().getMessages(MessageType.CONFIG, "exceptAutoRemove").forEach(string -> {
            try {
                exceptMaterial.add(Material.valueOf(string.toUpperCase()));
            } catch (IllegalArgumentException exception) {
                OptimizeMain.getInstance().getLogger().log(Level.SEVERE,string + "은(는) 존재하지 않는 아이템입니다!");
            }
        });
    }

    public static void clearItem() {
        plugin.getServer().getWorlds().forEach(world -> {
            if (plugin.getExceptWorlds().contains(world)) return;
            world.getEntitiesByClass(Item.class).forEach(item -> {
                if (exceptMaterial.contains(item.getItemStack().getType())) return;
                item.remove();
            });
        });
    }

}
