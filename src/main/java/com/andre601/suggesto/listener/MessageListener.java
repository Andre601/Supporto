package com.andre601.suggesto.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e){


        List<String> content = new ArrayList<>();
        Stream.of("[issue]", "[description]", "[link]").forEach(content::add);


    }

}
