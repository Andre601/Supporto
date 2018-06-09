package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.entities.User;

public class MessageUtil {

    public static String getTag(User user){
        return user.getName() + "#" + user.getDiscriminator();
    }

}
