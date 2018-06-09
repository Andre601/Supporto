package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;

public class EmbedUtil {

    public static EmbedBuilder getEmbed(User user){
        return new EmbedBuilder().setFooter(MessageFormat.format(
                "Requested by: {0}",
                MessageUtil.getTag(user)
        ), user.getEffectiveAvatarUrl());
    }

    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder();
    }

}
