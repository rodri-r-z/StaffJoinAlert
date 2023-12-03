package me.rodrigo.staffsjoinalerts.fusion;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.rodrigo.staffsjoinalerts.StaffsJoinAlerts;
import me.rodrigo.staffsjoinalerts.lib.Parser;
import net.kyori.adventure.text.Component;

import java.io.FileNotFoundException;

public class ReloadCommand implements SimpleCommand {
    private final StaffsJoinAlerts plugin;

    public ReloadCommand(StaffsJoinAlerts basePlugin) {
        this.plugin = basePlugin;
    }

    @Override
    public void execute(Invocation invocation) {
        try {
            if (invocation.source() instanceof Player) {
                invocation.source().sendMessage(Component.text(plugin.config.AsString("no_permission").replaceAll("&", "§")));
                return;
            }
            plugin.config = new Parser(plugin.dataFolder.resolve("config.yml"));
            plugin.logger.info(plugin.config.AsString("config_reloaded").replaceAll("&", "§"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to reload config!", e);
        }
    }
}
