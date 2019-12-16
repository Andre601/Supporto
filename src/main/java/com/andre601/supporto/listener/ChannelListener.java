package com.andre601.supporto.listener;

import com.andre601.supporto.Supporto;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class ChannelListener extends ListenerAdapter{
    
    private Supporto bot;
    public ChannelListener(Supporto bot){
        this.bot = bot;
    }
    
    private boolean memberIsStaff(Member member, Role role){
        if(role == null && !member.hasPermission(Permission.MANAGE_SERVER))
            return false;
        
        return member.getRoles().contains(role) || member.hasPermission(Permission.MANAGE_SERVER);
    }
    
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Guild guild = event.getGuild();
        TextChannel tc = event.getChannel();
        String ticketChannelId = bot.getTicketChannel(guild.getId());
    
        if(ticketChannelId.equalsIgnoreCase("none"))
            return;
    
        if(!tc.getId().equals(ticketChannelId))
            return;
    
        User user = event.getAuthor();
        if(user.isBot())
            return;
    
        String roleId = bot.getStaffRole(guild.getId());
        Role staff = null;
        if(!roleId.equalsIgnoreCase("none"))
            staff = guild.getRoleById(roleId);
    
        Member member = guild.getMember(user);
        if(member == null)
            return;
    
        if(memberIsStaff(member, staff))
            if(!event.getMessage().getContentRaw().toLowerCase().contains("!create"))
                return;
    
        String categoryId = bot.getCategory(guild.getId());
        Category category = null;
        if(!categoryId.equalsIgnoreCase("none"))
            category = guild.getCategoryById(categoryId);
    
        if(category != null){
            if(!guild.getSelfMember().hasPermission(category, Permission.MANAGE_CHANNEL)){
                MessageEmbed embed = bot.getEmbedUtil()
                        .getEmbed()
                        .setColor(0xFF0000)
                        .setDescription(String.format(
                                "Could not create a ticket due to a lack of permissions!\n" +
                                "\n" +
                                "**Required permission**: `Manage Channel`\n" +
                                "**Required in**: `Category %s`\n" +
                                "\n" +
                                "If you are an admin, make sure I have the permission in the mentioned channel/category.",
                                category.getName()
                        ))
                        .build();
    
                tc.sendMessage(embed).queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        }else{
            if(!guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)){
                MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                        .setColor(0xFF0000)
                        .setDescription(
                                "Could not create a ticket due to a lack of permissions!\n" +
                                "\n" +
                                "**Required permission**: `Manage Channel`\n" +
                                "\n" +
                                "If you are an admin, make sure I have the permission in the guild."
                        )
                        .build();
    
                tc.sendMessage(embed).queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
        }
    
        if(!memberIsStaff(member, staff) && bot.getTicketManager().hasTicket(guild, member.getId())){
            MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                    .setColor(0xFF0000)
                    .setDescription(String.format(
                            "You already have an open ticket!\n" +
                            "Please either respond to it, or close it.\n" +
                            "\n" +
                            "**Your Ticket**: %s\n" +
                            "\n" +
                            "If the above mentioned channel reads \"Unknown Channel\", report it to the dev\n" +
                            "of this bot!",
                            bot.getTicketManager().getTicketMention(guild, member.getId())
                    ))
                    .build();
        
            tc.sendMessage(embed).queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
    
        bot.getTicketManager().createTicket(
                event.getMessage().getContentRaw().replace("!create", ""),
                guild,
                member
        );
    }
}
