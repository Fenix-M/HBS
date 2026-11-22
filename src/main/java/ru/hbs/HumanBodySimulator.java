package ru.hbs;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hbs.anatomy.AnatomyRegistry;
import ru.hbs.command.HbsCommand;
import ru.hbs.listener.PlayerLifecycleListener;
import ru.hbs.storage.SqliteBodyRepository;

import java.util.Objects;

public final class HumanBodySimulator extends JavaPlugin {
    private SqliteBodyRepository repository;
    private AnatomyRegistry anatomyRegistry;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        anatomyRegistry = new AnatomyRegistry();
        repository = new SqliteBodyRepository(this);
        try {
            repository.initialize();
        } catch (Exception exception) {
            getLogger().severe("Database initialization failed: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(
                new PlayerLifecycleListener(this, repository, anatomyRegistry), this);
        PluginCommand command = Objects.requireNonNull(getCommand("hbs"));
        HbsCommand executor = new HbsCommand(this, repository, anatomyRegistry);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
        getLogger().info("HBS 0.1.0 enabled; anatomy structures: " + anatomyRegistry.size());
    }

    @Override
    public void onDisable() {
        if (repository != null) repository.close();
    }
}
