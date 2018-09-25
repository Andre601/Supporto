package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;

public class EmbedUtil {

    public static EmbedBuilder getEmbed(User user){
        return getEmbed().setFooter(MessageFormat.format(
                "Requested by: {0}",
                MessageUtil.getTag(user)
        ), user.getEffectiveAvatarUrl()).setTimestamp(LocalDateTime.now());
    }

    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder().setTimestamp(LocalDateTime.now()).setColor(new Color(54, 57, 63));
    }

}
