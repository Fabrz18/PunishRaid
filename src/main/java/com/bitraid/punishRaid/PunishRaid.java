package com.bitraid.punishRaid;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class PunishRaid extends JavaPlugin {

    private Database db;

    @Override
    public void onEnable() {
        db = new Database(this);
        try {
            db.connect();
        } catch (SQLException e) {
            getLogger().severe("¡Fallo crítico! No se pudo conectar a punishments.db");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        PunishCommands cmdExecutor = new PunishCommands(this);
        if (getCommand("ban") != null) getCommand("ban").setExecutor(cmdExecutor);
        if (getCommand("tempban") != null) getCommand("tempban").setExecutor(cmdExecutor);
        if (getCommand("mute") != null) getCommand("mute").setExecutor(cmdExecutor);
        if (getCommand("unban") != null) getCommand("unban").setExecutor(cmdExecutor);
        if (getCommand("unmute") != null) getCommand("unmute").setExecutor(cmdExecutor);
        if (getCommand("history") != null) getCommand("history").setExecutor(cmdExecutor);

        Bukkit.getPluginManager().registerEvents(new PunishEvents(this), this);

        getLogger().info("=======================================");
        getLogger().info("PunishRaid (v1.0) cargado correctamente.");
        getLogger().info("-> Sistema de moderación y SQLite activos.");
        getLogger().info("=======================================");
    }

    @Override
    public void onDisable() {
        try {
            if (db != null) db.close();
        } catch (SQLException ignored) {}
    }

    public Database getDb() {
        return db;
    }
}