package net.starly.optimize;

import lombok.Getter;
import net.starly.core.bstats.Metrics;
import net.starly.optimize.command.OptimizeExecutor;
import net.starly.optimize.context.MessageContent;
import net.starly.optimize.context.MessageType;
import net.starly.optimize.listener.EntityBreedListener;
import net.starly.optimize.listener.PlayerJoinListener;
import net.starly.optimize.listener.EntitySpawnListener;
import net.starly.optimize.scheduler.AutoCleanScheduler;
import net.starly.optimize.util.CleanUtil;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class OptimizeMain extends JavaPlugin {

    @Getter
    private static OptimizeMain instance;

    @Getter
    private final List<World> exceptWorlds = new ArrayList<>();

    @Getter
    private AutoCleanScheduler scheduler;

    @Override
    public void onEnable() {
        /* DEPENDENCY
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        if (!isPluginEnabled("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        saveDefaultConfig();
        MessageContent.getInstance().initialize(getConfig());

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        instance = this;
        new Metrics(this, 18705);

        CleanUtil.init();

        MessageContent.getInstance().getMessages(MessageType.CONFIG,"exceptWorld").forEach(string -> {
            World world = getServer().getWorld(string);
            if (world != null) {
                exceptWorlds.add(world);
            }
        });

        /* SCHEDULER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        scheduler = new AutoCleanScheduler();
        scheduler.start();

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getCommand("최적화").setExecutor(new OptimizeExecutor());
        getCommand("최적화").setTabCompleter(new OptimizeExecutor());

        /* LISTENER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getServer().getPluginManager().registerEvents(new EntitySpawnListener(),this);
        getServer().getPluginManager().registerEvents(new EntityBreedListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {
        if (scheduler != null) scheduler.cancel();
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
