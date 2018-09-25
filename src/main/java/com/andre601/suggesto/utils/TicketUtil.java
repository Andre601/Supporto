package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;

public class TicketUtil {

    public static void createTicket(Guild guild, TextChannel tc, Category category, Member author, String msg){

        Long ticketID = Database.getTicketID(guild);

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
                .addField("Creator:", MessageFormat.format(
                        "{0} (`{1}`)",
                        MessageUtil.getName(author),
                        author.getUser().getId()
                ), false)
                .addField("Message:", MessageFormat.format(
                        "```\n" +
                        "{0}\n" +
                        "```",
                        msg
                ), false)
                .addField("Close ticket:", MessageFormat.format(
                        "Only the creator of the ticket ({0}) or users with `manage server` permission can " +
                        "close this ticket.\n" +
                        "To close a ticket, click on the ✅ reaction of this message!",
                        MessageUtil.getTag(author.getUser())
                ),false);

        support.sendMessage(author.getAsMention()).embed(ticket.build()).queue(message -> {
            message.pin().queue();
            message.addReaction("✅").queue();
            Database.saveTicket(support.getId(), message.getId(), author.getUser().getId(), ticketID);
        });
    }

    public static void closeTicket(Guild guild, String channelID){
        guild.getTextChannelById(channelID).delete().queue();
        Database.deleteTicket(channelID);
    }

    public static void removeTicket(String channelID){
        Database.deleteTicket(channelID);
    }
}
