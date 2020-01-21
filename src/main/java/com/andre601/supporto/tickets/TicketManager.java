package com.andre601.supporto.tickets;

import ch.qos.logback.classic.Logger;
import com.andre601.supporto.Supporto;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

public class TicketManager {

    private Logger logger = (Logger) LoggerFactory.getLogger(TicketManager.class);
    private Supporto bot;

    public TicketManager(Supporto bot){
        this.bot = bot;
    }
    
    public boolean hasTicket(Guild guild, String userId){
        for(TextChannel tc : guild.getTextChannelCache()){
            if(bot.getTicket(tc.getId()) != null && userId.equals(bot.getAuthor(tc.getId())))
                return true;
        }
        
        return false;
    }
    
    public String getTicketMention(Guild guild, String memberId){
        for(TextChannel tc : guild.getTextChannelCache()){
            if(bot.getTicket(tc.getId()) != null && memberId.equals(bot.getAuthor(tc.getId())))
                return tc.getAsMention();
        }
        
        return "Unknown Channel";
    }

    public void createTicket(String msg, Guild guild, Member member){
        Category category = guild.getCategoryById(bot.getCategory(guild.getId()));
        final String reason = msg.length() > 800 ? msg.substring(0, 800) : msg;
        final long ticketId = bot.getDbUtil().getTicketId(guild.getId());
        
        bot.getDbUtil().updateTicketId(guild.getId());

        if(category == null){
            guild.createTextChannel(String.format("ticket-%d", ticketId))
                    .addPermissionOverride(
                            member,
                            Permission.getRaw(
                                    Permission.MESSAGE_WRITE,
                                    Permission.MESSAGE_EMBED_LINKS,
                                    Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_ADD_REACTION,
                                    Permission.MESSAGE_EXT_EMOJI,
                                    Permission.MESSAGE_HISTORY
                            ),
                            0
                    )
                    .addPermissionOverride(
                            guild.getSelfMember(),
                            Permission.getRaw(
                                    Permission.MESSAGE_WRITE,
                                    Permission.MESSAGE_EMBED_LINKS,
                                    Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_ADD_REACTION,
                                    Permission.MESSAGE_EXT_EMOJI,
                                    Permission.MESSAGE_HISTORY,
                                    Permission.MANAGE_CHANNEL,
                                    Permission.MESSAGE_MANAGE
                            ),
                            0
                    )
                    .setTopic(String.format(
                        "Author: %s (%s)\n" +
                        "Reason: %s",
                        member.getEffectiveName(),
                        member.getId(),
                        reason
                    ))
                    .reason(String.format(
                            "Creating ticket for %s",
                            member.getEffectiveName()
                    )).queue(channel -> { 
                        channel.sendMessage(member.getAsMention()).embed(getEmbed(String.format(
                                "ticket-%d", 
                                ticketId
                        ), reason, member)).queue(message -> { 
                            message.pin().queue();
                            message.addReaction("\u2705").queue(); 
                        }); 
                    });
        }else{
            category.createTextChannel(String.format("ticket-%d", ticketId))
                    .addPermissionOverride(
                            member, 
                            Permission.getRaw(
                                    Permission.MESSAGE_WRITE,
                                    Permission.MESSAGE_EMBED_LINKS,
                                    Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_ADD_REACTION,
                                    Permission.MESSAGE_EXT_EMOJI,
                                    Permission.MESSAGE_HISTORY
                            ), 
                            0
                    )
                    .addPermissionOverride(
                            guild.getSelfMember(),
                            Permission.getRaw(
                                    Permission.MESSAGE_WRITE,
                                    Permission.MESSAGE_EMBED_LINKS,
                                    Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_ADD_REACTION,
                                    Permission.MESSAGE_EXT_EMOJI,
                                    Permission.MESSAGE_HISTORY,
                                    Permission.MANAGE_CHANNEL,
                                    Permission.MESSAGE_MANAGE
                            ),
                            0
                    )
                    .setTopic(String.format(
                            "Author: %s (%s)\n" + 
                            "Reason: %s",
                            member.getEffectiveName(),
                            member.getId(),
                            reason
                    ))
                    .reason(String.format(
                            "Creating ticket %d for %s",
                            ticketId,
                            member.getEffectiveName()
                    )).queue(channel -> { 
                        channel.sendMessage(member.getAsMention()).embed(getEmbed(String.format(
                                "ticket-%d", 
                                ticketId
                        ), reason, member)).queue(message -> {
                            message.pin().queue();
                            message.addReaction("\u2705").queue();
                        });
                    });
        }
    }
    
