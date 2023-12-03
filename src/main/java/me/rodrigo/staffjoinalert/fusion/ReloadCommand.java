package me.rodrigo.staffjoinalert.fusion;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.rodrigo.staffjoinalert.StaffJoinAlert;
import me.rodrigo.staffjoinalert.lib.Parser;
import net.kyori.adventure.text.Component;

import java.io.FileNotFoundException;

public class ReloadCommand implements SimpleCommand {
    private final StaffJoinAlert plugin;

    public ReloadCommand(StaffJoinAlert basePlugin) {
        this.plugin = basePlugin;
    }

    @Override
    public void execute(Invocation invocation) {
        try {
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
