package ru.hbs.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.hbs.HumanBodySimulator;
import ru.hbs.anatomy.AnatomyPart;
import ru.hbs.anatomy.AnatomyRegistry;
import ru.hbs.model.BodyProfile;
import ru.hbs.model.PartCondition;
import ru.hbs.storage.SqliteBodyRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public final class HbsCommand implements CommandExecutor, TabCompleter {
    private final HumanBodySimulator plugin;
    private final SqliteBodyRepository repository;
    private final AnatomyRegistry anatomy;

    public HbsCommand(HumanBodySimulator plugin, SqliteBodyRepository repository, AnatomyRegistry anatomy) {
        this.plugin = plugin; this.repository = repository; this.anatomy = anatomy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Для консоли используй: /hbs inspect <ник>");
                return true;
            }
            showStatus(sender, player.getUniqueId());
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "inspect" -> {
                if (!sender.hasPermission("hbs.medic")) { sender.sendMessage("§cНедостаточно прав."); return true; }
                if (args.length < 2) { sender.sendMessage("§e/hbs inspect <игрок>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cИгрок должен быть онлайн."); return true; }
                showStatus(sender, target.getUniqueId());
                return true;
            }
            case "reload" -> {
                if (!sender.hasPermission("hbs.admin")) { sender.sendMessage("§cНедостаточно прав."); return true; }
                plugin.reloadConfig();
                sender.sendMessage("§aКонфигурация HBS перезагружена.");
                return true;
            }
            default -> {
                sender.sendMessage("§eИспользование: /hbs <status|inspect <игрок>|reload>");
                return true;
            }
        }
    }

    private void showStatus(CommandSender sender, UUID uuid) {
        try {
            Optional<BodyProfile> result = repository.load(uuid);
            if (result.isEmpty()) { sender.sendMessage("§cПрофиль ещё не создан: игрок должен зайти на сервер."); return; }
            BodyProfile profile = result.get();
            sender.sendMessage(Component.text("══ Human Body Simulator ══", NamedTextColor.RED));
            sender.sendMessage(Component.text("Кровь: " + fmt(profile.bloodPercent()) + "%", NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Кислород: " + fmt(profile.oxygenPercent()) + "%", NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Сознание: " + fmt(profile.consciousnessPercent()) + "%", NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Структур: " + profile.parts().size(), NamedTextColor.GRAY));
            for (Map.Entry<AnatomyPart, PartCondition> e : profile.parts().entrySet()) {
                sender.sendMessage(Component.text(e.getKey().displayName() + ": целостность "
                        + fmt(e.getValue().integrity()) + "%, функция "
                        + fmt(e.getValue().functionality()) + "%", NamedTextColor.DARK_GRAY));
            }
        } catch (SQLException ex) {
            sender.sendMessage("§cОшибка чтения медицинской карты. Проверь консоль.");
            plugin.getLogger().severe("HBS query failed: " + ex.getMessage());
        }
    }

    private String fmt(double value) { return String.format(Locale.US, "%.1f", value); }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("status", "inspect", "reload").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (args.length == 2 && args[0].equalsIgnoreCase("inspect"))
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }
}
