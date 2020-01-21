package com.andre601.supporto.util;

import com.andre601.supporto.Supporto;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;

public class LogUtil{
    
    private Supporto bot;
    
    public LogUtil(Supporto bot){
        this.bot = bot;
    }
    
    public void sendOpenEmbed(TextChannel tc, String ticket, Member author){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.TICKET_CREATE.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Author:", getMember(author), false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    public void sendCloseEmbed(TextChannel tc, String ticket, Member author, boolean pm, @Nullable Member closer){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.TICKET_CLOSE.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Author:", getMember(author), false)
                .addField("Closed by:", closer == null ? getMember(author) : getMember(closer), false)
                .addField("Send PM to author:", pm ? "Yes" : "No", false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    public void sendMemberAddEmbed(TextChannel tc, String ticket, Member added, Member mod){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.USER_ADD.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Added member:", getMember(added), false)
                .addField("Added by:", getMember(mod), false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    public void sendMemberRemoveEmbed(TextChannel tc, String ticket, Member removed, Member mod){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.USER_REMOVE.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Removed member:", getMember(removed), false)
                .addField("Removed by:", getMember(mod), false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    public void sendRoleAddEmbed(TextChannel tc, String ticket, Role added, Member mod){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.ROLE_ADD.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Added role:", added.getAsMention(), false)
                .addField("Added by:", getMember(mod), false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    public void sendRoleRemoveEmbed(TextChannel tc, String ticket, Role removed, Member mod){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setTitle(Actions.ROLE_REMOVE.getMessage())
                .addField("Ticket:", ticket, false)
                .addField("Removed role:", removed.getAsMention(), false)
                .addField("Removed by:", getMember(mod), false)
                .build();
        
        sendEmbed(tc, embed);
    }
    
    private void sendEmbed(TextChannel tc, MessageEmbed embed){
        tc.sendMessage(embed).queue();
    }
    
    private String getMember(Member member){
        if(member == null)
            return "Unknown user";
        
        return String.format("%s | `%s`", member.getAsMention(), member.getUser().getAsTag());
    }
    
    private enum Actions{
        TICKET_CREATE("<:channelCreate:532305580153503745>", "Ticket created"),
        TICKET_CLOSE ("<:channelDelete:532305580069748737>", "Ticket closed"),
        USER_ADD     ("<:memberAdd:532305579998445599>",     "Member added"),
        USER_REMOVE  ("<:memberRemove:532305580124274734>",  "Member removed"),
        ROLE_ADD     ("<:roleAdd:532305579805245461>",       "Role added"),
        ROLE_REMOVE  ("<:roleRemove:532305580409487380>",    "Role removed");
        
        private String emote;
        private String text;
        
        Actions(String emote, String text){
            this.emote = emote;
            this.text = text;
        }
        
        public String getMessage(){
            return this.emote + " " + this.text;
        }
    }
    
}
