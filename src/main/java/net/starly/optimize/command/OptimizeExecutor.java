package net.starly.optimize.command;

import net.starly.optimize.OptimizeMain;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import net.starly.optimize.listener.PlayerJoinListener;
import net.starly.optimize.util.CleanUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OptimizeExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        MessageContent content = MessageContent.getInstance();
        OptimizeMain plugin = OptimizeMain.getInstance();

        if (args.length != 1) {
            content.getMessages(MessageType.NORMAL, "helpMessage")
                    .forEach(messages -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages)));
            return false;
        }

        switch (args[0]) {
            case "리로드": {
                if (!sender.hasPermission("starly.optimize.reload")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    return false;
                }

                plugin.reloadConfig();
                content.initialize(plugin.getConfig());

                plugin.getExceptWorlds().clear();
                content.getMessages(MessageType.CONFIG,"exceptWorld").forEach(string -> {
                    World world = plugin.getServer().getWorld(string);
                    if (world != null) {
                        plugin.getExceptWorlds().add(world);
                    }
                });

                content.getMessageAfterPrefix(MessageType.NORMAL,"reloadCompleted").ifPresent(sender::sendMessage);
                return true;
            }

            case "바닥청소": {
                if (!sender.hasPermission("starly.optimize.clean")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    return false;
                }

                MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "itemRemoved").ifPresent(plugin.getServer()::broadcastMessage);
                CleanUtil.clearItem();
                return true;
            }

            case "청크언로드": {
                if (!sender.hasPermission("starly.optimize.chunkunload")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    return false;
                }

                plugin.getServer().getWorlds()
                        .stream()
                        .filter(world -> !plugin.getExceptWorlds().contains(world))
                        .forEach(world -> Arrays.stream(world.getLoadedChunks())
                                .forEach(chunk -> {
                                    if (Arrays.stream(chunk.getEntities()).anyMatch(entity -> entity instanceof Player)) return;
                                    chunk.unload();
                                })
                        );
                content.getMessageAfterPrefix(MessageType.NORMAL, "chunkUnloaded").ifPresent(sender::sendMessage);
                return true;
            }

            case "접속토글": {
                if (!sender.hasPermission("starly.optimize.jointoggle")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    return false;
                }

                if (PlayerJoinListener.isBlockJoin()) {
                    PlayerJoinListener.setBlockJoin(false);
                    content.getMessageAfterPrefix(MessageType.NORMAL, "joinBlockDisabled").ifPresent(sender::sendMessage);
                } else {
                    PlayerJoinListener.setBlockJoin(true);
                    content.getMessageAfterPrefix(MessageType.NORMAL, "joinBlockEnabled").ifPresent(sender::sendMessage);
                }
                return true;
            }

            default: {
                content.getMessages(MessageType.NORMAL, "helpMessage").forEach(string ->
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string)));
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("리로드", "바닥청소", "청크언로드", "접속토글");
        }
        return Collections.emptyList();
    }
}
