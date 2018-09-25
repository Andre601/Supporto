package com.andre601.suggesto.listener;

import com.andre601.suggesto.utils.Database;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event){
        TextChannel tc = event.getTextChannel();
        Guild guild = event.getGuild();

        if(!tc.getId().equals(Database.getSupportChannel(guild))) return;

        String msg = event.getMessage().getContentRaw();
    }

}
