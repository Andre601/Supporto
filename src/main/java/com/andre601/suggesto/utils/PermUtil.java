package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PermUtil {

    //  Check for MESSAGE_MANAGE permission (For pinning messages, deleting messages, ect.)
    public static boolean canManageMsg(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE);
    }

    public static boolean isBot(Message msg){
        return msg.getAuthor().isBot();
    }

    public static boolean isSelf(Message msg){
        return msg.getAuthor() == msg.getJDA().getSelfUser();
    }

    public static boolean isDM(Message msg){
        return msg.isFromType(ChannelType.PRIVATE);
    }

    public static boolean isOwner(Message msg){
        return msg.getAuthor().getId().equals("");
    }

    public static boolean canSendMsg(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_WRITE);
    }

    public static boolean canSeeChannel(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_READ);
    }

    public static boolean canSeeHistory(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY);
    }

    public static boolean canEmbedLinks(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS);
    }

    public static boolean isAdmin(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getMember(), Permission.MANAGE_SERVER);
    }
}
