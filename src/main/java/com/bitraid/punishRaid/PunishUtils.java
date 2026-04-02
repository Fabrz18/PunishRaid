    package com.bitraid.punishRaid;

    import java.text.SimpleDateFormat;
    import java.util.Date;

    public class PunishUtils {

        public static String formatTime(long expireDate) {
            if (expireDate == -1) return "Permanente";

            long millis = expireDate - System.currentTimeMillis();
            if (millis <= 0) return "Expirando...";

            long seconds = millis / 1000;
            long days = seconds / (24 * 3600);
            seconds %= (24 * 3600);
            long hours = seconds / 3600;
            seconds %= 3600;
            long minutes = seconds / 60;

            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append("d ");
            if (hours > 0) sb.append(hours).append("h ");
            if (minutes > 0) sb.append(minutes).append("m ");
            if (sb.length() == 0) sb.append(seconds).append("s");

            return sb.toString().trim();
        }
        public static String formatDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(new Date(timestamp));
        }
        public static String getBanScreen(String target, String punisher, String reason, long issueDate, long expireDate) {
            String timeLeft = formatTime(expireDate);
            String dateStr = formatDate(issueDate);

            return "§c§lBITRAID NETWORK\n\n" +
                    "§fEstás suspendido de este servidor.\n\n" +
                    "§fJugador: §e" + target + "\n" +
                    "§fStaff: §e" + punisher + "\n" +
                    "§fFecha: §e" + dateStr + "\n" +
                    "§fMotivo: §e" + reason + "\n" +
                    "§fExpira en: §e" + timeLeft + "\n\n" +
                    "§fPuedes apelar en: §bdiscord.gg/bitraid";
        }
    }