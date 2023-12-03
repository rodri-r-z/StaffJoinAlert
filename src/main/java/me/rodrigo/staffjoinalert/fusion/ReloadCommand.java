package me.rodrigo.staffjoinalert.fusion;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.rodrigo.staffjoinalert.StaffJoinAlert;
import me.rodrigo.staffjoinalert.bukkit.BukkitStaffJoinAlert;
import me.rodrigo.staffjoinalert.bungee.BungeeStaffJoinAlert;
import me.rodrigo.staffjoinalert.lib.Parser;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public class ReloadCommand implements SimpleCommand {
    private final StaffJoinAlert basePlugin;

    public ReloadCommand(StaffJoinAlert basePlugin) {
        this.basePlugin = basePlugin;
    }

    @Override
    public void execute(Invocation invocation) {
        try {
            final StaffJoinAlert plugin = (StaffJoinAlert) basePlugin;
            if (invocation.source() instanceof Player) {
                invocation.source().sendMessage(Component.text(plugin.config.AsString("no_permission")));
                return;
            }
            plugin.config = new Parser(plugin.dataFolder.resolve("config.yml"));
            plugin.logger.info(plugin.config.AsString("config_reloaded"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to reload config!", e);
        }
    }
}
