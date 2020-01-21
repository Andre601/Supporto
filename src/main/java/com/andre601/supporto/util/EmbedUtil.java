package com.andre601.supporto.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class EmbedUtil{
    
    public EmbedUtil(){}
    
    public EmbedBuilder getEmbed(){
        return new EmbedBuilder().setTimestamp(LocalDateTime.now()).setColor(0x3498db);
    }
    
    public EmbedBuilder getEmbed(User author){
        return getEmbed().setFooter(String.format(
                "Requested by: %s",
                author.getAsTag()
        ), author.getEffectiveAvatarUrl());
    }
    
    public void sendError(TextChannel tc, User author, String msg){
        sendError(tc, author, msg, 0);
    }
    
    public void sendError(TextChannel tc, User author, String msg, int deleteAfter){
        EmbedBuilder embed = getEmbed(author)
                .setColor(0xFF0000)
                .setDescription(msg);
        
        tc.sendMessage(embed.build()).queue(del -> {
            if(deleteAfter > 0)
                del.delete().queueAfter(deleteAfter, TimeUnit.SECONDS);
        });
    }
    
    public void sendPermissionError(TextChannel tc, User author, Permission permission, @Nullable Category category){
        sendError(tc, author, String.format(
                "Could not perform an action due to a lack of permissions.\n" + 
                "\n" +
                "**Required permission**: `%s`\n" +
                "**Required in**: `%s`",
                permission.getName(),
                category == null ? "Guild" : "Category " + category.getName()
        ), 10);
    }
    
}
