package me.rodrigo.staffjoinalert.bukkit;

import me.rodrigo.staffjoinalert.fusion.ReloadCommandBukkit;
import me.rodrigo.staffjoinalert.lib.Parser;
import me.rodrigo.staffjoinalert.lib.RegisteredPlayer;
import me.rodrigo.staffjoinalert.network.Http;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Logger;

public class BukkitStaffJoinAlert extends JavaPlugin implements Listener {
    private Logger logger;
    private Server proxy;
    private LuckPerms luckPerms;
    public Parser config;

    @Override
    public void onEnable() {
        final Path dataFolder = getDataFolder().toPath();

        this.logger = getLogger();
        this.proxy = getServer();

        if (!dataFolder.toFile().exists() && !dataFolder.toFile().mkdir()) {
            logger.severe("Failed to create data folder!");
            return;
        }

        if (!dataFolder.resolve("config.yml").toFile().exists()) {
            try {
                final String config = Http.getFileContentByUrl(new URL("https://raw.githubusercontent.com/rodri-r-z/StaffJoinAlert/main/src/main/resources/config.yml"));
                if (!dataFolder.resolve("config.yml").toFile().createNewFile()) {
                    logger.severe("Failed to create config file!");
                    return;
                }
                final FileWriter writer = new FileWriter(dataFolder.resolve("config.yml").toFile());
                writer.write(config);
                writer.close();
                this.config = new Parser(dataFolder.resolve("config.yml"));
            } catch (IOException e) {
                logger.severe("Failed to create/read config file! Error:"+ e);
            }
        }

        if (config == null) return;
        if (config.AsBoolean("use_luckperms_api") && proxy.getPluginManager().getPlugin("LuckPerms") == null) {
            logger.severe("LuckPerms is not installed! StaffJoinAlert will not work.");
            return;
        }
        if (config.AsBoolean("use_luckperms_api")) {
            luckPerms = LuckPermsProvider.get();
        }
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("staffjoinalert").setExecutor(new ReloadCommandBukkit(this));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        proxy.getScheduler().runTaskAsynchronously(this, () -> {
            if (config == null) return;
            if (!e.getPlayer().hasPermission("staffjoinalert.use")) {
                return;
            }
            String message = config.AsString("on_join");
            if (message == null) {
                logger.severe("Your configl.yml is invalid or incomplete!");
                return;
            }

            if (config.AsBoolean("use_luckperms_api") && luckPerms == null) return;

            if (config.AsBoolean("use_luckperms_api")) {
                final String prefix = RegisteredPlayer.getUserPrefix(e.getPlayer().getName(), luckPerms);
                if (prefix == null) {
                    logger.severe("User " + e.getPlayer().getName() + " not found on LuckPerms. This is not a StaffJoinAlert issue.");
                    return;
                }

                message = message.replaceAll("(?i)\\{luckperms_prefix\\}", prefix);
            }

            message = message.replaceAll("(?i)\\{player\\}", e.getPlayer().getName());
            String finalMessage = message;
            proxy.getOnlinePlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(
                    finalMessage.replaceAll("&", "§")
            ));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        proxy.getScheduler().runTaskAsynchronously(this, () -> {
            if (config == null) return;
            if (!e.getPlayer().hasPermission("staffjoinalert.use")) {
                return;
            }
            String message = config.AsString("on_leave");
            if (message == null) {
                logger.severe("Your configl.yml is invalid or incomplete!");
                return;
            }

            if (config.AsBoolean("use_luckperms_api") && luckPerms == null) return;

            if (config.AsBoolean("use_luckperms_api")) {
                final String prefix = RegisteredPlayer.getUserPrefix(e.getPlayer().getName(), luckPerms);
                if (prefix == null) {
                    logger.severe("User " + e.getPlayer().getName() + " not found on LuckPerms. This is not a StaffJoinAlert issue.");
                    return;
                }

                message = message.replaceAll("(?i)\\{luckperms_prefix\\}", prefix);
            }

            message = message.replaceAll("(?i)\\{player\\}", e.getPlayer().getName());
            String finalMessage = message;
            proxy.getOnlinePlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(
                    finalMessage.replaceAll("&", "§")
            ));
        });
    }
}
