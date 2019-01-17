package com.andre601.suggesto.listener;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;

public class GuildListener extends ListenerAdapter {

    private String getWebhookURL() {
        return Supporto.getFile().getItem("config", "webhookURL");
    }

    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        if (!Database.hasGuild(guild)) Database.createDB(guild);

        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.GREEN, "Joined Guild");
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();

        Database.deleteGuild(guild);
        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.RED, "Left Guild");
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        guild.getTextChannels().stream()
                .filter(textChannel -> Database.hasTicket(textChannel.getId()))
                .filter(textChannel -> member.getUser().getId().equals(Database.getAuthorID(textChannel.getId())))
                .forEach(textChannel -> {
                    if(textChannel.getPermissionOverride(member) == null) {
                        textChannel.createPermissionOverride(member).setAllow(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EXT_EMOJI
                        ).queue();
                    }

                    EmbedBuilder join = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription(String.format(
                                    "%s re-joined the guild.\n" +
                                            "He/She was auto-added back to the guild.",
                                    member.getEffectiveName()
                            ));

                    textChannel.sendMessage(join.build()).queue();
                });
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event){
        Guild guild = event.getGuild();
        Member member = event.getMember();

        guild.getTextChannels().stream()
                .filter(textChannel -> Database.hasTicket(textChannel.getId()))
                .filter(textChannel -> member.getUser().getId().equals(Database.getAuthorID(textChannel.getId())))
                .forEach(textChannel -> {
                    EmbedBuilder leave = EmbedUtil.getEmbed()
                            .setColor(Color.RED)
                            .setDescription(String.format(
                                    "%s left the guild.",
                                    member.getEffectiveName()
                            ));

                    textChannel.sendMessage(leave.build()).queue();
                });
    }
}
