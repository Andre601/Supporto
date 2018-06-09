package com.andre601.suggesto.listener;

import com.andre601.suggesto.utils.ConfigUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;

public class ReadyListener extends ListenerAdapter {

    ConfigUtil config = new ConfigUtil();

    private String getTickets(){
        return MessageFormat.format(
                "{0} created tickets | {1} created suggestions",
                String.valueOf(config.getProperty("ticketID")),
                String.valueOf(config.getProperty("suggestID")));
    }

    public void onReady(ReadyEvent e){

        e.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(getTickets()));

    }

}
