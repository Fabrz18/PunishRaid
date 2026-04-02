package com.bitraid.punishRaid;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;

public class PunishEvents implements Listener {

    private final PunishRaid plugin;

    public PunishEvents(PunishRaid plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            Database.PunishmentData data = plugin.getDb().getPunishment(event.getName(), "BAN");
            if (data != null) {
                String screen = PunishUtils.getBanScreen(event.getName(), data.punisher, data.reason, data.issueDate, data.expireDate);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, screen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        try {
            Database.PunishmentData data = plugin.getDb().getPunishment(event.getPlayer().getName(), "MUTE");
            if (data != null) {
                event.setCancelled(true);
                String timeLeft = PunishUtils.formatTime(data.expireDate);
                event.getPlayer().sendMessage("§cEstás silenciado. Motivo: §e" + data.reason + " §c(Expira en: " + timeLeft + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}