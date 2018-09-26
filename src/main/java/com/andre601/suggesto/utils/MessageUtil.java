package com.andre601.suggesto.utils;

import com.andre601.suggesto.listener.ReadyListener;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    private static DateTimeFormatter date = DateTimeFormatter.ofPattern("dd. MMM yyyy hh:mm:ss");

    public static String getTag(User user){
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String getName(Member member) {
        if(member.getNickname() == null)
            return MessageFormat.format(
                    "`{0}`",
                    getTag(member.getUser()).replace("`", "'")
            );

        return MessageFormat.format(
                "`{0}` (`{1}`)",
                member.getNickname().replace("`", "'"),
                getTag(member.getUser()).replace("`", "'")
        );
    }

    public static Runnable updatePresence(){
        return () -> {
            if(ReadyListener.getReady() == Boolean.TRUE){
                ShardManager shardManager = ReadyListener.getShards();
                shardManager.setGame(Game.watching(MessageFormat.format(
                        "{0} created tickets | {1} guilds",
                        Database.getTotalTickets(),
                        shardManager.getGuildCache().size()
                )));
            }
        };
    }

    public static String formatTime(LocalDateTime localDateTime){
        LocalDateTime time = LocalDateTime.from(localDateTime.atOffset(ZoneOffset.UTC));
        return time.format(date) + " UTC";
    }

}
