package com.andre601.suggesto.listener;

import com.andre601.suggesto.commands.commandUtils.CommandHandler;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.PermUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e){
        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        if(msg.isFromType(ChannelType.PRIVATE))
            return;

        if((msg.getContentRaw().equals(g.getJDA().getSelfUser().getAsMention())) &&
                (msg.getAuthor().getId() != msg.getJDA().getSelfUser().getId()) && !msg.getAuthor().isBot()){
            tc.sendMessage(MessageFormat.format(
                    "{0} My prefix is {1}",
                    msg.getAuthor().getAsMention(),
                    Database.getPrefix(g)
            )).queue();
            return;
        }

        if(msg.getContentRaw().startsWith(Database.getPrefix(g)) &&
                (!msg.getAuthor().getId().equals(msg.getJDA().getSelfUser().getId())) && (!msg.getAuthor().isBot())) {

            if (PermUtil.canDelete(tc))
                msg.delete().queue();

            if (tc.getId().equals(Database.getSupportChannel(g)) ||
                    tc.getId().equals(Database.getSuggestionChannel(g))) {

                tc.sendMessage(MessageFormat.format(
                        "{0} You can't use commands in a support or suggestion-channel!",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            CommandHandler.handleCommand(CommandHandler.parser.parse(msg.getContentRaw(), e));
        }

        if (tc.getId().equals(Database.getSupportChannel(g))) {

        }else
        if (tc.getId().equals(Database.getSuggestionChannel(g))) {

        }
    }
}
