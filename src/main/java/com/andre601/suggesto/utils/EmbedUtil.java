package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;
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

    public static void sendError(Message msg, String error){
        TextChannel tc = msg.getTextChannel();
        EmbedBuilder errorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(error);

        tc.sendMessage(errorEmbed.build()).queue();
    }

}
