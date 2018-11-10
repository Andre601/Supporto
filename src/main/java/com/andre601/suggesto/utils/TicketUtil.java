package com.andre601.suggesto.utils;

import com.andre601.suggesto.Supporto;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TicketUtil {

    private static Role getStaffRole(Guild guild){
        Role role;
        try{
            role = guild.getRoleById(Database.getRoleID(guild));
        }catch (Exception ex){
            role = null;
        }

        return role;
    }

    private static TextChannel getTicketChannel(Guild guild, String channelID){
        TextChannel channel;
        try{
            channel = guild.getTextChannelById(channelID);
        }catch (Exception ex){
            channel = null;
        }

        return channel;
    }

    private static boolean isMessage(Message msg){
        return msg.getContentRaw().equalsIgnoreCase(">confirm");
    }

    private static Map<User, String> queued = new HashMap<>();

    public static void createTicket(Guild guild, TextChannel tc, Category category, Member author, String msg){

        TextChannel support;

        if(category != null){
            if(!PermUtil.canManagePerms(category) || !PermUtil.canManageChannels(category)){
                tc.sendMessage(String.format(
                        "%s I can't create a ticket.\n" +
                        "Make sure, that I have the `manage channels` and `manage permission` permission in the " +
                        "guild and category!",
                        author.getUser().getAsMention()
                )).queue();
                return;
            }

            Long ticketID = Database.getNewTicketID(guild);
            Role role = getStaffRole(guild);
            support = (TextChannel) category.createTextChannel(String.format(
                    "Ticket-%s",
                    ticketID
            )).reason(String.format(
                    "Create ticket #%s",
                    ticketID
            )).complete();
            support.createPermissionOverride(author).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();
            support.createPermissionOverride(guild.getSelfMember()).setAllow(
                    Permission.MESSAGE_MANAGE,
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_ROLES,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_ADD_REACTION
            ).queue();
            support.putPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();

            EmbedBuilder ticket = EmbedUtil.getEmbed()
                    .setTitle(String.format(
                            "Ticket #%s",
                            ticketID
                    ))
                    .addField("Creator:", author.getAsMention(), false)
                    .addField("Message:", String.format(
                            "```\n" +
                            "%s\n" +
                            "```",
                            msg
                    ), false)
                    .addField("Close ticket:", String.format(
                            "Only the creator of the ticket%sor users with `manage server` permission can " +
                            "close this ticket.\n" +
                            "To close a ticket, click on the ✅ reaction of this message!",
                            (role == null ? " " : ", users with " + role.getAsMention() + " ")
                    ),false);

            support.sendMessage(author.getAsMention()).embed(ticket.build()).queue(message -> {
                message.pin().queue();
                message.addReaction("✅").queue();
                Database.saveTicket(support.getId(), message.getId(), author.getUser().getId(), ticketID);
            });
        }else{
            if(!(PermUtil.canManagePerms(guild) && PermUtil.canManageChannels(guild))){
                tc.sendMessage(String.format(
                        "%s I can't create a ticket.\n" +
                        "Make sure, that I have the `manage channels` and `manage permission` permission in the " +
                        "guild and category!",
                        author.getUser().getAsMention()
                )).queue();
                return;
            }

            Long ticketID = Database.getNewTicketID(guild);
            Role role = getStaffRole(guild);
            support = (TextChannel) guild.getController().createTextChannel(String.format(
                    "Ticket-%s",
                    ticketID
            )).reason(String.format(
                    "Create ticket #%s",
                    ticketID
            )).complete();
            support.createPermissionOverride(author).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();
            support.createPermissionOverride(guild.getSelfMember()).setAllow(
                    Permission.MESSAGE_MANAGE,
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_ROLES,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_ADD_REACTION
            ).queue();
            support.putPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();

            EmbedBuilder ticket = EmbedUtil.getEmbed()
                    .setTitle(String.format(
                            "Ticket #%s",
                            ticketID
                    ))
                    .addField("Creator:", author.getAsMention(), false)
                    .addField("Message:", String.format(
                            "```\n" +
                            "%s\n" +
                            "```",
                            msg
                    ), false)
                    .addField("Close ticket:", String.format(
                            "Only the creator of the ticket%sor users with `manage server` permission can " +
                            "close this ticket.\n" +
                            "To close a ticket, click on the ✅ reaction of this message!",
                            (role == null ? " " : ", users with " + role.getAsMention() + " ")
                    ),false);

            support.sendMessage(author.getAsMention()).embed(ticket.build()).queue(message -> {
                message.pin().queue();
                message.addReaction("✅").queue();
                Database.saveTicket(support.getId(), message.getId(), author.getUser().getId(), ticketID);
            });
        }
    }

    public static void performClose(Guild guild, User closer, String channelID){
        TextChannel ticket = getTicketChannel(guild, channelID);

        if(ticket == null) return;

        if(queued.containsKey(closer) && queued.get(closer).equals(ticket.getId())){
            ticket.sendMessage(String.format(
                    "%s You already requested a closing of this ticket!\n" +
                    "Please confirm your request!",
                    closer.getAsMention()
            )).queue();
            return;
        }

        queued.put(closer, ticket.getId());

        ticket.sendMessage(String.format(
                "%s Are you sure, you want to close this ticket?\n" +
                "Type `>confirm` to confirm this action.\n" +
                "**This action will be cancelled in 30 seconds...**",
                closer.getAsMention()
        )).queue(message -> {
            EventWaiter waiter = Supporto.waiter;
            waiter.waitForEvent(
                    MessageReceivedEvent.class,
                    ev -> (isMessage(ev.getMessage()) &&
                            ev.getTextChannel().equals(ticket) &&
                            (ev.getAuthor() != ev.getJDA().getSelfUser() ||
                            ev.getAuthor() != message.getAuthor()) &&
                            ev.getAuthor() == closer
                    ),
                    ev -> {
                        closeTicket(guild, ticket, closer);
                        queued.remove(closer);
                    },
                    30, TimeUnit.SECONDS,
                    () -> {
                        try{
                            message.delete().queue();
                        }catch (Exception ex){
                            Supporto.getLogger().error("Couldn't delete a own message :-/");
                        }

                        queued.remove(closer);
                        ticket.sendMessage("The close-request timed out!").queue();
                    }
            );
        });
    }

    public static void closeTicket(Guild guild, TextChannel ticket, User closer){

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
                    message.getContentDisplay()
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
            builder.append(message).append("\n");
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
                    for(String transcriptMsg : transcriptsMsg){
                        pm.sendMessage(transcriptMsg).queue();
                    }
                    if(input != null){
                        pm.sendFile(input, String.format(
                                "%s.txt",
                                ticket.getName()
                        )).queue();
                    }
                });

        ticket.delete().reason(MessageFormat.format(
                "Ticket closed by {0}",
                closer.getName()
        )).queue();
        Database.deleteTicket(ticket.getId());
    }

    public static void removeTicket(String channelID){
        Database.deleteTicket(channelID);
    }
}
