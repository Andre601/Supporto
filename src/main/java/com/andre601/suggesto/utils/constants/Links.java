package com.andre601.suggesto.utils.constants;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;

public final class Links {

    public static String INVITE_BOT(JDA jda){
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

    public static String INVITE_GUILD = "https://discord.gg/W79Pbaw";
    public static String GITHUB       = "https://github.com/Andre601/Supporto";
}
