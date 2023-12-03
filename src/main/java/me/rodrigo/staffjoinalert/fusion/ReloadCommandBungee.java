package me.rodrigo.staffjoinalert.fusion;

import me.rodrigo.staffjoinalert.bungee.BungeeStaffJoinAlert;
import me.rodrigo.staffjoinalert.lib.Parser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.FileNotFoundException;

public class ReloadCommandBungee extends Command {
    private final BungeeStaffJoinAlert basePlugin;

    public ReloadCommandBungee(BungeeStaffJoinAlert basePlugin) {
        super("staffjoinalert");
        this.basePlugin = basePlugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            final BungeeStaffJoinAlert plugin = (BungeeStaffJoinAlert) basePlugin;
            if (sender instanceof ProxiedPlayer) {
                sender.sendMessage(TextComponent.fromLegacyText(plugin.config.AsString("no_permission")));
                return;
            }
            plugin.config = new Parser(plugin.getDataFolder().toPath().resolve("config.yml"));
            plugin.getLogger().info(plugin.config.AsString("config_reloaded"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to reload config!", e);
        }
    }
}
