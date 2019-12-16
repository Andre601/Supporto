package com.andre601.supporto.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.LocalDateTime;

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
    
}
