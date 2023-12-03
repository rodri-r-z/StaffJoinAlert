package me.rodrigo.staffjoinalert.lib;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

public class RegisteredPlayer {
    public static String getUserPrefix(String nick, LuckPerms luckPerms) {
        final User user = luckPerms.getUserManager().getUser(nick);
        if (user == null) {
            return null;
        }
        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) {
            prefix = "";
        }
        prefix = prefix.trim();
        return prefix;
    }
}
