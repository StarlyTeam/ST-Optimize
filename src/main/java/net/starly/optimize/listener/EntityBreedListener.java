package net.starly.optimize.listener;

import net.starly.optimize.OptimizeMain;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener implements Listener {

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (OptimizeMain.getInstance().getExceptWorlds().contains(event.getEntity().getWorld())) return;

        if (!MessageContent.getInstance().getBoolean(MessageType.CONFIG, "ableBreedEntity")) {
            Entity mother = event.getMother();
            Entity father = event.getFather();

            if (isValidBreed(mother) && isValidBreed(father)) {
                cancelBreedInteraction(mother);
                cancelBreedInteraction(father);
                event.setCancelled(true);
            }
        }
    }

    private void cancelBreedInteraction(Entity entity) {
        if (entity instanceof Animals) {
            Animals animal = (Animals) entity;
            animal.setBreed(false);
        }
    }

    private boolean isValidBreed(Entity entity) {
        if (entity instanceof Animals) return true;
        return false;
    }
}
