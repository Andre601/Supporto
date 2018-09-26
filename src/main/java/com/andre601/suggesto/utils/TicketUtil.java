package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import java.text.MessageFormat;
import java.util.List;
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

    public static void createTicket(Guild guild, TextChannel tc, Category category, Member author, String msg){

        Long ticketID = Database.getTicketID(guild);
        Role role = getStaffRole(guild);

        TextChannel support;
        try {
            if (category == null)
                support = (TextChannel) guild.getController().createTextChannel(MessageFormat.format(
                        "Ticket-{0}",
                        ticketID
                )).complete();
            else
                support = (TextChannel) category.createTextChannel(MessageFormat.format(
                        "Ticket-{0}",
                        ticketID
                )).complete();
        }catch (Exception ex){
            tc.sendMessage(MessageFormat.format(
                    "{0} I can't create a ticket.\n" +
                    "Make sure, that I have the Manage channel permission in the guild and/or category!",
                    author.getUser().getAsMention()
            )).queue();
            return;
        }

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
        support.putPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();

        EmbedBuilder ticket = EmbedUtil.getEmbed()
                .setTitle(MessageFormat.format(
                        "Ticket #{0}",
                        ticketID
                ))
                .addField("Creator:", author.getAsMention(), false)
                .addField("Message:", MessageFormat.format(
                        "```\n" +
                        "{0}\n" +
                        "```",
                        msg
                ), false)
                .addField("Close ticket:", MessageFormat.format(
                        "Only the creator of the ticket{0}or users with `manage server` permission can " +
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

    public static void closeTicket(Guild guild, String channelID){
        TextChannel ticket = getTicketChannel(guild, channelID);

        if(ticket == null) return;

        EmbedBuilder info = EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                "Your ticket `{0}` in `{1}` was closed.",
                ticket.getName(),
                guild.getName()
        ));

        MessageHistory history = ticket.getHistory();
        while(history.retrievePast(100).complete().size() > 0);

        List<User> users = history.getRetrievedHistory().stream().map(Message::getAuthor).filter(user -> !user.isBot())
                .distinct().collect(Collectors.toList());

        users.stream().filter(user -> !user.isFake()).map(User::openPrivateChannel).map(RestAction::complete)
                .forEach(pm -> pm.sendMessage(info.build()).queue());

        ticket.delete().queue();
        Database.deleteTicket(channelID);
    }

    public static void removeTicket(String channelID){
        Database.deleteTicket(channelID);
    }
}
