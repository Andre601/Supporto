package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

public class ChannelUtil {

    public static void createTicket(Guild g, User user, Message msg){

        String ticketID = Database.getTicketID(g).toString();

        TextChannel tc = (TextChannel)g.getController().createTextChannel("ticket-" + ticketID).complete();

        if(Database.hasCategory(g)){
            Category category = g.getCategoryById(Database.getCategory(g));
            tc.getManager().setParent(category).queue();
        }

        g.getTextChannelById(tc.getId()).createPermissionOverride(g.getMember(user)).setAllow(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_EMBED_LINKS
        ).queue();

        EmbedBuilder ticket = EmbedUtil.getEmbed(user)
                .setTitle("ticket #" + ticketID)
                .setDescription(msg.getContentRaw());

        tc.sendMessage(user.getAsMention()).queue(message -> {
            message.editMessage(ticket.build()).queue();
            message.addReaction("✅").queue();
            message.pin().queue();
            Database.saveTicket(g, message.getId(), tc.getId(), user.getId());
        });
    }
}
