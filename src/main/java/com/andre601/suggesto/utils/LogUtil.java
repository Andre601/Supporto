package com.andre601.suggesto.utils;

import com.andre601.suggesto.utils.constants.Emotes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LogUtil {

    private static TextChannel getLogChannel(Guild guild){
        TextChannel textChannel;
        try{
            textChannel = guild.getTextChannelById(Database.getLogChannel(guild));
        }catch (Exception ex){
            textChannel = null;
        }

        return textChannel;
    }

    private static Member getTicketAuthor(Guild guild, TextChannel tc){
        Member member;
        try{
            member = guild.getMemberById(Database.getAuthorID(tc.getId()));
        }catch (Exception ex){
            member = null;
        }

        return member;
    }

    public static void sendTranscript(TextChannel ticket){

        Guild guild = ticket.getGuild();

        if(Database.getDMSetting(guild).equalsIgnoreCase("off")) return;

        MessageHistory history = ticket.getHistory();
        while(history.retrievePast(100).complete().size() > 0);

        List<String> transcripts = new LinkedList<>();
        List<String> transcriptFile = new LinkedList<>();
        for(Message message : history.getRetrievedHistory()){
            String timestamp = MessageUtil.formatTime(LocalDateTime.from(message.getCreationTime()));
            transcripts.add(String.format(
                    "`[%s]` **%s**: %s",
                    timestamp,
                    message.getAuthor().getName(),
                    message.getContentDisplay()
            ));
            transcriptFile.add(String.format(
                    "[%s] %s: %s",
                    timestamp,
                    message.getAuthor().getName(),
                    message.getContentDisplay().replace("\n", "\r\n")
            ));
        }
        Collections.reverse(transcripts);
        Collections.reverse(transcriptFile);

        List<String> transcriptsMsg = new LinkedList<>();
        List<String> messageList = new LinkedList<>();

        for(String message : transcripts){
            if(messageList.stream().mapToInt(String::length).sum() + messageList.size() + message.length() + 1 > 1992){
                transcriptsMsg.add(String.join("\n", messageList));
                messageList.clear();
            }
            messageList.add(message);
        }
        if(messageList.size() > 0){
            transcriptsMsg.add(String.join("\n", messageList));
        }

        StringBuilder builder = new StringBuilder();
        for(String message : transcriptFile){
            builder.append(message).append("\r\n");
        }

        List<User> users = history.getRetrievedHistory().stream().map(Message::getAuthor).filter(user -> !user.isBot())
                .distinct().collect(Collectors.toList());

        InputStream is;
        try {
            is = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
        }catch (Exception ex){
            is = null;
        }

        final InputStream input = is;
        users.stream().filter(user -> !user.isFake()).map(User::openPrivateChannel).map(RestAction::complete)
                .forEach(pm -> {
                    pm.sendMessage(String.format(
                            "The ticket `%s` in the guild `%s` was closed!\n" +
                                    "Here's a transcript of the chat:",
                            ticket.getName(),
                            guild.getName()
                    )).queue();
                    for (String transcriptMsg : transcriptsMsg) {
                        pm.sendMessage(transcriptMsg).queue();
                    }
                    if (input != null) {
                        pm.sendFile(input, String.format(
                                "%s.txt",
                                ticket.getName()
                        )).queue();
                    }
                });

    }

    public static void ticketCreate(TextChannel ticket, Member creator){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        EmbedBuilder logEmbed = EmbedUtil.getEmbed()
                .setTitle(Emotes.CHANNEL_CREATE + " Ticket created")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Opened by:", String.format(
                        "%s | %s#%s",
                        creator.getAsMention(),
                        creator.getUser().getName(),
                        creator.getUser().getDiscriminator()
                ), false);

        logChannel.sendMessage(logEmbed.build()).queue();
    }

    public static void ticketClose(TextChannel ticket, Member closer){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        Member creator = getTicketAuthor(ticket.getGuild(), ticket);

        EmbedBuilder logEmbed = EmbedUtil.getEmbed()
                .setTitle(Emotes.CHANNEL_DELETE + " Ticket closed")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Opened by:", creator == null ? "`Unknown member`" : String.format(
                        "%s | %s#%s",
                        creator.getAsMention(),
                        creator.getUser().getName(),
                        creator.getUser().getDiscriminator()
                ), false)
                .addField("Closed by:", closer == null ? "`Channel deleted manually`" : String.format(
                        "%s | %s#%s",
                        closer.getAsMention(),
                        closer.getUser().getName(),
                        closer.getUser().getDiscriminator()
                ), false);

        logChannel.sendMessage(logEmbed.build()).queue();
    }

    public static void memberAdd(TextChannel ticket, Member executor, Member member){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        EmbedBuilder memberAdd = EmbedUtil.getEmbed()
                .setTitle(Emotes.MEMBER_ADD + " Member added")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Added Member:", String.format(
                        "%s | %s#%s",
                        member.getAsMention(),
                        member.getUser().getName(),
                        member.getUser().getDiscriminator()
                ), false)
                .addField("Added by:", String.format(
                        "%s | %s#%s",
                        executor.getAsMention(),
                        executor.getUser().getName(),
                        executor.getUser().getDiscriminator()
                ), false);

        logChannel.sendMessage(memberAdd.build()).queue();
    }

    public static void roleAdd(TextChannel ticket, Member executor, Role role){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        EmbedBuilder memberAdd = EmbedUtil.getEmbed()
                .setTitle(Emotes.ROLE_ADD + " Role added")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Added Role:", String.format(
                        "%s | %s",
                        role.getAsMention(),
                        role.getName()
                ), false)
                .addField("Added by:", String.format(
                        "%s | %s#%s",
                        executor.getAsMention(),
                        executor.getUser().getName(),
                        executor.getUser().getDiscriminator()
                ), false);

        logChannel.sendMessage(memberAdd.build()).queue();
    }

    public static void memberRemove(TextChannel ticket, Member executor, Member member){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        EmbedBuilder memberAdd = EmbedUtil.getEmbed()
                .setTitle(Emotes.MEMBER_REMOVE + " Member removed")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Removed Member:", String.format(
                        "%s | %s#%s",
                        member.getAsMention(),
                        member.getUser().getName(),
                        member.getUser().getDiscriminator()
                ), false)
                .addField("Removed by:", String.format(
                        "%s | %s#%s",
                        executor.getAsMention(),
                        executor.getUser().getName(),
                        executor.getUser().getDiscriminator()
                ), true);

        logChannel.sendMessage(memberAdd.build()).queue();
    }

    public static void roleRemove(TextChannel ticket, Member executor, Role role){
        if(!Database.hasLogChannel(ticket.getGuild())) return;

        TextChannel logChannel = getLogChannel(ticket.getGuild());

        if(logChannel == null) return;

        EmbedBuilder memberAdd = EmbedUtil.getEmbed()
                .setTitle(Emotes.ROLE_REMOVE + " Role removed")
                .addField("Ticket:", ticket.getName(), false)
                .addField("Removed Role:", String.format(
                        "%s | %s",
                        role.getAsMention(),
                        role.getName()
                ), false)
                .addField("Removed by:", String.format(
                        "%s | %s#%s",
                        executor.getAsMention(),
                        executor.getUser().getName(),
                        executor.getUser().getDiscriminator()
                ), false);

        logChannel.sendMessage(memberAdd.build()).queue();

    }

}
