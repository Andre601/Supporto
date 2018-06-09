package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CheckUtil {

    public static boolean checkMsg(Message msg){
        TextChannel tc = msg.getTextChannel();
        User author = msg.getAuthor();
        String raw = msg.getContentRaw();
        Guild g = msg.getGuild();

        if(tc.getId().equals(Database.getSuggestionChannel(g))){
            List<String> items = new ArrayList<>();
            Stream.of("[title]", "[description]", "[link]").forEach(items::add);

            if(contains(raw, items))
                return true;
        }
        return false;
    }

    private static boolean contains(String msg, List<String> contain){
        return contain.parallelStream().allMatch(msg.toLowerCase()::contains);
    }

}
