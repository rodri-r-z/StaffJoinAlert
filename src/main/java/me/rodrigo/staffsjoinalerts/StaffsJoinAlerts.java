package me.rodrigo.staffsjoinalerts;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rodrigo.staffsjoinalerts.fusion.ReloadCommand;
import me.rodrigo.staffsjoinalerts.lib.Parser;
import me.rodrigo.staffsjoinalerts.lib.RegisteredPlayer;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "staffsjoinalerts",
        name = "StaffsJoinAlerts",
        version = "1.0-SNAPSHOT",
        authors = {"Rodrigo R."},
        description = "Broadcasts to online staff members when staff members join/leave the server."
)
public class StaffsJoinAlerts {

    @Inject
    public Logger logger;
    private final ProxyServer proxy;
    private LuckPerms luckPerms;
    public Parser config;
    public Path dataFolder;

    @Inject
    public StaffsJoinAlerts(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataFolder = dataFolder;

        if (!dataFolder.toFile().exists() && !dataFolder.toFile().mkdir()) {
            logger.error("Failed to create data folder!");
            return;
        }

        if (!dataFolder.resolve("config.yml").toFile().exists()) {
            try {
                final InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
                if (!dataFolder.resolve("config.yml").toFile().createNewFile()) {
                    logger.error("Failed to get config file!");
                    return;
                }
                Files.copy(stream, dataFolder.resolve("config.yml").toFile().toPath());
            } catch (IOException e) {
                logger.error("Failed to create config file! Error: "+e);
            }
        }
        try {
            this.config = new Parser(dataFolder.resolve("config.yml"));
        } catch (IOException e) {
            logger.error("Failed to read config file! Error: "+e);
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (config == null) return;
        if (config.AsBoolean("use_luckperms_api") && proxy.getPluginManager().getPlugin("luckperms").isEmpty()) {
            logger.error("LuckPerms is not installed! StaffJoinAlert will not work.");
            return;
        }
        if (config.AsBoolean("use_luckperms_api")) {
            luckPerms = LuckPermsProvider.get();
        }
        proxy.getCommandManager().register("staffjoinalert", new ReloadCommand(this));
    }

    @Subscribe
    private void onServerPreConnect(ServerPreConnectEvent e) {
        if (e.getPreviousServer() != null) return;
        proxy.getScheduler().buildTask(this, () -> {
            if (config == null) return;
            if (!e.getPlayer().hasPermission("staffjoinalert.use")) {
                return;
            }
            String message = config.AsString("on_join");
            if (message == null) {
                logger.error("Your configl.yml is invalid or incomplete!");
                return;
            }

            if (config.AsBoolean("use_luckperms_api") && luckPerms == null) return;

            if (config.AsBoolean("use_luckperms_api")) {
                final String prefix = RegisteredPlayer.getUserPrefix(e.getPlayer().getUsername(), luckPerms);
                if (prefix == null) {
                    logger.error("User " + e.getPlayer().getUsername() + " not found on LuckPerms. This is not a StaffJoinAlert issue.");
                    return;
                }

                message = message.replaceAll("(?i)\\{luckperms_prefix\\}", prefix);
            }

            message = message.replaceAll("(?i)\\{player\\}", e.getPlayer().getUsername());
            String finalMessage = message;
            proxy.getAllPlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(Component.text(
                    finalMessage.replaceAll("&", "§")
            )));
        }).schedule();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e) {
        proxy.getScheduler().buildTask(this, () -> {
            if (config == null) return;
            if (!e.getPlayer().hasPermission("staffjoinalert.use")) {
                return;
            }
            String message = config.AsString("on_leave");
            if (message == null) {
                logger.error("Your configl.yml is invalid or incomplete!");
                return;
            }

            if (config.AsBoolean("use_luckperms_api") && luckPerms == null) return;

            if (config.AsBoolean("use_luckperms_api")) {
                final String prefix = RegisteredPlayer.getUserPrefix(e.getPlayer().getUsername(), luckPerms);
                if (prefix == null) {
                    logger.error("User " + e.getPlayer().getUsername() + " not found on LuckPerms. This is not a StaffJoinAlert issue.");
                    return;
                }

                message = message.replaceAll("(?i)\\{luckperms_prefix\\}", prefix);
            }

            message = message.replaceAll("(?i)\\{player\\}", e.getPlayer().getUsername());
            String finalMessage = message;
            proxy.getAllPlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(Component.text(
                    finalMessage.replaceAll("&" ,"§")
            )));
        }).schedule();
    }
}
