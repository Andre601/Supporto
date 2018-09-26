package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;

public class LinkUtil {

    public static String getInviteURL(JDA jda){
        return jda.asBot().getInviteUrl(
                Permission.MESSAGE_MANAGE,
                Permission.MANAGE_CHANNEL,
                Permission.MANAGE_ROLES,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ADD_REACTION
        );
    }

    public static String guild_invite = "https://discord.gg/W79Pbaw";

}
