package me.rodrigo.staffsjoinalerts.bungee;

import me.rodrigo.staffsjoinalerts.fusion.ReloadCommandBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import me.rodrigo.staffsjoinalerts.lib.Parser;
import me.rodrigo.staffsjoinalerts.lib.RegisteredPlayer;
import me.rodrigo.staffsjoinalerts.network.Http;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.event.EventHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Logger;

public class BungeeStaffJoinAlert extends Plugin implements Listener {
    private Logger logger;
    private ProxyServer proxy;
    private LuckPerms luckPerms;
    public Parser config;

    @Override
    public void onEnable() {
        final Path dataFolder = getDataFolder().toPath();

        this.logger = getLogger();
        this.proxy = getProxy();

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
                logger.severe("Failed to create/read config file! Error:"+e);
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
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new ReloadCommandBungee(this));
    }

    @EventHandler
    private void onPostLogin(PostLoginEvent e) {
        proxy.getScheduler().runAsync(this, () -> {
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
            proxy.getPlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(TextComponent.fromLegacyText(
                    finalMessage.replaceAll("&", "§")
            )));
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        proxy.getScheduler().runAsync(this, () -> {
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
            proxy.getPlayers().stream().filter(a -> a.hasPermission("staffjoinalert.use")).forEach(a -> a.sendMessage(TextComponent.fromLegacyText(
                    finalMessage.replaceAll("&", "§")
            )));
        });
    }
}

