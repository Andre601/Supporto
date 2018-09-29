package com.andre601.suggesto.utils;

import com.andre601.suggesto.SuggestoBot;
import com.andre601.suggesto.listener.ReadyListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.Color;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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

    public static void sendWebhook(String url, Guild guild, Color color, String webhookName){
        MessageEmbed webhook = getEmbed()
                .setColor(color)
                .setThumbnail(guild.getIconUrl())
                .addField("Guild", MessageFormat.format(
                        "{0} (`{1}`)",
                        guild.getName(),
                        guild.getId()
                ), false)
                .addField("Owner", MessageFormat.format(
                        "{0} | {1}",
                        guild.getOwner().getAsMention(),
                        guild.getOwner().getEffectiveName()
                ), false)
                .addField("Members", MessageFormat.format(
                        "**Total**: {0}\n" +
                        "**Humans**: {1}\n" +
                        "**Bots**: {2}",
                        guild.getMembers().size(),
                        guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count(),
                        guild.getMembers().stream().filter(user -> user.getUser().isBot()).count()
                ), false)
                .setFooter(MessageFormat.format(
                        "Guild-count: {0}",
                        ReadyListener.getShards().getGuildCache().size()
                ), null)
                .setTimestamp(ZonedDateTime.now())
                .build();

        WebhookClient webhookClient = SuggestoBot.getWebhookClient(url);
        webhookClient.send(new WebhookMessageBuilder().addEmbeds(webhook)
                .setUsername(webhookName)
                .setAvatarUrl(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .build()
        );
        webhookClient.close();
    }

}
