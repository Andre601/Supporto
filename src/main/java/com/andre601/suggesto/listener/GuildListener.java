package com.andre601.suggesto.listener;

import com.andre601.suggesto.SuggestoBot;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.LinkUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.text.MessageFormat;

public class GuildListener extends ListenerAdapter {

    private String getWebhookURL(){
        return SuggestoBot.getFile().getItem("config", "webhookURL");
    }

    public void onGuildJoin(GuildJoinEvent event){
        Guild guild = event.getGuild();

        if(Database.isBlacklisted(guild.getId())){
            guild.getOwner().getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage(MessageFormat.format(
                        "Your guild `{0}` got blacklisted with the reason `{1}`!\n" +
                                "For more information, feel free to join our discord: {2}",
                        guild.getName(),
                        Database.getReason(guild),
                        LinkUtil.INVITE_GUILD
                )).queue();
                SuggestoBot.getLogger().info("Left a blacklisted guild with a PM send!");
            }, throwable -> {
                SuggestoBot.getLogger().info("Left a blacklisted guild with no PM send!");
            });
            guild.leave().queue();
            return;
        }
        if(!Database.hasGuild(guild)) Database.createDB(guild);

        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.GREEN, "Joined Guild");
    }

    public void onGuildLeave(GuildLeaveEvent event){
        Guild guild = event.getGuild();

        if(Database.isBlacklisted(guild.getId())) return;

        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.RED, "Left Guild");
    }


}
