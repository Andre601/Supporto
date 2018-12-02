package com.andre601.suggesto.listener;

import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.TicketUtil;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.PermUtil;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class ChannelListener extends ListenerAdapter {

    private TextChannel getSupportChannel(Guild guild){
        TextChannel tc;
        try{
            tc = guild.getTextChannelById(Database.getSupportChannel(guild));
        }catch (Exception ex){
            tc = null;
        }

        return tc;
    }

    private Category getSupportCategory(Guild guild){
        Category category;
        try{
            category = guild.getCategoryById(Database.getCategory(guild));
        }catch (Exception ex){
            category = null;
        }

        return category;
    }

    private Role getStaffRole(Guild guild){
        Role role;
        try{
            role = guild.getRoleById(Database.getRoleID(guild));
        }catch (Exception ex){
            role = null;
        }

        return role;
    }

    public void onMessageReceived(MessageReceivedEvent event){
        Guild guild = event.getGuild();
        TextChannel tc = event.getTextChannel();
        TextChannel support = getSupportChannel(guild);
        Message msg = event.getMessage();
        Role role = getStaffRole(guild);
        Member member = event.getMember();

        if(support == null) return;
        if(tc != support) return;
        if(PermUtil.isBot(msg)) return;
        if(PermUtil.isSelf(msg)) return;
        if(role != null)
            if(PermUtil.isStaff(member, role))
                if(!msg.getContentRaw().contains("!create")) return;
        if(PermUtil.isAdmin(tc, member))
            if (!msg.getContentRaw().contains("!create")) return;
        if(!PermUtil.canManageChannels(tc)) {
            EmbedUtil.sendError(msg,
                    "I can't create a ticket due to a lack of permissions!\n" +
                    "I need `manage channel` permissions."
            );
            return;
        }

        for(TextChannel textChannel : guild.getTextChannels()){
            if(role != null)
                if(PermUtil.isStaff(member, role)) break;

            if(PermUtil.isAdmin(textChannel, member)) break;

            if(Database.hasTicket(textChannel.getId())){
                if(msg.getMember().getUser().getId().equals(Database.getAuthorID(textChannel.getId()))){
                    tc.sendMessage(String.format(
                            "%s You already have a ticket created.\n" +
                            "Please go to %s and answer or close it!",
                            msg.getAuthor().getAsMention(),
                            textChannel.getAsMention()
                    )).queue(message -> {
                        msg.delete().queue();
                        message.delete().queueAfter(10, TimeUnit.SECONDS);
                    });
                    return;
                }
            }
        }

        String raw = msg.getContentRaw();
        Category category = getSupportCategory(guild);

        TicketUtil.createTicket(guild, tc, category, msg.getMember(), raw.replace("!create", ""));
        if (PermUtil.canManageMsg(tc))
            msg.delete().queue();

    }

    public void onTextChannelDelete(TextChannelDeleteEvent event){
        Guild guild = event.getGuild();
        TextChannel tc = event.getChannel();
        TextChannel support = getSupportChannel(guild);

        if(support == null) return;
        if(tc == support){
            Database.setSupportChannel(guild, "none");
            return;
        }
        if(Database.hasTicket(tc.getId()))
            TicketUtil.removeTicket(tc.getId());
    }

    public void onCategoryDelete(CategoryDeleteEvent event){
        Category category = event.getCategory();
        Category supportCategory = getSupportCategory(event.getGuild());

        if(supportCategory == null) return;
        if(category != supportCategory) return;

        Database.setCategory(event.getGuild(), "none");
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        Guild guild = event.getGuild();
        String messageID = event.getMessageId();
        TextChannel tc = event.getChannel();
        Role role = getStaffRole(guild);

        if(!Database.hasTicket(tc.getId())) return;
        if(!event.getReactionEmote().getName().equals("✅")) return;
        if(event.getUser().isBot()) return;

        Member member = event.getMember();
        if(!messageID.equals(Database.getMessageID(tc.getId()))) return;
        if(member.getUser().getId().equals(Database.getAuthorID(tc.getId()))) {
            TicketUtil.performClose(guild, member.getUser(), tc.getId());
            return;
        }
        if(PermUtil.isAdmin(tc, member)){
            TicketUtil.performClose(guild, member.getUser(), tc.getId());
            return;
        }
        if(role != null)
            if(PermUtil.isStaff(member, role))
                TicketUtil.performClose(guild, member.getUser(), tc.getId());
    }

}
