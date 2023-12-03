Broadcasts to online staff members when staff members join/leave the server.

**Supports:** Velocity, Bukkit (Spigot and Paper), BungeeCord (Waterfall and any trusted fork of Bungee)

**Depends on:** [LuckPerms](https://luckperms.net/download)

**Setup:**
- [Download LuckPerms](https://luckperms.net/download) and put it into your plugins folder
- [Download the plugin](https://www.spigotmc.org/resources/staffs-join-alerts-bungee-bukkit-velocity-broadcast-alerts-to-staffs-when-staffs-joins.113824/) and put it into your plugins folder
- Restart your server

[**Open source**](https://github.com/rodri-r-z/StaffJoinAlert)
[**Get this resource on Spigot**](https://www.spigotmc.org/resources/staffs-join-alerts-bungee-bukkit-velocity-broadcast-alerts-to-staffs-when-staffs-joins.113824/)

**Default config**

```yml
# StaffJoinAlert
# By: Rodrigo R.
# Version 1.0

# Set to false if you don't want to use LuckPerms.
# LuckPerms API is used to retrieve a player's prefix
use_luckperms_api: true

on_join: "&b[Staff] {luckperms_prefix} {player} joined the server."
# Message broadcasted to all players that are staffs when another staff enters the server
on_leave: "&b[Staff] {luckperms_prefix} {player} left the server."
# Message broadcasted to all players that are staffs when another staff leaves the server
# If you're not using the LuckPerms API, remove {luckperms_prefix} from the message above

config_reloaded: "&b[StaffJoinAlert] Config reloaded."
no_permission: "&b[StaffJoinAlert] &cYou don't have permission to use this command."
# Only console can reload the config
# Permissions to alerts:
# staffjoinalert.use[/SPOILER]
```
