# PunishRaid 🔨

**PunishRaid** es un plugin de moderación ligero y eficiente, diseñado específicamente para servidores de Minecraft pequeños (aprox. 20 jugadores). Proporciona un control total sobre las sanciones del servidor sin la necesidad de configuraciones complejas ni bases de datos externas pesadas.

Todo se gestiona de forma local mediante **SQLite**, lo que garantiza un rendimiento óptimo y un registro inmutable del historial de los jugadores.

---

## ✨ Características Principales

* **Sanciones Precisas:** Sistema de baneos (permanentes y temporales) y mutes.
* **Historial Inmutable:** Los castigos nunca se borran por completo. Las sanciones expiradas o perdonadas quedan registradas en el historial del jugador para futuras referencias.
* **Pantallas de Expulsión Limpias:** Interfaz de desconexión detallada que muestra al jugador el staff responsable, el motivo exacto, la fecha de emisión y el tiempo restante.
* **Formatos de Tiempo Intuitivos:** Soporte para expulsiones temporales usando un formato simplificado (`m` = minutos, `h` = horas, `d` = días).
* **Plug & Play:** Al usar SQLite nativo, no requiere configurar puertos ni credenciales MySQL. Simplemente arrastra el archivo `.jar` y empieza a moderar.

---

## 📜 Comandos y Permisos

Todos los comandos requieren el permiso: `punishraid.admin` (o tener rango OP).

| Comando | Uso | Descripción |
| :--- | :--- | :--- |
| `/ban` | `/ban <jugador> <motivo>` | Banea permanentemente a un jugador y lo expulsa si está conectado. |
| `/tempban` | `/tempban <jugador> <tiempo> <motivo>` | Banea temporalmente a un jugador (Ej: `1d`, `12h`, `30m`). |
| `/mute` | `/mute <jugador> <motivo>` | Silencia permanentemente a un jugador, impidiendo que envíe mensajes al chat. |
| `/unban` | `/unban <jugador>` | Levanta el baneo de un jugador, cambiando su estado a [PASADO] en el historial. |
| `/unmute` | `/unmute <jugador>` | Devuelve los privilegios de chat a un jugador silenciado. |
| `/history` | `/history <jugador>` | Muestra un registro completo de todas las sanciones activas y pasadas del jugador. |

---

## ⚙️ Instalación

1. Descarga el archivo `PunishRaid.jar` más reciente desde la pestaña de *Releases*.
2. Coloca el archivo dentro de la carpeta `plugins/` de tu servidor de Minecraft (Soporta Bukkit/Spigot/Paper 1.21+).
3. Inicia o reinicia el servidor.
4. El plugin generará automáticamente su carpeta y el archivo local `punishments.db`. ¡Listo para usar!

---

## 🛠️ Tecnologías Utilizadas
* **Java 21**
* **Spigot/Paper API** (1.21)
* **SQLite** (Persistencia de datos)

---
*Desarrollado para la gestión de servidores locales y redes de pequeña escala.*
