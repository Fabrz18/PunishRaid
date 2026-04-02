package com.bitraid.punishRaid;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class PunishCommands implements CommandExecutor {

    private final PunishRaid plugin;

    public PunishCommands(PunishRaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("punishraid.admin")) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        String cmd = command.getName().toLowerCase();
        String punisherName = sender.getName();

        try {
            switch (cmd) {
                case "ban":
                    if (args.length < 2) return error(sender, "/ban <jugador> <motivo>");
                    String banTarget = args[0];
                    String banReason = buildReason(args, 1);
                    long expireBan = -1;

                    plugin.getDb().addPunishment(banTarget, "BAN", banReason, punisherName, expireBan);
                    kickIfOnline(banTarget,punisherName, banReason, expireBan);
                    Bukkit.broadcastMessage("§8[§cPunishRaid§8] §c" + banTarget + " §fha sido baneado por §c" + punisherName);
                    return true;

                case "tempban":
                    if (args.length < 3) return error(sender, "/tempban <jugador> <tiempo ej: 1d, 2h> <motivo>");
                    String tempTarget = args[0];
                    long durationMs = parseTime(args[1]);
                    if (durationMs == -1) {
                        sender.sendMessage("§cFormato inválido. Usa m (min), h (horas), d (días).");
                        return true;
                    }
                    String tempReason = buildReason(args, 2);
                    long expireTemp = System.currentTimeMillis() + durationMs;

                    plugin.getDb().addPunishment(tempTarget, "BAN", tempReason, punisherName, expireTemp);
                    kickIfOnline(tempTarget, punisherName, tempReason, expireTemp);
                    Bukkit.broadcastMessage("§8[§cPunishRaid§8] §c" + tempTarget + " §fha sido baneado temporalmente.");
                    return true;

                case "mute":
                    if (args.length < 2) return error(sender, "/mute <jugador> <motivo>");
                    String muteTarget = args[0];
                    String muteReason = buildReason(args, 1);

                    plugin.getDb().addPunishment(muteTarget, "MUTE", muteReason, punisherName, -1);
                    sender.sendMessage("§aHas silenciado a " + muteTarget + ".");

                    Player pTarget = Bukkit.getPlayerExact(muteTarget);
                    if (pTarget != null) pTarget.sendMessage("§cHas sido silenciado permanentemente. Motivo: " + muteReason);
                    return true;

                case "unban":
                    if (args.length != 1) return error(sender, "/unban <jugador>");
                    plugin.getDb().removePunishment(args[0], "BAN");
                    sender.sendMessage("§aHas perdonado (desbaneado) a " + args[0] + ".");
                    return true;

                case "unmute":
                    if (args.length != 1) return error(sender, "/unmute <jugador>");
                    plugin.getDb().removePunishment(args[0], "MUTE");
                    sender.sendMessage("§aHas desmuteado a " + args[0] + ".");

                    Player pUnmute = Bukkit.getPlayerExact(args[0]);
                    if (pUnmute != null) pUnmute.sendMessage("§aTu silencio ha sido levantado. Ya puedes hablar.");
                    return true;
                case "history":
                    if (args.length != 1) return error(sender, "/history <jugador>");
                    String histTarget = args[0];
                    java.util.List<Database.PunishmentData> history = plugin.getDb().getHistory(histTarget);

                    if (history.isEmpty()) {
                        sender.sendMessage("§eEl jugador " + histTarget + " no tiene historial de sanciones.");
                        return true;
                    }

                    sender.sendMessage("§8================ §cHistorial de " + histTarget + " §8================");
                    for (Database.PunishmentData h : history) {
                        String status = (h.expireDate == -1 || h.expireDate > System.currentTimeMillis()) ? "§a[ACTIVO]" : "§c[PASADO]";
                        String dateStr = PunishUtils.formatDate(h.issueDate);

                        sender.sendMessage("§8- " + status + " §e" + h.type + " §f| §7Por: §b" + h.punisher + " §f| §7Fecha: §b" + dateStr);
                        sender.sendMessage("  §7Motivo: §f" + h.reason);
                    }
                    sender.sendMessage("§8==================================================");
                    return true;
            }
        } catch (SQLException e) {
            sender.sendMessage("§cError conectando a la base de datos.");
        }
        return false;
    }

    private boolean error(CommandSender sender, String msg) {
        sender.sendMessage("§cUso: " + msg);
        return true;
    }

    private String buildReason(String[] args, int startIndex) {
        StringBuilder reason = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) reason.append(args[i]).append(" ");
        return reason.toString().trim();
    }

    private long parseTime(String timeStr) {
        try {
            char unit = timeStr.charAt(timeStr.length() - 1);
            long value = Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
            return switch (unit) {
                case 'm' -> value * 60L * 1000L;
                case 'h' -> value * 60L * 60L * 1000L;
                case 'd' -> value * 24L * 60L * 60L * 1000L;
                default -> -1;
            };
        } catch (Exception e) { return -1; }
    }

    private void kickIfOnline(String username, String punisher, String reason, long expireDate) {
        Player target = Bukkit.getPlayerExact(username);
        if (target != null) {
            target.kickPlayer(PunishUtils.getBanScreen(target.getName(), punisher, reason, System.currentTimeMillis(), expireDate));
        }
    }
}