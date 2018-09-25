package com.andre601.suggesto.listener;

import com.andre601.suggesto.utils.Database;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildListener extends ListenerAdapter {

    public void onGuildJoin(GuildJoinEvent event){
        Database.createDB(event.getGuild());
    }


}