    public void closeTicket(String guildId, TextChannel tc, Member author, @Nullable Member closer){
        String name = tc.getName();
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setDescription(String.format(
                        "Your ticket `%s` in the Guild `%s` was closed by %s",
                        name,
                        tc.getGuild().getName(),
                        closer == null ? "you" : closer.getAsMention()
                ))
                .build();
        
        tc.delete().queue();
        if(bot.isDmEnabled(guildId)){
            author.getUser().openPrivateChannel().queue(pm -> pm.sendMessage(embed).queue(
                    msg -> bot.getLogUtil().sendCloseEmbed(tc, name, author, true, closer)),
                    throwable -> bot.getLogUtil().sendCloseEmbed(tc, name, author, false, closer)
            );
            return;
        }
        
        bot.getLogUtil().sendCloseEmbed(tc, name, author, false, closer);
        
    }
    
    public void addMember(TextChannel tc, Member member, Member executor){
        PermissionOverride permission = tc.getPermissionOverride(member);
        if(permission != null){
            bot.getEmbedUtil().sendError(tc, executor.getUser(), "The provided member is already part of this Ticket.");
            return;
        }
        
        tc.upsertPermissionOverride(member).setAllow(
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.MESSAGE_HISTORY
        ).reason(String.format(
                "Adding member %s to ticket. Requested by %s",
                member.getEffectiveName(),
                executor.getEffectiveName()
        )).queue(s -> {
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(executor.getUser())
                    .setColor(0x00FF00)
                    .setDescription(String.format(
                            "Added %s to the Ticket!",
                            member.getAsMention()
                    ))
                    .build();
    
            tc.sendMessage(embed).queue();
        });
    }
    
    public void removeMember(TextChannel tc, Member member, Member executor){
        Member author = tc.getGuild().getMemberById(bot.getAuthor(tc.getId()));
        if(member.equals(author)){
            bot.getEmbedUtil().sendError(
                    tc,
                    executor.getUser(),
                    "The provided Member is the author of this Ticket.\n" +
                    "You may not remove the Ticket-Author from their Ticket."
            );
            return;
        }
        
        if(member.equals(tc.getGuild().getSelfMember())){
            bot.getEmbedUtil().sendError(
                    tc,
                    executor.getUser(),
                    "The provided member is the Ticket-Bot (Me).\n" +
                    "You may not remove the Ticket-Bot from a ticket."
            );
            return;
        }
        
        PermissionOverride permission = tc.getPermissionOverride(member);
        if(permission == null){
            bot.getEmbedUtil().sendError(tc, executor.getUser(), "The provided Member is not part of this channel!");
            return;
        }
        
        permission.delete().reason(String.format(
                "Removing member %s from ticket. Requested by %s",
                member.getEffectiveName(),
                executor.getEffectiveName()
        )).queue(s -> {
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(executor.getUser())
                    .setColor(0x00FF00)
                    .setDescription(String.format(
                            "Removed %s from the Ticket!",
                            member.getAsMention()
                    ))
                    .build();
    
            tc.sendMessage(embed).queue();
        });
    }

    private MessageEmbed getEmbed(String name, String reason, Member member){
        return new EmbedBuilder()
                .setTitle(name)
                .addField("Creator:", member.getAsMention(), false)
                .addField("Message:", String.format(
                        "```\n" +
                        "%s\n" +
                        "```",
                        reason.length() > MessageEmbed.VALUE_MAX_LENGTH ? reason.substring(0, 1000) + "..." : reason
                ), false)
                .addField(
                        "Closing ticket:",
                        "To close a ticket click on the ✅ reaction and confirm with `>confirm`",
                        false
                ).build();
    }
}
