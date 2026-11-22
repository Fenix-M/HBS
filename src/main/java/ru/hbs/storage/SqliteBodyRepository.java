package ru.hbs.storage;

import org.bukkit.plugin.java.JavaPlugin;
import ru.hbs.anatomy.AnatomyPart;
import ru.hbs.model.BodyProfile;
import ru.hbs.model.PartCondition;

import java.io.File;
import java.sql.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SqliteBodyRepository {
    private final JavaPlugin plugin;
    private Connection connection;

    public SqliteBodyRepository(JavaPlugin plugin) { this.plugin = plugin; }

    public void initialize() throws SQLException {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) throw new SQLException("Cannot create data folder");
        File db = new File(folder, plugin.getConfig().getString("storage.file", "hbs.db"));
        connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA journal_mode=WAL");
            st.execute("PRAGMA foreign_keys=ON");
            st.execute("CREATE TABLE IF NOT EXISTS body_profiles (uuid TEXT PRIMARY KEY, blood REAL NOT NULL, oxygen REAL NOT NULL, consciousness REAL NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS body_parts (uuid TEXT NOT NULL, part TEXT NOT NULL, integrity REAL NOT NULL, functionality REAL NOT NULL, PRIMARY KEY(uuid, part), FOREIGN KEY(uuid) REFERENCES body_profiles(uuid) ON DELETE CASCADE)");
        }
    }

    public synchronized Optional<BodyProfile> load(UUID uuid) throws SQLException {
        double blood, oxygen, consciousness;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT blood, oxygen, consciousness FROM body_profiles WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                blood = rs.getDouble(1); oxygen = rs.getDouble(2); consciousness = rs.getDouble(3);
            }
        }
        Map<AnatomyPart, PartCondition> parts = new EnumMap<>(AnatomyPart.class);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT part, integrity, functionality FROM body_parts WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        parts.put(AnatomyPart.valueOf(rs.getString(1)),
                                new PartCondition(rs.getDouble(2), rs.getDouble(3)));
                    } catch (IllegalArgumentException ignored) {
                        plugin.getLogger().warning("Unknown anatomy entry skipped in database.");
                    }
                }
            }
        }
        return Optional.of(new BodyProfile(uuid, parts, blood, oxygen, consciousness));
    }

    public synchronized void save(BodyProfile profile) throws SQLException {
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO body_profiles(uuid,blood,oxygen,consciousness) VALUES(?,?,?,?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET blood=excluded.blood, oxygen=excluded.oxygen, consciousness=excluded.consciousness")) {
                ps.setString(1, profile.playerId().toString());
                ps.setDouble(2, profile.bloodPercent());
                ps.setDouble(3, profile.oxygenPercent());
                ps.setDouble(4, profile.consciousnessPercent());
                ps.executeUpdate();
            }
            try (PreparedStatement del = connection.prepareStatement("DELETE FROM body_parts WHERE uuid=?")) {
                del.setString(1, profile.playerId().toString()); del.executeUpdate();
            }
            try (PreparedStatement ins = connection.prepareStatement(
                    "INSERT INTO body_parts(uuid,part,integrity,functionality) VALUES(?,?,?,?)")) {
                for (Map.Entry<AnatomyPart, PartCondition> e : profile.parts().entrySet()) {
                    ins.setString(1, profile.playerId().toString());
                    ins.setString(2, e.getKey().name());
                    ins.setDouble(3, e.getValue().integrity());
                    ins.setDouble(4, e.getValue().functionality());
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void close() {
        if (connection == null) return;
        try { connection.close(); }
        catch (SQLException ex) { plugin.getLogger().severe("DB close failed: " + ex.getMessage()); }
    }
}
