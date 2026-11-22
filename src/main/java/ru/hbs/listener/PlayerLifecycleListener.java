package ru.hbs.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hbs.HumanBodySimulator;
import ru.hbs.anatomy.AnatomyRegistry;
import ru.hbs.model.BodyProfile;
import ru.hbs.storage.SqliteBodyRepository;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerLifecycleListener implements Listener {
    private final HumanBodySimulator plugin;
    private final SqliteBodyRepository repository;
    private final AnatomyRegistry anatomy;
    private final Map<UUID, BodyProfile> cache = new ConcurrentHashMap<>();

    public PlayerLifecycleListener(HumanBodySimulator plugin, SqliteBodyRepository repository,
                                   AnatomyRegistry anatomy) {
        this.plugin = plugin; this.repository = repository; this.anatomy = anatomy;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            BodyProfile profile;
            try {
                profile = repository.load(uuid).orElseGet(() -> BodyProfile.healthy(uuid, anatomy.all()));
                repository.save(profile);
            } catch (SQLException ex) {
                plugin.getLogger().severe("Profile load failed for " + uuid + ": " + ex.getMessage());
                return;
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (event.getPlayer().isOnline()) {
                    cache.put(uuid, profile);
                    event.getPlayer().sendMessage("§8[§cHBS§8] §7Профиль организма загружен.");
                }
            });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BodyProfile profile = cache.remove(event.getPlayer().getUniqueId());
        if (profile == null) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try { repository.save(profile); }
            catch (SQLException ex) {
                plugin.getLogger().severe("Profile save failed: " + ex.getMessage());
            }
        });
    }

    public BodyProfile getCached(UUID uuid) { return cache.get(uuid); }
}
