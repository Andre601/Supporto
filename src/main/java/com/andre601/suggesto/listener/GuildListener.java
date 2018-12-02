package com.andre601.suggesto.listener;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class GuildListener extends ListenerAdapter {

    private String getWebhookURL(){
        return Supporto.getFile().getItem("config", "webhookURL");
    }

    public void onGuildJoin(GuildJoinEvent event){
        Guild guild = event.getGuild();
        if(!Database.hasGuild(guild)) Database.createDB(guild);

        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.GREEN, "Joined Guild");
    }

    public void onGuildLeave(GuildLeaveEvent event){
        Guild guild = event.getGuild();

        EmbedUtil.sendWebhook(getWebhookURL(), guild, Color.RED, "Left Guild");
    }


}
