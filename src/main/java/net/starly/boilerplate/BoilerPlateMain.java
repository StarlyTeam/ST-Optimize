package net.starly.boilerplate;

import net.starly.core.bstats.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BoilerPlateMain extends JavaPlugin {

    private static BoilerPlateMain instance;
    public static BoilerPlateMain getInstance() {
        return instance;
    }


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

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        instance = this;
        new Metrics(this, 12345); // TODO: 수정

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        // TODO: 수정

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        // TODO: 수정

        /* LISTENER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        // TODO: 수정
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
