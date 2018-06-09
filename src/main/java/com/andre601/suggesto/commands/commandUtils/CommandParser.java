package com.andre601.suggesto.commands.commandUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandParser {

    public commandContainer parse(String raw, MessageReceivedEvent e){
        String beheaded = raw.replaceFirst("_", "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0].toLowerCase();
        ArrayList<String> split = new ArrayList<>();
        for(String s : splitBeheaded){
            split.add(s);
        }
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);

        return new commandContainer(raw, beheaded, splitBeheaded, invoke, args, e);
    }

    public class commandContainer{
        private final String raw;
        private final String beheaded;
        private final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;

        public commandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, String[] args,
                                MessageReceivedEvent event){
            this.raw = raw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }
    }
}
