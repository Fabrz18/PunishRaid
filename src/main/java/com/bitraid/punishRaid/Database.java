package com.bitraid.punishRaid;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final PunishRaid plugin;
    private Connection connection;

    public Database(PunishRaid plugin) {
        this.plugin = plugin;
    }

    // Clase interna para devolver los datos del castigo fácilmente
    public static class PunishmentData {
        public String type;
        public String reason;
        public String punisher;
        public long issueDate;
        public long expireDate;

        public PunishmentData(String type, String reason, String punisher, long issueDate, long expireDate) {
            this.type = type;
            this.reason = reason;
            this.punisher = punisher;
            this.issueDate = issueDate;
            this.expireDate = expireDate;
        }
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) return;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File databaseFile = new File(dataFolder, "punishments.db");
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);

        createTable();
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(16) NOT NULL," +
                "type VARCHAR(10) NOT NULL," +
                "reason TEXT NOT NULL," +
                "punisher VARCHAR(16) NOT NULL," +
                "issue_date LONG NOT NULL," +
                "expire_date LONG NOT NULL" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void addPunishment(String username, String type, String reason, String punisher, long expireDate) throws SQLException {
        String sql = "INSERT INTO punishments (username, type, reason, punisher, issue_date, expire_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            pstmt.setString(2, type.toUpperCase());
            pstmt.setString(3, reason);
            pstmt.setString(4, punisher);
            pstmt.setLong(5, System.currentTimeMillis());
            pstmt.setLong(6, expireDate);
            pstmt.executeUpdate();
        }
    }

    public void removePunishment(String username, String type) throws SQLException {
        String sql = "UPDATE punishments SET expire_date = ? WHERE username = ? AND type = ? AND (expire_date = -1 OR expire_date > ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            long now = System.currentTimeMillis();
            pstmt.setLong(1, now);
            pstmt.setString(2, username.toLowerCase());
            pstmt.setString(3, type.toUpperCase());
            pstmt.setLong(4, now);
            pstmt.executeUpdate();
        }
    }

    public PunishmentData getPunishment(String username, String type) throws SQLException {
        // Añadimos ORDER BY id DESC para leer siempre la sanción más reciente
        String sql = "SELECT reason, punisher, issue_date, expire_date FROM punishments WHERE username = ? AND type = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            pstmt.setString(2, type.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long expireDate = rs.getLong("expire_date");

                    if (expireDate != -1 && System.currentTimeMillis() > expireDate) {
                        return null;
                    }

                    return new PunishmentData(type, rs.getString("reason"), rs.getString("punisher"), rs.getLong("issue_date"), expireDate);
                }
            }
        }
        return null;
    }
    public List<PunishmentData> getHistory(String username) throws SQLException {
        List<PunishmentData> history = new ArrayList<>();
        String sql = "SELECT type, reason, punisher, issue_date, expire_date FROM punishments WHERE username = ? ORDER BY id DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new PunishmentData(
                            rs.getString("type"),
                            rs.getString("reason"),
                            rs.getString("punisher"),
                            rs.getLong("issue_date"),
                            rs.getLong("expire_date")
                    ));
                }
            }
        }
        return history;
    }
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}