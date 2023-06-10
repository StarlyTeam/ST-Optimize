package net.starly.optimize.scheduler;

import net.starly.optimize.OptimizeMain;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import net.starly.optimize.util.CleanUtil;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AutoCleanScheduler extends BukkitRunnable {

    private final OptimizeMain plugin = OptimizeMain.getInstance();
    private boolean sendRemain = false;
    private long time;
    private List<Long> sendTime;

    public void start() {
        MessageContent content = MessageContent.getInstance();

        long delay = content.getInt(MessageType.CONFIG, "autoRemove");
        if (delay <= 0) return;


        if (content.getMessages(MessageType.CONFIG, "sendRemainMessage").isEmpty()) {
            runTaskTimer(plugin, delay * 20, delay * 20);
            sendRemain = false;
        } else {
            sendTime = new ArrayList<>();
            content.getInts(MessageType.CONFIG, "sendRemainMessage").forEach(integer -> sendTime.add((long) integer));

            time = delay;
            sendRemain = true;
            runTaskTimer(plugin, 0L, 20L);
        }
    }

    @Override
    public void run() {

        if (sendRemain) {

            if (sendTime.contains(time))
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "autoRemoveRemain")
                        .ifPresent(s -> plugin.getServer().broadcastMessage(s.replaceAll("%second%", String.valueOf(time))));
            time--;

            if (time <= 0) {
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "itemRemoved").ifPresent(plugin.getServer()::broadcastMessage);
                CleanUtil.clearItem();
                time = MessageContent.getInstance().getInt(MessageType.CONFIG, "autoRemove");
            }

        } else CleanUtil.clearItem();
    }
}
