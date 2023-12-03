package me.rodrigo.staffsjoinalerts.fusion;

import me.rodrigo.staffsjoinalerts.bungee.BungeeStaffJoinAlert;
import me.rodrigo.staffsjoinalerts.lib.Parser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.FileNotFoundException;

public class ReloadCommandBungee extends Command {
    private final BungeeStaffJoinAlert plugin;

    public ReloadCommandBungee(BungeeStaffJoinAlert basePlugin) {
        super("staffjoinalert");
        this.plugin = basePlugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (sender instanceof ProxiedPlayer) {
                sender.sendMessage(TextComponent.fromLegacyText(plugin.config.AsString("no_permission").replaceAll("&", "§")));
                return;
            }
            plugin.config = new Parser(plugin.getDataFolder().toPath().resolve("config.yml"));
            plugin.getLogger().info(plugin.config.AsString("config_reloaded").replaceAll("&", "§"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to reload config!", e);
        }
    }
}
