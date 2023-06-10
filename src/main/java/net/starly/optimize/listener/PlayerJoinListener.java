package net.starly.optimize.listener;

import lombok.Getter;
import lombok.Setter;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    @Getter
    @Setter
    private static boolean blockJoin = false;

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        if (!blockJoin) return;
        if (event.getPlayer().hasPermission("starly.optimize.bypass")) return;

        MessageContent.getInstance().getMessage(MessageType.NORMAL, "kickMessage")
                .ifPresent(s -> event.disallow(PlayerLoginEvent.Result.KICK_OTHER, s));
    }
}
