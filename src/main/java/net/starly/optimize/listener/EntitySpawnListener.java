package net.starly.optimize.listener;

import net.starly.optimize.OptimizeMain;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (OptimizeMain.getInstance().getExceptWorlds().contains(event.getEntity().getWorld())) return;
        if (!event.getEntityType().isAlive()) return;

        Chunk chunk = event.getEntity().getLocation().getChunk();
        if (event.getEntityType().equals(EntityType.VILLAGER)) {
            AtomicInteger count = new AtomicInteger();
            Arrays.stream(chunk.getEntities()).forEach(entity -> {
                if (entity.getType().equals(EntityType.VILLAGER)) count.getAndIncrement();
            });
            if (count.get() >= MessageContent.getInstance().getInt(MessageType.CONFIG, "maximumVillagerByChunk")) event.setCancelled(true);
        } else {
            AtomicInteger count = new AtomicInteger();
            Arrays.stream(chunk.getEntities()).forEach(entity -> {
                if (entity.getType().isAlive() && !entity.getType().equals(EntityType.VILLAGER)) count.getAndIncrement();
            });
            if (count.get() >= MessageContent.getInstance().getInt(MessageType.CONFIG, "maximumEntityByChunk")) event.setCancelled(true);
        }
    }
}
