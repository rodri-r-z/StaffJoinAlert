package me.rodrigo.staffjoinalert.fusion;

import me.rodrigo.staffjoinalert.bukkit.BukkitStaffJoinAlert;
import me.rodrigo.staffjoinalert.lib.Parser;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public class ReloadCommandBukkit implements CommandExecutor {

    private final BukkitStaffJoinAlert basePlugin;
    public ReloadCommandBukkit(BukkitStaffJoinAlert basePlugin) {
        this.basePlugin = basePlugin;
    }

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        try {
            final BukkitStaffJoinAlert plugin = basePlugin;
            if (sender instanceof org.bukkit.entity.Player) {
                sender.sendMessage(plugin.config.AsString("no_permission"));
                return true;
            }
            plugin.config = new Parser(plugin.getDataFolder().toPath().resolve("config.yml"));
            plugin.getLogger().info(plugin.config.AsString("config_reloaded"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to reload config!", e);
        }
        return true;
    }
}
